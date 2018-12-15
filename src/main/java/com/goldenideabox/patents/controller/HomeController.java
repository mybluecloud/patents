package com.goldenideabox.patents.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldenideabox.patents.common.FileUploadHelper;
import com.goldenideabox.patents.dao.AreaInfoMapper;
import com.goldenideabox.patents.dao.DocumentMapper;
import com.goldenideabox.patents.model.AreaInfo;
import com.goldenideabox.patents.model.Document;
import com.goldenideabox.patents.model.Document.DocumentParseStatus;
import com.goldenideabox.patents.model.Document.DocumentSearchStatus;
import com.goldenideabox.patents.model.GooglePatent;
import com.goldenideabox.patents.model.QueryHistory;
import com.goldenideabox.patents.service.DocumentWordStatService;
import com.goldenideabox.patents.service.GooglePatentsService;
import com.goldenideabox.patents.service.QueryHistoryService;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
public class HomeController {

    @Value("${goldenideabox.path.pdf}")
    private String pdf;

    @Value("${goldenideabox.path.temp}")
    private String excelPath;


    @Autowired
    private GooglePatentsService googlePatentsService;

    @Autowired
    private DocumentWordStatService documentWordStatService;

    @Autowired
    private QueryHistoryService queryHistoryService;


    @Autowired
    private AreaInfoMapper areaInfoMapper;

    @ResponseBody
    @RequestMapping(value = "/searchFile")
    public Object searchFile(@RequestParam("files[]") MultipartFile file) {

        FileUploadHelper.upload(file, pdf);
        Map<Object, Object> info = new HashMap<Object, Object>();

        QueryHistory queryHistory = new QueryHistory();
        queryHistory.setName(file.getOriginalFilename());
        queryHistory.setType(1);
        queryHistory.setStatus(0);

        queryHistoryService.addQueryHistory(queryHistory);
        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setPath(pdf + file.getOriginalFilename());
        document.setType(0);
        document.setParsestatus(DocumentParseStatus.INIT);
        document.setSearchstatus(DocumentSearchStatus.INIT);
        documentWordStatService.addDocument(document);

        documentWordStatService.parsesDocumentWord(document);

        documentWordStatService.calcTFDIF();

        List<String> words = documentWordStatService.getDocumentWord(document);

        info.put("words", words);
        if (words == null || words.size() == 0) {
            queryHistory.setStatus(2);
            queryHistoryService.updateQueryHistory(queryHistory);
            info.put("status", 1);

            return info;
        }

        List<GooglePatent> lst = googlePatentsService.searchGooglePatentsByKey(StringUtils.join(words.toArray(), " "), queryHistory);
        info.put("patents", lst);
        info.put("status", 0);
        info.put("id", queryHistory.getId());
        document.setParsestatus(DocumentParseStatus.PARSED);
        document.setSearchstatus(DocumentSearchStatus.SEARCHED);
        documentWordStatService.updateDocument(document);
        queryHistoryService.updateQueryHistory(queryHistory);

        return info;
    }


    @ResponseBody
    @RequestMapping(value = {"/search"})
    public Object search(@RequestParam(required = true) String queryKey) {
        Map<Object, Object> info = new HashMap<Object, Object>();
        QueryHistory queryHistory = new QueryHistory();
        queryHistory.setName(queryKey);
        queryHistory.setType(0);
        queryHistory.setStatus(0);

        queryHistoryService.addQueryHistory(queryHistory);
        List<GooglePatent> lst = googlePatentsService.searchGooglePatentsByKey(queryKey, queryHistory);
        queryHistoryService.updateQueryHistory(queryHistory);
        info.put("patents", lst);
        info.put("id", queryHistory.getId());
        return info;
    }

    @ResponseBody
    @RequestMapping(value = {"/areaInfo"})
    public Object areaInfo(@RequestParam(required = true) int pid) {
        Map<Object, Object> info = new HashMap<Object, Object>();

        List<AreaInfo> lst = areaInfoMapper.selectByPid(pid);
        info.put("areas", lst);
        return info;
    }

