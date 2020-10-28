package com.proj.service.Impl;

import com.proj.dao.SysUserMapper;
import com.proj.entity.SysUser;
import com.proj.service.SysUserService;
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