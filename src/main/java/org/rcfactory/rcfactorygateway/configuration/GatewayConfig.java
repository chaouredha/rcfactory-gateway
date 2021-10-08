package org.rcfactory.rcfactorygateway.configuration;

import static io.github.resilience4j.timelimiter.TimeLimiterConfig.custom;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(route ->
                        route.path("/finance/**")
                                .filters(filter -> filter
                                        .addRequestHeader("x-rapidapi-host", "yh-finance.p.rapidapi.com")
                                        .addRequestHeader("x-rapidapi-key", "f51ef48892msh18d2ad8f2b649fbp19a2c8jsnd9b0dd329746")
                                        .rewritePath("/finance/(?<segment>.*)", "/${segment}")
                                        .circuitBreaker(h -> h.setName("finance")
                                                .setFallbackUri("forward:/defaultFinance")
                                        )
                                )
                                .uri("https://yh-finance.p.rapidapi.com/auto-complete"))
                .build();
    }

    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator(
            ReactiveDiscoveryClient reactiveDiscoveryClient,
            DiscoveryLocatorProperties discoveryLocatorProperties) {
        return new DiscoveryClientRouteDefinitionLocator(reactiveDiscoveryClient, discoveryLocatorProperties);
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultConfig() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig())
                .circuitBreakerConfig(circuitBreakerConfig())
                .build()
        );
    }

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(ofMillis(1000))
                .slidingWindowSize(2)
                .build();
    }

    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return custom()
                .timeoutDuration(ofSeconds(1))
                .build();
    }
}
