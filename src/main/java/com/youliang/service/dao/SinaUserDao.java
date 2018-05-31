package com.youliang.service.dao;

import com.youliang.service.bean.SinaSpiderUser;

public interface SinaUserDao {

    int insert(SinaSpiderUser user);

    int isExist(Long sid);

}
