package com.proj.entity;


public class SysUser {

  private String id;
  private String username;
  private String nickname;
  private String cipherCode;
  private String type;
  private String state;
  private String nature;
  private String invalidTime;
  private String createTime;
  private String modifyTime;
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


  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }


  public String getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
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
