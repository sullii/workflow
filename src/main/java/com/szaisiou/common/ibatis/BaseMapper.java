package com.szaisiou.common.ibatis;

/**
 * @Description
 * @Author huangjianye
 * @Date $date$ $time$
 * @Param $param$
 * @Return $return$
 * @Exception $exception$
 */

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 所有dao的基类
 * @param <T>
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
