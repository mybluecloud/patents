package com.goldenideabox.patents.service;

import com.goldenideabox.patents.model.Document;
import java.util.List;

public interface DocumentWordStatService {

    List<String> getDocumentWord(Document document);



    //void calcTFDIF() throws Exception;

    void parsesDocumentWord(Document document);

    void calcTFDIF();

    int addDocument(Document document);

    int updateDocument(Document document);

    List<Document> getUnParseDocuments();

    int updateParseStatusToAll(int parsed);
}
