package com.youliang.service.impl;

import com.youliang.service.SinaUserService;
import com.youliang.service.bean.SinaSpiderUser;
import com.youliang.service.dao.SinaUserDao;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sinaUserService")
public class SinaUserServiceImpl implements SinaUserService {
    private static final Logger log = LoggerFactory.getLogger(SinaUserServiceImpl.class);

    @Autowired
    private SinaUserDao userDao;

    @Override
    public boolean insert(SinaSpiderUser user) {
        if(user != null) {
            log.debug("insert SinaSpiderUser: {}", user);
            return userDao.insert(user) > 0;
        }
        return false;
    }

    @Override
    public boolean isExistUser(Long sid) {
        if(sid < 0) {
            log.info("user sid is error; sid:{}", sid);
            return false;
        }
        return userDao.isExist(sid) > 0;
    }
}
