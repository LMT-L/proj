package com.proj.api.dao;

import com.proj.api.entity.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserMapper {
    List<SysUser> querySysUser();
}