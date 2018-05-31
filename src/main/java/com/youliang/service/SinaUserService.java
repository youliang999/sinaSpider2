package com.youliang.service;

import com.youliang.service.bean.SinaSpiderUser;

public interface SinaUserService {

    boolean insert(SinaSpiderUser user);

    boolean isExistUser(Long sid);

}
