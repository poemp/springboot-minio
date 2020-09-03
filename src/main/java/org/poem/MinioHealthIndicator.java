/*
 * Copyright Jordan LEFEBURE © 2019.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.poem;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.poem.config.MinioConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Set the Minio health indicator on Actuator.
 *
 * @author Jordan LEFEBURE
 */
@ConditionalOnClass(ManagementContextAutoConfiguration.class)
@Component
public class MinioHealthIndicator implements HealthIndicator {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfigurationProperties minioConfigurationProperties;


    @Override
    public Health health() {
        if (minioClient == null) {
            return Health.down().build();
        }

        try {
            if (minioClient.bucketExists(minioConfigurationProperties.getBucket())) {
                return Health.up()
                        .withDetail("bucketName", minioConfigurationProperties.getBucket())
                        .build();
            } else {
                return Health.down()
                        .withDetail("bucketName", minioConfigurationProperties.getBucket())
                        .build();
            }
        } catch (InvalidBucketNameException | IOException | NoSuchAlgorithmException | InsufficientDataException | InvalidKeyException | XmlParserException | ErrorResponseException | InternalException | InvalidResponseException | ServerException e) {
            return Health.down(e)
                    .withDetail("bucketName", minioConfigurationProperties.getBucket())
                    .build();
        }
    }
}
