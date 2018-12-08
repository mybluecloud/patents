package com.goldenideabox.patents.dao;

import com.goldenideabox.patents.model.AreaInfo;
import java.util.List;

public interface AreaInfoMapper {


    List<AreaInfo> selectByDeep(Integer deep);

    List<AreaInfo> selectByPid(Integer pid);

    AreaInfo selectById(Integer id);
}