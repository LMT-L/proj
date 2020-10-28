package com.proj.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.proj.api.entity.SysUser;
import com.proj.api.service.SysUserService;
import com.youth.web.framework.platform.toolkit.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sysuser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @RequestMapping("/findSysUser")
    public Page<SysUser> findSysUser(){
        List<SysUser> sysUsers = sysUserService.findSysUser();
        Page<SysUser> page = PageUtil.getPage(sysUsers, 1, 10);
        return page;
    }
}