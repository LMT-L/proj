package com.proj.api.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.proj.api.entity.SysUser;
import com.proj.api.service.Impl.SysUserServiceImpl;
import com.proj.api.service.SysUserService;
import com.youth.web.framework.platform.toolkit.PageUtil;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sysuser")
public class SysUserController {


    @Autowired
    private SysUserServiceImpl sysUserService;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findSysUser")
    public List<SysUser> findSysUser(){
        List<SysUser> sysUsers = sysUserService.findSysUser();
        return sysUsers;
    }

    /**
     * 添加一个
     * @param sysUser
     * @param request
     * @return
     */
    @RequestMapping("/addSysUser")
    public Integer addSysUser(String sysUser, HttpServletRequest request){
        String sessionId = WebUtils.toHttp(request).getHeader("token");
        System.out.println("当前sessionId：" + sessionId);
        SysUser sysUser1 = JSON.parseObject(sysUser, SysUser.class);
        System.out.println(sysUser);
        int addSysUser = sysUserService.addSysUser(sysUser1);
        if (addSysUser > 0){
            return 0;
        }
        return 200;
    }

    /**
     * 删除一个
     * @param id
     * @return
     */
    @RequestMapping("/removeSysUser")
    public Integer removeSysUser(String id){
        int i = sysUserService.removeSysUser(id);
        if (i > 0){
            return 0;
        }
        return 200;
    }
}