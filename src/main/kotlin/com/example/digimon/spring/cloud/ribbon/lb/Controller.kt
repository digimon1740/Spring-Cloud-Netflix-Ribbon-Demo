package com.example.digimon.spring.cloud.ribbon.lb

import org.slf4j.LoggerFactory
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.server.ServerWebExchange
import javax.annotation.PostConstruct

@RestController
class Controller(
    val loadBalancer: LoadBalancerClient,
    val webClientBuilder: WebClient.Builder,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var webClient: WebClient

    @PostConstruct
    fun setWebClient() {
        val exchangeStrategies =
            ExchangeStrategies
                .builder()
                .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(-1) }
                .build()
        webClient = webClientBuilder
            .exchangeStrategies(exchangeStrategies)
            .build()
    }

    @GetMapping("/v2/contents/{alias}")
    suspend fun contents(
        exchange: ServerWebExchange,
        @PathVariable alias: String,
        @RequestParam store: String,
        @RequestParam(required = false) type: String,
    ): Map<String, Any> {
        val instance = loadBalancer.choose("content-service")
        val url = "${instance.scheme}://${instance.host}:${instance.port}"
        val path = "/v2/contents/$alias?store=$store"
        logger.info("url : {}", url + path)
        return webClient
            .get()
            .uri(url + path)
            .headers { headers ->
                exchange.request.headers.keys.forEach { key ->
                    if (key.startsWith("x-lz", ignoreCase = true)) {
                        headers[key] = exchange.request.headers[key]
                    }
                }
            }
            .retrieve()
            .awaitBody()
    }
}