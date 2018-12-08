package com.goldenideabox.patents.schedule;

import com.goldenideabox.patents.model.Document;
import com.goldenideabox.patents.model.Document.DocumentParseStatus;
import com.goldenideabox.patents.service.DocumentWordStatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoUpdateWordFrequency {

    private static boolean init = false;

    @Autowired
    private DocumentWordStatService documentWordStatService;

     //@Scheduled(cron = "0 30 15 * * ?") // 每天凌晨一点执行
    //@Scheduled(cron = "0 0/1 * * * ?") // 每小时执行
    //@Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次
    public void updateWordFrequency() {

         if (!init) {
             init = true;
             List<Document> docs = documentWordStatService.getUnParseDocuments();

             for (Document doc:docs) {
                 documentWordStatService.parsesDocumentWord(doc);
             }

             documentWordStatService.calcTFDIF();

             documentWordStatService.updateParseStatusToAll(DocumentParseStatus.PARSED);

             init = false;
         }

    }

}
