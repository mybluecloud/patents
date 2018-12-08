package com.goldenideabox.patents.service;


import com.goldenideabox.patents.model.GooglePatent;
import com.goldenideabox.patents.model.QueryHistory;
import java.util.List;

public interface GooglePatentsService {

    List<GooglePatent> searchGooglePatentsByKey(String key,QueryHistory queryHistory);


    List<GooglePatent> getGooglePatentByQueryID(int id);
}
