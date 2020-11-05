package com.proj.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@TableName(value = "SYS_USER")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUser {
    private String id;
    private String username;
    private String nickname;
    @JsonIgnore
    private String cipherCode;
    private String type;
    private String state;
    private String nature;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    private String invalidTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifyTime;
    private String tenantId;
    private String revision;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }


  public String getCipherCode() {
    return cipherCode;
  }

  public void setCipherCode(String cipherCode) {
    this.cipherCode = cipherCode;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }


  public String getNature() {
    return nature;
  }

  public void setNature(String nature) {
    this.nature = nature;
  }


  public String getInvalidTime() {
    return invalidTime;
  }

  public void setInvalidTime(String invalidTime) {
    this.invalidTime = invalidTime;
  }


  public long getCreateTime() {
    return createTime.getTime();
  }

  public void setCreateTime(long createTime) {
      this.createTime = new Date(createTime);
  }


  public long getModifyTime() {
    return modifyTime.getTime();
  }

  public void setModifyTime(long modifyTime) {
    this.modifyTime = new Date(modifyTime);
  }


  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }


  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

    @Override
    public String toString() {
        return "SysUser{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", cipherCode='" + cipherCode + '\'' +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", nature='" + nature + '\'' +
                ", invalidTime='" + invalidTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", revision='" + revision + '\'' +
                '}';
    }
}
