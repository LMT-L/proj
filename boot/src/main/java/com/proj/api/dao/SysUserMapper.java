package com.proj.api.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.proj.api.entity.SysUser;
import com.proj.api.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {
    User queryUser(String username);
}