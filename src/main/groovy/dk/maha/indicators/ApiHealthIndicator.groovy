package dk.maha.indicators

import groovyx.net.http.RESTClient
import org.apache.commons.logging.LogFactory
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.SocketConfig
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

import javax.annotation.PostConstruct

import static groovyx.net.http.Method.GET

class ApiHealthIndicator implements HealthIndicator {

    static log = LogFactory.getLog(ApiHealthIndicator.class);

    private RESTClient client
    private URL url;

    @Value('${httpClient.socketTimeout}')
    int httpClientSocketTimeout

    @Value('${httpClient.connectionTimeout}')
    int httpClientConnectionTimeout

    ApiHealthIndicator(URL url) {
        this.client = new RESTClient(url)
        this.url = url
    }

    @PostConstruct
    def init() {
        SocketConfig sc = SocketConfig.custom().setSoTimeout(httpClientSocketTimeout).build()
        RequestConfig rc = RequestConfig.custom().setConnectTimeout(httpClientConnectionTimeout).setSocketTimeout(httpClientSocketTimeout).build()
        def hc = HttpClients.custom().setDefaultSocketConfig(sc).setDefaultRequestConfig(rc).build()
        this.client.client = hc
    }

    @Override
    Health health() {
        log.info "Processing health-check for " + url.host
        def response
        try {
            response = client.request(GET) { req ->
                getResponse().failure = { resp -> resp }
            }
        } catch (InterruptedIOException | IOException e) {
            log.warn("Health indicator error: " + e)
            return Health.outOfService()
                    .build()
        }
        log.info "Processed health check for " + url.host
        switch (response.status) {
            case 200:
                return Health.up()
//                        .withDetail("body", response.data)
                        .build();
            case 503:
                return Health.outOfService()
                        .build()
            default:
                return Health.down()
                        .build()
        }
    }
}
