package com.szaisiou.entity;

import lombok.Data;

import java.util.Date;

/**
 * 员工关系表
 *
 * @Author suli
 */
@Data
public class Employ {
    private String id;
    private String name;
    private Integer age;
    private Date birthday;
    private String mobile;
    private String email;
    private String depNo;
    private String managerId;
    private String position;
    private String depName;
}
