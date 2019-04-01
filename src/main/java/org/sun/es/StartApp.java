package org.sun.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @Package: org.sun.es
 * @Auther: yxd
 * @Date: 2019-03-29 09:16:50
 * @Description:
 */

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "org.sun.es.dao") // 将es操作接口注入到spring容器
public class StartApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(StartApp.class,args);
    }

}
