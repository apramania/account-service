package com.apratim.banking.accountservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient() {
        // Configure and create RedissonClient instance
        Config config = new Config();

        config.useSingleServer()
              .setAddress("redis://localhost:6379"); // Replace with your Redis server address
        // You can customize the configuration as needed (e.g., host, port, password)
        return Redisson.create(config);
    }
}
