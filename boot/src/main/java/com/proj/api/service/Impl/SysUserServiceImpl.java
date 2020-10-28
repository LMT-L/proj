package com.proj.api.service.Impl;

import com.proj.api.dao.SysUserMapper;
import com.proj.api.entity.SysUser;
import com.proj.api.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysUser> findSysUser() {
        return sysUserMapper.querySysUser();
    }
}