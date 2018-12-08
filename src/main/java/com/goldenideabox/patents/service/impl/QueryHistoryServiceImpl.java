package com.goldenideabox.patents.service.impl;

import com.goldenideabox.patents.common.TestHelper;
import com.goldenideabox.patents.dao.QueryHistoryMapper;
import com.goldenideabox.patents.model.QueryHistory;
import com.goldenideabox.patents.service.QueryHistoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service("queryHistoryService")
public class QueryHistoryServiceImpl implements QueryHistoryService {

    @Autowired
    private QueryHistoryMapper queryHistoryMapper;

    @Autowired
    private TestHelper testHelper;

    @Override
    public int addQueryHistory(QueryHistory queryHistory) {
        return queryHistoryMapper.insert(queryHistory);
    }

    @Override
    public int updateQueryHistory(QueryHistory queryHistory) {
        return queryHistoryMapper.updateByPrimaryKeySelective(queryHistory);
    }

    @Override
    public List<QueryHistory> getAllQueryHistory() {
        return queryHistoryMapper.getAllQueryHistory();
    }

    @Override
    public QueryHistory getQueryHistory(int id) {
        testHelper.executeCpquery();
        return queryHistoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteQueryHistory(int id) {
        return queryHistoryMapper.deleteByPrimaryKey(id);
    }


}
