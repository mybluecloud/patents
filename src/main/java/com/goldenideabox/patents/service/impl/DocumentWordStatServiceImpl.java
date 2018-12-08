package com.goldenideabox.patents.service.impl;

import com.goldenideabox.patents.common.OCR;
import com.goldenideabox.patents.dao.DocumentKeywordFrequencyMapper;
import com.goldenideabox.patents.dao.DocumentMapper;
import com.goldenideabox.patents.model.Document;
import com.goldenideabox.patents.model.Document.DocumentParseStatus;
import com.goldenideabox.patents.model.DocumentKeywordFrequency;
import com.goldenideabox.patents.service.DocumentWordStatService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.apdplat.word.WordFrequencyStatistics;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service("documentWordStatService")
public class DocumentWordStatServiceImpl implements DocumentWordStatService {


    @Value("${ocr.path}")
    private String tessPath;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentKeywordFrequencyMapper documentKeywordFrequencyMapper;

    @Override
    public void parsesDocumentWord(Document document) {

        try {
            String text = parseByPath(document.getPath());
            if (text == null || text.length() == 0 || text.equalsIgnoreCase("null")) {
                return;
            }
            List<DocumentKeywordFrequency> documentKeys = statWordFrequency(document, text);
            System.out.println("documentKeys:"+documentKeys.size());
            for (DocumentKeywordFrequency dkf : documentKeys) {
                //System.out.println(dkf.toString());
                documentKeywordFrequencyMapper.insert(dkf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void calcTFDIF() {
        List<Document> documents = documentMapper.getAllDocument();

        List<DocumentKeywordFrequency> documentKeywords = documentKeywordFrequencyMapper.getAllDocumentKeywordFrequency();
        Map<String, Integer> keyMap = tranList2Map(documentKeywords);

        // 计算频率值
        System.out.println("计算频率值");
        calcKeyWordTFDIF(documents, documentKeywords, keyMap);

        // 根据文档总数刷新文档频率
        System.out.println("根据文档总数刷新文档频率");

        for (DocumentKeywordFrequency dkf : documentKeywords) {
            documentKeywordFrequencyMapper.updateByPrimaryKeySelective(dkf);
        }

    }

    @Override
    public int addDocument(Document document) {
        return documentMapper.insert(document);
    }

    @Override
    public int updateDocument(Document document) {
        return documentMapper.updateByPrimaryKeySelective(document);
    }

    @Override
    public List<Document> getUnParseDocuments() {
        return documentMapper.getUnParseDocuments();
    }

    @Override
    public int updateParseStatusToAll(int parsed) {
        return documentMapper.updateParseStatusToAll(parsed);
    }


    @Override
    public List<String> getDocumentWord(Document document) {

        return documentKeywordFrequencyMapper.getDocumentWord(document.getId(),10);

    }


    private String parseByPath(String path) throws Exception {
        String text = null;
        if (isWord(path)) {
            text = parseWithWord(path);
        } else if (isText(path)) {
            text = parseWithTxt(path);
        } else if (isPDF(path)) {
            text = parseWithPDF(path);
        }
        System.out.println(text);
        return text;
    }


    private void calcKeyWordTFDIF(List<Document> documents, List<DocumentKeywordFrequency> documentKeywords,
        Map<String, Integer> keyMap) {

        int fileSize = documents.size();
        for (DocumentKeywordFrequency keywordFrequency : documentKeywords) {


            String keyword = keywordFrequency.getKeyword();
            int num = keyMap.get(keyword);
            double idf = 0;
            if (fileSize <= num + 1) {
                idf = 1;
            } else {
                idf = Math.log((double) fileSize / (keyMap.get(keyword) + 1));
            }

            double tfidf = idf * keywordFrequency.getTf();

            keywordFrequency.setIdf(idf);
            keywordFrequency.setTfidf(tfidf);
        }
    }

    private Map<String, Integer> tranList2Map(List<DocumentKeywordFrequency> documentKeywords) {
       Map<String, Integer> keyMap = new HashMap<String, Integer>();

        for (DocumentKeywordFrequency keywordFrequency : documentKeywords) {


            String keyword = keywordFrequency.getKeyword();
            Integer num = keyMap.get(keyword);
            if (num == null) {
                num = 1;
            } else {
                num++;
            }
            keyMap.put(keyword, num);
        }
        return keyMap;
    }

    private List<DocumentKeywordFrequency> statWordFrequency(Document document, String text)
        throws Exception {
        // 词频统计设置
        WordFrequencyStatistics wordFrequencyStatistics = new WordFrequencyStatistics();
        wordFrequencyStatistics.setRemoveStopWord(true);
        wordFrequencyStatistics.setSegmentationAlgorithm(SegmentationAlgorithm.MaxNgramScore);
        wordFrequencyStatistics.reset();

        String folder = System.getProperty("java.io.tmpdir");
        File input = new File(folder, UUID.randomUUID().toString() + ".txt");
        FileUtils.write(input, text, "UTF-8");
        File output = new File(folder, UUID.randomUUID().toString() + ".txt");
        // 对文件进行分词
        wordFrequencyStatistics.seg(input, output);
        // 输出词频统计结果
        File outputFrequency = new File(folder, UUID.randomUUID().toString() + ".txt");
        wordFrequencyStatistics.dump(outputFrequency.getAbsolutePath());

        List<DocumentKeywordFrequency> documentKeys = new ArrayList<DocumentKeywordFrequency>();
        BufferedReader br = null;
        int happenMax = 0;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(outputFrequency), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] array = StringUtils.trim(line).split("\\s+");

                if (array.length != 3) {
                    continue;
                }
                DocumentKeywordFrequency documentKey = new DocumentKeywordFrequency();
                String key = array[0];

                if (StringUtils.length(key) == 1) {
                    continue;
                }
                if (StringUtils.isNumeric(key)) {
                    continue;
                }
                int num = Integer.parseInt(array[1]);
                int firstLine = Integer.parseInt(array[2]);
                documentKey.setDocumentid(document.getId());
                documentKey.setKeyword(key);
                documentKey.setNum(num);
                documentKey.setFirstline(firstLine);
                documentKeys.add(documentKey);

                if (num > happenMax) {
                    happenMax = num;
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        if (happenMax > 0) {
            for (DocumentKeywordFrequency documentKey : documentKeys) {
                //double tf =  documentKey.getNum() / happenMax;
                System.out.println("documentKey.getNum():"+documentKey.getNum());
                System.out.println("happenMax:"+happenMax);
                System.out.println("tf:"+(double)documentKey.getNum() / happenMax);
                documentKey.setTf((double)documentKey.getNum() / happenMax);

            }
        }
        return documentKeys;
    }

    private String parseWithWord(String path) throws Exception {
        String text = null;
        if (path.endsWith(".doc")) {
            try {
                text = parseWith2003(path);
            } catch (Exception e) {
                e.printStackTrace();
                text = parseWith2007(path);
            }

        } else if (path.endsWith(".docx")) {
            try {
                text = parseWith2007(path);
            } catch (Exception e) {
                e.printStackTrace();
                text = parseWith2003(path);
            }
        }
        text = StringUtils.trim(text);
        return text;
    }

    private String parseWithPDF(String path) throws Exception {
        String folder = System.getProperty("java.io.tmpdir");
        RandomAccessBufferedFileInputStream is = null;
        try {
            is = new RandomAccessBufferedFileInputStream(new File(path));
            PDFParser parser = new PDFParser(is);
            parser.parse();
            PDDocument document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(document);
            StringBuffer sb = new StringBuffer();
            sb.append(StringUtils.trim(content));
            PDDocumentCatalog cata = document.getDocumentCatalog();
            PDPageTree tree = cata.getPages();
            int count = tree.getCount();
            for (int i = 0; i < count; i++) {
                PDPage page = tree.get(i);
                if (null != page) {
                    PDResources res = page.getResources();
                    Iterable<COSName> xObjectNames = res.getXObjectNames();
                    Iterator<COSName> iterator = xObjectNames.iterator();
                    while (iterator.hasNext()) {
                        COSName name = iterator.next();
                        if (res.isImageXObject(name)) {
                            PDImageXObject img = (PDImageXObject) res.getXObject(name);
                            File imgFile = new File(folder, UUID.randomUUID().toString() + ".png");
                            ImageIO.write(img.getImage(), "png", imgFile);
                            String text = new OCR(tessPath).recognizeText(imgFile, "png");

                            sb.append(StringUtils.trim(text));
                        }
                    }
                }
            }
            return sb.toString();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String parseWithTxt(String path) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r");
            }
            return sb.toString();
        } finally {
            if (br != null) {
                br.close();
            }
        }

    }

    private String parseWith2007(String path) throws IOException, XmlException, OpenXML4JException {
        OPCPackage opcPackage = null;
        try {
            opcPackage = POIXMLDocument.openPackage(path);
            POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
            return extractor.getText();
        } finally {
            if (opcPackage != null) {
                opcPackage.close();
            }
        }
    }

    private String parseWith2003(String path) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            WordExtractor ex = new WordExtractor(is);
            return ex.getText();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private boolean isWord2003(String path) {
        return path.endsWith(".doc");
    }

    private boolean isWord2007(String path) {
        return path.endsWith(".docx");
    }

    private boolean isWord(String path) {
        return isWord2003(path) || isWord2007(path);
    }

    private boolean isText(String path) {
        return path.endsWith(".txt");
    }

    private boolean isPDF(String path) {
        return path.endsWith(".pdf");
    }
}
