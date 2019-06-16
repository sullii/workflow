package com.szaisiou.dao;

import com.szaisiou.entity.Employ;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2019/6/12.
 */
@Mapper
@Repository
public interface EmployMapper {

    Employ findEmploy(Employ em);
}
