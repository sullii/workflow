package com.szaisiou.entity;

import lombok.Data;

import java.util.Date;

/**
 */
@Data
public class ActivitiDto {

    private String name;
    private Integer days;
    private String reason;
    private String remark;
    private String userName;
    private Date createTime;
    private String status;

}
