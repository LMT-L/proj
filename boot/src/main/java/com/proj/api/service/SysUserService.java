package com.proj.api.service;

import com.proj.api.entity.SysUser;
import com.proj.api.entity.User;
import com.youth.common.base.service.BaseService;
import io.swagger.models.auth.In;

import java.util.List;


public interface SysUserService {
    List<SysUser> findSysUser();
    int addSysUser(SysUser sysUser);
    User findUser(String username);
    int removeSysUser(String id);
}
