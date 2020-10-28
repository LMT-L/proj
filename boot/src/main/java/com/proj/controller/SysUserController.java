package com.proj.controller;

import com.proj.entity.SysUser;
import com.proj.service.SysUserService;
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
    public List<SysUser> findSysUser(){
        List<SysUser> sysUsers = sysUserService.findSysUser();
        return sysUsers;
    }
}