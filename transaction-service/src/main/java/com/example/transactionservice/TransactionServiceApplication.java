package com.example.transactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
// @EnableDiscoveryClient // Tắt Eureka service discovery
// @EnableKafka // Tắt Kafka
public class TransactionServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransactionServiceApplication.class, args);
  }

}
