package com.proj.dao;

import com.proj.entity.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SysUserMapper {
    List<SysUser> querySysUser();
}