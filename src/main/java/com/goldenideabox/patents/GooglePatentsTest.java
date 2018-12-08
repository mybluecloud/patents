package com.goldenideabox.patents;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goldenideabox.patents.model.GooglePatent;
import com.goldenideabox.patents.service.DocumentWordStatService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

public class GooglePatentsTest {
    @Autowired
    private DocumentWordStatService documentWordStatService;

    public static void main(String[] args) throws UnsupportedEncodingException {
        Date date = new Date();
        String url = "http://cpquery.sipo.gov.cn/JcaptchaServlet?type=1&usertype=1&date=" + URLEncoder.encode(date + "" + Math.random(), "UTF-8");

        System.out.println(url);


    }

    private static String parseSipoIds(String enStr) {
        int b4 = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < enStr.length(); i += 2) {
            if (b4 > 255)
                b4 = 0;
            int c = Integer.parseInt(enStr.substring(i, i + 2), 16) ^ b4++;
            sb.append((char) c);
        }
        return sb.toString();
    }


}
