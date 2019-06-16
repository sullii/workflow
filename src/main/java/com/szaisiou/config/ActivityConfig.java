package com.szaisiou.config;

import org.activiti.engine.*;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * activity核心配置类 相当于 activity-cfg.xml
 *
 * @author  suli@szaisiou.com
 * @description 运行springboot启动程序后，通过配置类即可初始化activity数据库
 */
@Configuration
public class ActivityConfig {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ApplicationContext applicationContext;

    @Primary
    @Bean
    public ProcessEngine getProcessEngine() throws IOException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        //读取类路径下 processes目录下 *.bpmn流程文件
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ResourceLoader.CLASSPATH_URL_PREFIX + "processes/*.bpmn");
        //设置数据库链接
        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager);
        config.setDatabaseType("mysql");
        //设置是否使用activti自带的用户体系
        config.setDbIdentityUsed(false);
        //读取流程配置文件
        config.setDeploymentResources(resources);
        //设置字体
        config.setActivityFontName("宋体");
        config.setLabelFontName("宋体");
        config.setAnnotationFontName("宋体");
        //设置数据库自动生成表
        config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        config.setAsyncExecutorActivate(false);
        ProcessEngine processEngine  = config.buildProcessEngine();
        return processEngine;
    }

    //以下是工作流中核心service组件

    /**
     * 工作流仓储服务
     * @param processEngine
     * @return
     */
    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    /**
     * 工作流运行服务
     * @param processEngine
     * @return
     */
    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    /**
     * 工作流任务服务
     * @param processEngine
     * @return
     */
    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }


    /**
     * 工作流历史数据服务
     * @param processEngine
     * @return
     */
    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }


    /**
     * 工作流管理服务
     * @param processEngine
     * @return
     */
    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }


    /**
     * 工作流唯一服务
     * @param processEngine
     * @return
     */
    @Bean
    public IdentityService identityService(ProcessEngine processEngine) {
        return processEngine.getIdentityService();
    }

}

