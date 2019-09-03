package com.wjy.gateway;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableEurekaClient
public class SpringCloudGatewayApplication {

//	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("path_route", r -> r.path("/about")
						.uri("http://ityouknow.com"))
				.build();
	}

	@Bean
	@Scope("prototype")
	@Qualifier("userKeyResolver")
	@Primary
	public KeyResolver userKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
	}

	@Bean
	@Scope("prototype")
	@Qualifier("ipKeyResolver")
	public KeyResolver ipKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudGatewayApplication.class, args);
	}

}
