package com.szaisiou;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//因为activiti-spring-boot-starter-basic中引用了spring-boot-starter-security
@SpringBootApplication(exclude = {org.activiti.spring.boot.SecurityAutoConfiguration.class})
public class WorkflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowApplication.class, args);
	}

}
