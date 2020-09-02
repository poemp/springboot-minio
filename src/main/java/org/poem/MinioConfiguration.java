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
import okhttp3.OkHttpClient;
import org.poem.excception.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinioConfigurationProperties.class)
@ComponentScan("org.poem")
public class MinioConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioConfiguration.class);

    @Autowired
    private MinioConfigurationProperties minioConfigurationProperties;

    @Bean
    public MinioClient minioClient() throws InvalidEndpointException, InvalidPortException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException, org.poem.excception.MinioException, XmlParserException {

        MinioClient minioClient;
        if(!configuredProxy()) {
            minioClient = new MinioClient(
                    minioConfigurationProperties.getUrl(),
                    minioConfigurationProperties.getAccessKey(),
                    minioConfigurationProperties.getSecretKey(),
                    minioConfigurationProperties.isSecure()
            );
        }
        else{
            minioClient = new MinioClient(
                    minioConfigurationProperties.getUrl(),
                    0,
                    minioConfigurationProperties.getAccessKey(),
                    minioConfigurationProperties.getSecretKey(),
                    null,
                    minioConfigurationProperties.isSecure(),
                    client()
            );
        }
        minioClient.setTimeout(
            minioConfigurationProperties.getConnectTimeout().toMillis(),
            minioConfigurationProperties.getWriteTimeout().toMillis(),
            minioConfigurationProperties.getReadTimeout().toMillis()
        );

        if (minioConfigurationProperties.isCheckBucket()) {
            try {
                LOGGER.debug("Checking if bucket {} exists", minioConfigurationProperties.getBucket());
                boolean b = minioClient.bucketExists(minioConfigurationProperties.getBucket());
                if (!b) {
                    if (minioConfigurationProperties.isCreateBucket()) {
                        try {
                            minioClient.makeBucket(minioConfigurationProperties.getBucket());
                        } catch (RegionConflictException e) {
                            throw new org.poem.excception.MinioException("Cannot create bucket", e);
                        }
                    } else {
                        throw new InvalidBucketNameException(minioConfigurationProperties.getBucket(), "Bucket does not exists");
                    }
                }
            } catch
            (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException  | ErrorResponseException | InternalException | InvalidResponseException | MinioException | XmlParserException
                    e) {
                LOGGER.error("Error while checking bucket", e);
                throw e;
            }
        }

        return minioClient;
    }

    private boolean configuredProxy(){
        String httpHost = System.getProperty("http.proxyHost");
        String httpPort = System.getProperty("http.proxyPort");
        return httpHost!=null && httpPort!=null;
    }
    private OkHttpClient client() {
        String httpHost = System.getProperty("http.proxyHost");
        String httpPort = System.getProperty("http.proxyPort");

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(httpHost!=null)
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpHost,Integer.parseInt(httpPort))));
        return builder
                .build();
    }

}
