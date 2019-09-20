package com.boot.webservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "sys_log")
public class SysLog implements Serializable {

    @Id
    private String id;

    private String description;//描述

    private String username;//用户

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;//开始时间

    @Column(precision = 10)
    private Integer spendTime;//消耗时间

    private String basePath;//根路径

    private String uri;//URI

    private String url;//URL

    private String method;//请求类型

    private String ip;//IP地址

    @Column(length = 4000)
    private String parameter;//请求参数

    @Column(columnDefinition = "clob")
    private String result;//返回参数

    private String operation;//操作

    private String serverName;//服务名

}
