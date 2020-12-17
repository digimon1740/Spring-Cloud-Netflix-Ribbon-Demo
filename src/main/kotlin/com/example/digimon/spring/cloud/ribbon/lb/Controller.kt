package com.example.digimon.spring.cloud.ribbon.lb

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.server.ServerWebExchange
import javax.annotation.PostConstruct

@RestController
class Controller(
    val loadBalancer: LoadBalancerClient,
    val webClientBuilder: WebClient.Builder,
) {

    private lateinit var webClient: WebClient

    @PostConstruct
    fun setWebClient() {
        webClient = webClientBuilder.build()
    }

    @GetMapping("/v2/contents/{id}")
    suspend fun contents(
        exchange: ServerWebExchange,
        @PathVariable id: Long,
        @RequestHeader(required = false, value = "X-LZ-AllowAdult", defaultValue = "false") adult: Boolean,
        @RequestHeader(required = false, value = "X-LZ-Locale", defaultValue = "ko-KR") locale: String,
        @RequestHeader(required = false, value = "X-LZ-Country", defaultValue = "kr") country: String,
    ): Map<String, Any> {
        val instance = loadBalancer.choose("content-service")
        val url = "${instance.scheme}://${instance.host}:${instance.port}"
        val path = "/v2/contents/$id?store=web"

        return webClient
            .get()
            .uri(url + path)
            .headers { headers ->
                exchange.request.headers.keys.forEach { key ->
                    headers[key] = exchange.request.headers[key]
                }
            }
            .retrieve()
            .awaitBody()
    }
}