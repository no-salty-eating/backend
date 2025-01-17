package com.sparta.product.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

@Slf4j
@Configuration
public class EcsConfig {

	@Bean
	public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
		EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
		String publicIp = null;
		String privateIp;
		try {
			URL url = new URL("https://api.ipify.org");

			privateIp = InetAddress.getLocalHost().getHostAddress();
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			publicIp = reader.readLine();
			log.info("Public IP Address: {}", publicIp);
			log.info("Private IP Address: {}", privateIp);
		} catch (Exception e) {
			log.error("Failed : {}", e.getMessage());
		}

		config.setIpAddress(publicIp);

		return config;
	}

}