    @RequestMapping(value = {"/inventor"})
    public String inventor(Model model, @RequestParam(required = true) String name) {


        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpclient = httpClientBuilder.build();
        String content,token,query;
        try {
           /// query = "in:("+name+")";
            openPatexplorer(httpclient);
            token = queryPatexplorerToken(httpclient, name);
            content = queryPatexplorerTotalNum(httpclient, name,token);
            JSONObject jsonObject = JSON.parseObject(content);
            model.addAttribute("content",jsonObject);
            model.addAttribute("query",name);

            httpclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "inventor";
    }


    @RequestMapping(value = {"/analyze"})
    public String analyze(Model model, @RequestParam(required = true) int province,
        @RequestParam(required = true) int city,
        @RequestParam(required = true) int county) {
        String query = "";
        if (province != 0 ) {
            query = areaInfoMapper.selectById(province).getExtName();
        }

        if (city != 0 ) {
            String name = areaInfoMapper.selectById(city).getExtName();
            if (!name.equalsIgnoreCase("市辖区")) {
                query += name;
            }
        }

        if (county != 0 ) {
            query += areaInfoMapper.selectById(county).getExtName();
        }

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpclient = httpClientBuilder.build();
        String total,content,token;
        try {
            openPatexplorer(httpclient);
            token = queryPatexplorerToken(httpclient, query);
            content = queryPatexplorerTotalNum(httpclient, query,token);
            JSONObject jsonObject = JSON.parseObject(content);
            total = jsonObject.getJSONObject("cubePatentSearchResponse").getString("total_hits");
            model.addAttribute("content",jsonObject);

            model.addAttribute("total",total);
            model.addAttribute("query",query);
            //申请人
            String pa = queryPatexplorerPropInfo(httpclient,query,total,"pa","");
            model.addAttribute("pa",pa);
            //申请日
            String ad = queryPatexplorerPropInfo(httpclient,query,total,"ad","");
            model.addAttribute("ad",ad);
            //公开日
            String pd = queryPatexplorerPropInfo(httpclient,query,total,"pd","");
            model.addAttribute("pd",pd);
            //授权日
            String apd = queryPatexplorerPropInfo(httpclient,query,total,"apd","");
            model.addAttribute("apd",apd);
            //分类号（大类）
            String ic12 = queryPatexplorerPropInfo(httpclient,query,total,"ic1","i2/");
            model.addAttribute("ic12",ic12);
            //分类号（小类）
            String ic13 = queryPatexplorerPropInfo(httpclient,query,total,"ic1","i3/");
            model.addAttribute("ic13",ic13);
            //分类号（大组）
            String ic14 = queryPatexplorerPropInfo(httpclient,query,total,"ic1","i4/");
            model.addAttribute("ic14",ic14);
            //分类号（小组）
            String ic15 = queryPatexplorerPropInfo(httpclient,query,total,"ic1","i5/");
            model.addAttribute("ic15",ic15);

            httpclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "analyze";
    }

    private String queryPatexplorerPropInfo(CloseableHttpClient httpclient,String query, String total, String code,String prefix)
        throws Exception {

        HttpPost httppost = new HttpPost("https://www.patexplorer.com/results/getAjaxFilter" );

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("sc", ""));
        formparams.add(new BasicNameValuePair("q", query));
        formparams.add(new BasicNameValuePair("code", code));
        formparams.add(new BasicNameValuePair("pageSize", total));

        if (prefix != null && prefix.length() > 0) {
            formparams.add(new BasicNameValuePair("prefix", prefix));
        }

        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

        httppost.setEntity(uefEntity);
        httppost.addHeader("Accept","*/*");
        httppost.addHeader("Accept-Encoding","gzip, deflate, br");
        httppost.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        httppost.addHeader("Cache-Control","no-cache");
        httppost.addHeader("Connection","keep-alive");

        httppost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        httppost.addHeader("Host","www.patexplorer.com");
        httppost.addHeader("Pragma","no-cache");
        httppost.addHeader("Origin","https://www.patexplorer.com");
        httppost.addHeader("X-Requested-With","XMLHttpRequest");
        httppost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");

        CloseableHttpResponse httpresponse = httpclient.execute(httppost);
        HttpEntity entity = httpresponse.getEntity();
        String info = EntityUtils.toString(entity);
        return info;
    }

    private String queryPatexplorerToken(CloseableHttpClient httpclient, String query) throws Exception {

        HttpPost httppost = new HttpPost("https://www.patexplorer.com/results/s.html?sc=&q=" + URLEncoder.encode(query, "utf-8") + "&fq=lsn1%3A%28%E6%9C%89%E6%9D%83%29&type=s");

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("sc", ""));
        formparams.add(new BasicNameValuePair("q", query));
        formparams.add(new BasicNameValuePair("type", "s"));

        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

        httppost.setEntity(uefEntity);
        httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httppost.addHeader("Accept-Encoding", "gzip, deflate, br");
        httppost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httppost.addHeader("Cache-Control", "no-cache");
        httppost.addHeader("Connection", "keep-alive");
        httppost.addHeader("Host", "www.patexplorer.com");
        httppost.addHeader("Pragma", "no-cache");
        httppost.addHeader("Referer", "https://www.patexplorer.com/");
        httppost.addHeader("Upgrade-Insecure-Requests", "1");
        httppost.addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");

        CloseableHttpResponse httpresponse = httpclient.execute(httppost);

        HttpEntity entity = httpresponse.getEntity();
        String body = EntityUtils.toString(entity);
        
        String token = body.substring(body.lastIndexOf("window.token = '") + 16, body.indexOf("';", body.indexOf("window.token = '")));

        String bToken = Base64.getEncoder().encodeToString(token.getBytes("utf-8"));

        httpresponse.close();

        return bToken;
    }

