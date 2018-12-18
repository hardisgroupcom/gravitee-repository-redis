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

import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.EntryPointRepository;
import io.gravitee.repository.management.model.EntryPoint;
import io.gravitee.repository.redis.management.internal.EntryPointRedisRepository;
import io.gravitee.repository.redis.management.model.RedisEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class RedisEntryPointRepository implements EntryPointRepository {

    @Autowired
    private EntryPointRedisRepository entryPointRedisRepository;

    @Override
    public Optional<EntryPoint> findById(final String entryPointId) throws TechnicalException {
        final RedisEntryPoint redisEntryPoint = entryPointRedisRepository.findById(entryPointId);
        return Optional.ofNullable(convert(redisEntryPoint));
    }

    @Override
    public EntryPoint create(final EntryPoint entryPoint) throws TechnicalException {
        final RedisEntryPoint redisEntryPoint = entryPointRedisRepository.saveOrUpdate(convert(entryPoint));
        return convert(redisEntryPoint);
    }

    @Override
    public EntryPoint update(final EntryPoint entryPoint) throws TechnicalException {
        if (entryPoint == null || entryPoint.getValue() == null) {
            throw new IllegalStateException("EntryPoint to update must have a value");
        }

        final RedisEntryPoint redisEntryPoint = entryPointRedisRepository.findById(entryPoint.getId());

        if (redisEntryPoint == null) {
            throw new IllegalStateException(String.format("No entryPoint found with id [%s]", entryPoint.getId()));
        }

        final RedisEntryPoint redisEntryPointUpdated = entryPointRedisRepository.saveOrUpdate(convert(entryPoint));
        return convert(redisEntryPointUpdated);
    }

    @Override
    public Set<EntryPoint> findAll() throws TechnicalException {
        final Set<RedisEntryPoint> entryPoints = entryPointRedisRepository.findAll();

        return entryPoints.stream()
                .map(this::convert)
                .collect(Collectors.toSet());
    }

    @Override
    public void delete(final String entryPointId) throws TechnicalException {
        entryPointRedisRepository.delete(entryPointId);
    }

    private EntryPoint convert(final RedisEntryPoint redisEntryPoint) {
        final EntryPoint entryPoint = new EntryPoint();
        entryPoint.setId(redisEntryPoint.getId());
        entryPoint.setValue(redisEntryPoint.getValue());
        entryPoint.setTags(redisEntryPoint.getTags());
        return entryPoint;
    }

    private RedisEntryPoint convert(final EntryPoint entryPoint) {
        final RedisEntryPoint redisEntryPoint = new RedisEntryPoint();
        redisEntryPoint.setId(entryPoint.getId());
        redisEntryPoint.setValue(entryPoint.getValue());
        redisEntryPoint.setTags(entryPoint.getTags());
        return redisEntryPoint;
    }
}
