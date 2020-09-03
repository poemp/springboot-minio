package org.poem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Configuration {

    @Value("${swagger.enable}")
    private boolean enableSwagger;

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("")
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.poem"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(buildApiInf())
                .enable(enableSwagger);
    }

    private ApiInfo buildApiInf() {
        return new ApiInfoBuilder()
                .title("minio doc")
                .contact("zh")
                .version("1.0")
                .build();
    }

}
