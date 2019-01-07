/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.redis.management;

import io.gravitee.repository.config.TestRepositoryInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import redis.embedded.RedisSentinel;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Conditional(SentinelCondition.class)
public class SentinelTestRepositoryConfiguration extends ManagementRepositoryConfiguration {

    @Bean
    public TestRepositoryInitializer testRepositoryInitializer() {
        return new RedisTestRepositoryInitializer();
    }

    @Bean(destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        final RedisServer redisServer = new RedisServer();
        redisServer.start();
        return redisServer;
    }

    @Bean(destroyMethod = "stop")
    public RedisSentinel redisSentinel() throws IOException {
        final RedisSentinel redisSentinel = RedisSentinel.builder()
                .port(26379)
                .setting(String.format("sentinel monitor %s %s %s 1", "mymaster", InetAddress.getLocalHost().getHostAddress(), 6379))
                .setting(String.format("sentinel down-after-milliseconds %s 200", "mymaster"))
                .setting(String.format("sentinel failover-timeout %s 1000", "mymaster"))
                .build();

        redisSentinel.start();
        return redisSentinel;
    }

}