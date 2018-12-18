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
package io.gravitee.repository.redis.management.internal.impl;

import io.gravitee.repository.redis.management.internal.EntryPointRedisRepository;
import io.gravitee.repository.redis.management.model.RedisEntryPoint;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class EntryPointRedisRepositoryImpl extends AbstractRedisRepository implements EntryPointRedisRepository {

    private final static String REDIS_KEY = "entryPoint";

    @Override
    public Set<RedisEntryPoint> findAll() {
        final Map<Object, Object> entryPoints = redisTemplate.opsForHash().entries(REDIS_KEY);

        return entryPoints.values()
                .stream()
                .map(object -> convert(object, RedisEntryPoint.class))
                .collect(Collectors.toSet());
    }

    @Override
    public RedisEntryPoint findById(final String entryPointId) {
        Object entryPoint = redisTemplate.opsForHash().get(REDIS_KEY, entryPointId);
        return convert(entryPoint, RedisEntryPoint.class);
    }

    @Override
    public RedisEntryPoint saveOrUpdate(final RedisEntryPoint entryPoint) {
        redisTemplate.opsForHash().put(REDIS_KEY, entryPoint.getId(), entryPoint);
        return entryPoint;
    }

    @Override
    public void delete(final String entryPoint) {
        redisTemplate.opsForHash().delete(REDIS_KEY, entryPoint);
    }
}
