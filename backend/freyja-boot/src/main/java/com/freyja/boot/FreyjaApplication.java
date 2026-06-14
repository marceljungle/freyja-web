package com.freyja.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.freyja")
@EntityScan(basePackages = "com.freyja.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.freyja.infrastructure.persistence.repository")
public class FreyjaApplication {

  public static void main(String[] args) {
    SpringApplication.run(FreyjaApplication.class, args);
  }
}
