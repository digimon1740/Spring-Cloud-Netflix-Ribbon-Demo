# Spring-Cloud-Netflix-Ribbon-Demo

1. MSA 배경 지식
    - What is an API Gateway ?
        - 현대적 마이크로서비스 어플리케이션은 L7만으론 부족한 기능들이 존재함
        - 특징들 : Routing, Canary-ing, Security, Monolith Strangling, Monitoring, Resiliency
    - Side-car pattern, Proxy pattern 을 합친것을 Service Mesh pattern
        - 애플리케이션이 존재하는 모든 서버안에 Proxy 가 존재
        - inbound, outbound 모두 proxy를 사용
        - 몇가지 오픈소스 제품들이 나와있음
2. 클라이언트 로드밸런서
    - [https://dzone.com/articles/create-an-api-gateway-with-load-balancer-in-java](https://dzone.com/articles/create-an-api-gateway-with-load-balancer-in-java)

    ```jsx
    API Gateway (e.g. POST /login HTTP 1.1 or GET /contents HTTP 1.1 ) 
    -> Netflix Ribbon (Zone Available Latency check) 
    -> API Endpoint
    ```

3. Istio 사용이 어려움
    - 쿠버네티스에 찰떡..
    - 설정 기반이라 개발자가 커스텀 하기 어려움
4. Spring cloud ribbon
    - Netflix ribbon 기반
        - 로드밸런싱 알고리즘 적용 가능
            - 예) 라운드 로빈, 웨이트 방식 등
        - 캐시, 라우팅, 로깅, 모니터링 등 처리를 개발자가 커스텀 할 수 있음
    - WebFlux
        - Netty 기반(비동기-논블로킹)
        - 복잡한 Reactive Streams(Mono, Flux)대신 코루틴 사용 했음.
5. http 테스트
    - `http :8888/v2/contents/260 x-lz-allowadult:true`
6. Ab 테스트
    - `ab -n 200 -c 200 127.0.0.1:8888/v2/contents/260`
7. 같이 하면 좋은 
    - [Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker)
        - Netfix Hystrix (지는해)
        - Resilience4J (대세)
            - Circuit breaking(Fail-fast)
            - Rate limiting(요청 제어)
            - Bulkheading(동시 실행 제어)
            - Automatic retrying(실패시 재시도)
            - Timeout handling(타임 아웃 제어)
            - Result caching(성공 시점의 데이터를 캐시)
            - Fallback(장애시 폴백 데이터 제공)
    - [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
        - 2점대 부터 내부적으로 Netflix Zuul 안씀
        - Reactive 기반
    - Spring Cloud Netflix Eureka
        - Service discovery
            - 클라이언트 헬스 체크
            - dns 같은 name 서버 역할 MSA 환경에서 static한 호스트를 구현하기 어려움