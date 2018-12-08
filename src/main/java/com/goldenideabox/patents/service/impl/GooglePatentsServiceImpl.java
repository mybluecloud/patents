package com.goldenideabox.patents.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goldenideabox.patents.dao.DocumentMapper;
import com.goldenideabox.patents.dao.GooglePatentMapper;
import com.goldenideabox.patents.dao.QueryHistoryMapper;
import com.goldenideabox.patents.model.Document.DocumentParseStatus;
import com.goldenideabox.patents.model.Document.DocumentSearchStatus;
import com.goldenideabox.patents.model.GooglePatent;
import com.goldenideabox.patents.model.QueryHistory;
import com.goldenideabox.patents.service.GooglePatentsService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service("googlePatentsService")
public class GooglePatentsServiceImpl implements GooglePatentsService {

    @Value("${google.searchUrl}")
    private String searchUrl;

    @Value("${google.searchHead}")
    private String searchHead;

    @Value("${http.proxyHost}")
    private String proxyHost;

    @Value("${http.proxyPort}")
    private String proxyPort;

    @Value("${goldenideabox.path.pdf}")
    private String pdf;

    @Autowired
    private GooglePatentMapper googlePatentMapper;

    @Autowired
    private DocumentMapper documentMapper;



    @Override
    public List<GooglePatent> searchGooglePatentsByKey(String key,QueryHistory queryHistory) {
        trustEveryone();
        List<GooglePatent> patents = new ArrayList<>();
        try {
            List<String> patentsUrl = getWebContentByKey(key);
            for (String url : patentsUrl) {

                GooglePatent patent = getPatentContent(url, key);

                patent.setQueryId(queryHistory.getId());

                googlePatentMapper.insert(patent);
                patents.add(patent);

            }
            queryHistory.setQueryKey(key);
            queryHistory.setStatus(1);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return patents;
    }

    @Override
    public List<GooglePatent> getGooglePatentByQueryID(int id) {
        return googlePatentMapper.getGooglePatentByQueryID(id);
    }


    private synchronized List<String> getWebContentByKey(String key) throws IOException {

        List<String> urls = new ArrayList<>();

        String url = String.format(searchUrl, URLEncoder.encode(key, "utf-8"));


        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        if (conn.getResponseCode() != 200) {
            throw new MalformedURLException();
        }

        InputStream in = conn.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        String json = baos.toString();
        baos.close();
        in.close();
        JSONObject jsonObject = JSON.parseObject(json);

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.size(); i++) {
            urls.add(jsonArray.getJSONObject(i).getString("link"));
        }

        return urls;
    }

    private synchronized GooglePatent getPatentContent(String url, String key) throws IOException {
        GooglePatent patent = new GooglePatent();

        url = url.replace("http://", "https://");

        Document doc = HttpConnection.connect(url).header("User-Agent", searchHead)
            .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("accept-encoding","gzip, deflate, br")
            .header("accept-language","zh-CN,zh;q=0.9")
            .header("upgrade-insecure-requests","1")
            .get();

        Elements lst = null;

        lst = doc.select("span[itemprop=\"title\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setTitle(lst.first().text());
        }

        lst = doc.getElementsByClass("abstract");
        if (lst != null && !lst.isEmpty()) {
            patent.setSummary(lst.first().text());
        }

        lst = doc.select("dd[itemprop=\"publicationNumber\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setPublicationNumber(lst.first().text());
        }

        lst = doc.select("dd[itemprop=\"applicationNumber\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setApplicationNumber(lst.first().text());
        }

        lst = doc.select("dd[itemprop=\"directAssociations\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setAlsoPublishedNumber(lst.first().text());
        }

        lst = doc.select("dd[itemprop=\"assigneeCurrent\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setAssigneeCurrent(lst.first().text());
        }

        lst = doc.select("dd[itemprop=\"assigneeOriginal\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setAssigneeOriginal(lst.first().text());
        }

        lst = doc.select("span[itemprop=\"count\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setClaims(Integer.valueOf(lst.first().text()));
        }
        int citedBy = 0;
        lst = doc.select("section h2:contains(Cited By)");
        if (lst != null && !lst.isEmpty()) {
            String str = lst.first().text();
            citedBy = citedBy + Integer.valueOf(str.substring(str.indexOf("(")+1,str.indexOf(")")));
        }
        lst = doc.select("section h2:contains(Families Citing this family)");
        if (lst != null && !lst.isEmpty()) {
            String str = lst.first().text();
            citedBy = citedBy + Integer.valueOf(str.substring(str.indexOf("(")+1,str.indexOf(")")));
        }
        patent.setCitedBy(citedBy);

        lst = doc.select("section span[itemprop=\"Code\"]");
        if (lst != null && !lst.isEmpty()) {
            String classifications = "";
            for (Element element:lst) {
                classifications = classifications + element.text() + " ";
            }
            if (classifications.length() > 5000) {
                System.out.println(classifications);
                classifications = classifications.substring(0,4999);
            }
            patent.setClassifications(classifications);
        }

        lst = doc.select("dd[itemprop=\"inventor\"]");
        if (lst != null && !lst.isEmpty()) {
            String inventor = "";
            for (Element element:doc.select("dd[itemprop=\"inventor\"]")) {
                inventor = inventor + element.text() + " ";
            }
            patent.setInventors(inventor);
        }

        lst = doc.select("time[itemprop=\"priorityDate\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setPriorityDate(lst.first().text());
        }

        lst = doc.select("time[itemprop=\"filingDate\"]");
        if (lst != null && !lst.isEmpty()) {
            patent.setFilingDate(lst.first().text());
        }

        lst = doc.select("h2");
        for (Element element:lst) {
            if (element.text().contains("Publications")) {
                String publications = "";
                Element ele = element.nextElementSibling();
                Elements numbers = ele.select("span[itemprop=\"publicationNumber\"]");
                Elements dates = ele.select("td[itemprop=\"publicationDate\"]");
                for (int i=0;i < numbers.size();i++) {
                    //System.out.println(numbers.get(i).text() + ":" + dates.get(i).text());
                    publications = publications + numbers.get(i).text() + " -> " + dates.get(i).text() + "<br>";
                }

                patent.setPublications(publications);
                break;
            }
        }

        lst = doc.select("a[itemprop=\"pdfLink\"]");
        if (lst != null && !lst.isEmpty()) {
            String pdfPath = pdf + patent.getPublicationNumber() + ".pdf";
            int state = downloadFile( lst.first().attr("href"), pdfPath);
            if (state == 0) {
                patent.setPdf("/pdf/" + patent.getPublicationNumber() + ".pdf");

                com.goldenideabox.patents.model.Document document = new com.goldenideabox.patents.model.Document();
                document.setName(patent.getPublicationNumber() + ".pdf");
                document.setPath(pdfPath);
                document.setType(0);
                document.setParsestatus(DocumentParseStatus.INIT);
                document.setSearchstatus(DocumentSearchStatus.INIT);
                documentMapper.insert(document);
            } else {
                patent.setPdf("");
            }
        } else {
            patent.setPdf("");
        }

        patent.setQueryKey(key);

        return patent;
    }

    private int downloadFile(String url, String path) {

        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {

            File f = new File(path);
            if (f.exists()) {
                return 0;
            }

            urlfile = new URL(url);
            httpUrl = (HttpURLConnection) urlfile.openConnection();
            httpUrl.connect();
            if (httpUrl.getResponseCode() != 200) {
                return 1;
            }
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            bis.close();
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                if (bis != null && bos != null) {
                    bis.close();
                    bos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private void trustEveryone() {

        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
