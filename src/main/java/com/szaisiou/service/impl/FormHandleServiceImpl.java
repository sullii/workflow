package com.szaisiou.service.impl;

import com.szaisiou.common.utils.IdGen;
import com.szaisiou.entity.LeaveBill;
import com.szaisiou.service.FormHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description 表单处理业务
 * @Author suli@szaisiou.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FormHandleServiceImpl implements FormHandleService<LeaveBill,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormHandleServiceImpl.class);

    @Autowired
    private FormHandleServiceImpl formHandleService;

    @Override
    public void addRecord(LeaveBill leaveBill) throws Exception {
        leaveBill.setId(IdGen.uuid());
        formHandleService.addRecord(leaveBill);
    }

    @Override
    public void deleteRecord(String id) throws Exception {

    }

    @Override
    public void updateRecord(LeaveBill o) throws Exception{

    }

    @Override
    public LeaveBill findRecord(String id) throws Exception {
        return null;
    }

    @Override
    public List findAllRecord() throws Exception{
        return null;
    }
}
