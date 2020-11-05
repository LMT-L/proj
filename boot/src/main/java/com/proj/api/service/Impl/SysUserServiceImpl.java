package com.proj.api.service.Impl;

import com.proj.api.dao.SysUserMapper;
import com.proj.api.entity.SysUser;
import com.proj.api.entity.User;
import com.proj.api.service.SysUserService;
import com.youth.common.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl extends BaseService implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public List<SysUser> findSysUser() {
        return sysUserMapper.selectList(null);
    }

    @Override
    public int addSysUser(SysUser sysUser) {
        return sysUserMapper.insert(sysUser);
    }

    @Override
    public User findUser(String username) {
        return sysUserMapper.queryUser(username);
    }

    @Override
    public int removeSysUser(String id) {
        return sysUserMapper.deleteById(id);
    }
}