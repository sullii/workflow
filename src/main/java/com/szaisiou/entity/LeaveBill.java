package com.szaisiou.entity;

import lombok.Data;

import java.util.Date;

/**
 * 请假单实体类
 *
 * @Author suli
 */
@Data
public class LeaveBill extends Bill{



    private String id;
    private String empId;
    private String reason;
    private String remark;
    private Date creatTime;
    private Date finishTime;
    private Integer status;
    private Date startTime;
    private Date endTime;
}