    private String queryPatexplorerTotalNum(CloseableHttpClient httpclient, String query, String token) throws Exception {
        HttpPost httppost = new HttpPost("https://www.patexplorer.com/results/list/" + token);



        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("sc", ""));
        formparams.add(new BasicNameValuePair("q", query));
        formparams.add(new BasicNameValuePair("type", "s"));
        formparams.add(new BasicNameValuePair("sort", ""));
        formparams.add(new BasicNameValuePair("sortField", ""));
        formparams.add(new BasicNameValuePair("fq", ""));
        formparams.add(new BasicNameValuePair("pageSize", "10"));
        formparams.add(new BasicNameValuePair("pageIndex", "1"));
        formparams.add(new BasicNameValuePair("merge", "no-merge"));
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

        httppost.setEntity(uefEntity);
        httppost.addHeader("Accept","*/*");
        httppost.addHeader("Accept-Encoding","gzip, deflate, br");
        httppost.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        httppost.addHeader("Cache-Control","no-cache");
        httppost.addHeader("Connection","keep-alive");

        httppost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        httppost.addHeader("Host","www.patexplorer.com");
        httppost.addHeader("Pragma","no-cache");
        httppost.addHeader("Origin","https://www.patexplorer.com");
        httppost.addHeader("X-Requested-With","XMLHttpRequest");
        httppost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");

        CloseableHttpResponse httpresponse = httpclient.execute(httppost);

        HttpEntity entity = httpresponse.getEntity();
        String content = EntityUtils.toString(entity);
        httpresponse.close();

