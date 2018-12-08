package com.goldenideabox.patents.service;

import com.goldenideabox.patents.model.QueryHistory;
import java.util.List;

public interface QueryHistoryService {


    int addQueryHistory(QueryHistory queryHistory);

    int updateQueryHistory(QueryHistory queryHistory);

    List<QueryHistory> getAllQueryHistory();

    QueryHistory getQueryHistory(int id);

    int deleteQueryHistory(int id);
}
