package org.sun.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @Package: org.sun.es
 * @Auther: yxd
 * @Date: 2019-03-29 09:16:50
 * @Description:
 */

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "org.sun.es.dao")
public class StartApp {

    public static void main(String[] args) {
        SpringApplication.run(StartApp.class,args);
    }

}
