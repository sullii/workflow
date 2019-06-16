package com.szaisiou.dao;

import com.szaisiou.common.ibatis.BaseMapper;
import com.szaisiou.entity.LeaveBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 请假申请表单
 * @Author suli@szaisiou.com
 */
@Mapper
@Repository
public interface LeaveBillMapper{

    void addLeaveBillRecord(LeaveBill leaveBill);

    void deleteLeaveBillRecord(String id);

    void updateLeaveBillRecord(LeaveBill leaveBill);

    LeaveBill selectLeaveBillRecord(String id);

    List<LeaveBill> selectLeaveBillRecordlist();

    List<LeaveBill> selectRelation(@Param("userId") String userId);

}
