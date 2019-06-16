package com.szaisiou.service;

import java.util.List;

/**
 * @Description 用于处理表单的curd操作
 * @Author suli@szaisiou.com
 */
public interface FormHandleService<T,R> {

    public void addRecord(T t) throws Exception;

    public void deleteRecord(R id) throws Exception;

    public void updateRecord(T t) throws Exception;

    public T findRecord(R id) throws Exception;

    public List<T> findAllRecord() throws Exception;

}
