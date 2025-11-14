package com.mapofmemory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile({"local", "test"})
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Map-of-Memory API Document")
                .version("v1.0.0")
                .description("기억의 지도 API 명세서입니다.");

        Server localServer = new Server().url("http://localhost:8080").description("Local server");

        return new OpenAPI()
                .components(new Components())
                .info(info)
                .servers(List.of(localServer));
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("1. Member API")
                .pathsToMatch("/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memoryApi() {
        return GroupedOpenApi.builder()
                .group("2. Memory API")
                .pathsToMatch("/memories/**")
                .pathsToExclude("/memories/**/like")
                .build();
    }

    @Bean
    public GroupedOpenApi likeApi() {
        return GroupedOpenApi.builder()
                .group("3. Like API")
                .pathsToMatch("/memories/**/like")
                .build();
    }
}
