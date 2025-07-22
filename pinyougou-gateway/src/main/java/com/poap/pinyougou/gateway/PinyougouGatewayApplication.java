package com.poap.pinyougou.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PinyougouGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinyougouGatewayApplication.class, args);
	}

}