        return content;
    }

    private void openPatexplorer(CloseableHttpClient httpclient) throws Exception {
        HttpGet httpGet = new HttpGet("https://www.patexplorer.com/");
        httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Cache-Control", "no-cache");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Host", "www.patexplorer.com");
        httpGet.addHeader("Pragma", "no-cache");
        httpGet.addHeader("Upgrade-Insecure-Requests", "1");
        httpGet.addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        CloseableHttpResponse httpresponse = httpclient.execute(httpGet);
        if (httpresponse.getStatusLine().getStatusCode() != 200) {
            throw new Exception();
        }
        httpresponse.close();

        HttpPost httppost = new HttpPost("https://www.patexplorer.com/search");
        httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httppost.addHeader("Accept-Encoding", "gzip, deflate, br");
        httppost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httppost.addHeader("Cache-Control", "no-cache");
        httppost.addHeader("Connection", "keep-alive");

        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httppost.addHeader("Host", "www.patexplorer.com");
        httppost.addHeader("Pragma", "no-cache");
        httppost.addHeader("Origin", "https://www.patexplorer.com");
        httppost.addHeader("Upgrade-Insecure-Requests", "1");
        httppost.addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");

        httpresponse = httpclient.execute(httppost);
        httpresponse.close();
    }

    @ResponseBody
    @RequestMapping(value = {"/delRecord"})
    public Object delRecord(@RequestParam(required = true) int id) {
        Map<Object, Object> info = new HashMap<Object, Object>();

        queryHistoryService.deleteQueryHistory(id);

        return info;
    }

    @ResponseBody
    @RequestMapping(value = "/export", produces = "application/json;charset=UTF-8")
    public String export(@RequestParam(required = true) int id) {

        List<GooglePatent> lst = googlePatentsService.getGooglePatentByQueryID(id);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");
        String fileName = dateFormat.format(new Date());

        Workbook wb = new XSSFWorkbook();
        try {

            Sheet sheet = wb.createSheet(fileName);

            Row row = sheet.createRow(0);

            CellStyle style = wb.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(IndexedColors.BLUE.getIndex());
            style.setBorderTop(BorderStyle.MEDIUM_DASHED);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setAlignment(HorizontalAlignment.CENTER);
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);

            setExcelHeader(sheet, row, style, "标题", 0, true);
            setExcelHeader(sheet, row, style, "公开号", 1, true);
            setExcelHeader(sheet, row, style, "专利申请号", 2, true);
            setExcelHeader(sheet, row, style, "申请日期", 3, true);
            setExcelHeader(sheet, row, style, "发明人", 4, true);
            setExcelHeader(sheet, row, style, "摘要", 5, true);

            int i = 1;
            for (GooglePatent patent : lst) {
                row = sheet.createRow(i);

                setExcelHeader(sheet, row, null, patent.getTitle(), 0, false);
                setExcelHeader(sheet, row, null, patent.getPublicationNumber(), 1, false);
                setExcelHeader(sheet, row, null, patent.getApplicationNumber(), 2, false);
                setExcelHeader(sheet, row, null, patent.getFilingDate(), 3, false);
                setExcelHeader(sheet, row, null, patent.getInventors(), 4, false);
                setExcelHeader(sheet, row, null, patent.getSummary(), 5, false);

                i++;
            }

            OutputStream fileOut = new FileOutputStream(excelPath + fileName + ".xlsx");
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSONObject.toJSONString("/temp/" + fileName + ".xlsx");
    }

    private void setExcelHeader(Sheet sheet, Row row, CellStyle style, String name, int column, boolean auto) {

        Cell cell = row.createCell(column);
        cell.setCellValue(name);
        if (style != null) {
            cell.setCellStyle(style);
        }

        if (auto) {
            int length = cell.getStringCellValue().getBytes().length;
            int columnWidth = sheet.getColumnWidth(column) / 256;
            if (columnWidth < length + 1) {
                columnWidth = length + 1;
            }
            sheet.autoSizeColumn(column);
            sheet.setColumnWidth(column, columnWidth * 256);
        }

    }

    @RequestMapping(value = {"/"})
    public String usersPage(Model model) {
        List<QueryHistory> lst = queryHistoryService.getAllQueryHistory();

        List<AreaInfo> provinces = areaInfoMapper.selectByDeep(0);
        model.addAttribute("provinces", provinces);

//        List<AreaInfo> cities = areaInfoMapper.selectByDeep(1);
//        model.addAttribute("cities", cities);
//
//        List<AreaInfo> counties = areaInfoMapper.selectByDeep(2);
//        model.addAttribute("counties", counties);

        model.addAttribute("histories", lst);
        return "index";
    }

    @RequestMapping(value = {"/result"})
    public String test(Model model, @RequestParam(required = true) int id) {
        QueryHistory history = queryHistoryService.getQueryHistory(id);
        List<GooglePatent> lst = googlePatentsService.getGooglePatentByQueryID(id);

        model.addAttribute("history", history);
        model.addAttribute("patents", lst);
        return "result";
    }


}
