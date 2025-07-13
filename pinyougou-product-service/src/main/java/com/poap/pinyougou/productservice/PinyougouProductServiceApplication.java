package com.poap.pinyougou.productservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.poap.pinyougou.productservice.feign") // 确保扫描到 UserFeignClient 接口
public class PinyougouProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinyougouProductServiceApplication.class, args);
	}

}
