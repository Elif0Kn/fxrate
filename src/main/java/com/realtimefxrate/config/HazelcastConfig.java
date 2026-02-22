package com.realtimefxrate.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        // Map with TTL
        MapConfig mapConfig = new MapConfig("rates"); // "rates" is the map name
        mapConfig.setTimeToLiveSeconds(60); // TTL is set to 60 seconds
        config.addMapConfig(mapConfig);

        return Hazelcast.newHazelcastInstance(config);
    }
}
