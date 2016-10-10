package dk.maha.indicators

import groovyx.net.http.RESTClient
import org.apache.commons.logging.LogFactory
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.SocketConfig
import org.apache.http.impl.client.HttpClients
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

import static groovyx.net.http.Method.GET

class ApiHealthIndicator implements HealthIndicator {

    static log = LogFactory.getLog(ApiHealthIndicator.class);

    private RESTClient client
    private URL url;

    ApiHealthIndicator(URL url) {
        this.client = new RESTClient(url)
        def timeout = 2000
        SocketConfig sc = SocketConfig.custom().setSoTimeout(timeout).build()
        RequestConfig rc = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build()
        def hc = HttpClients.custom().setDefaultSocketConfig(sc).setDefaultRequestConfig(rc).build()
        this.client.client = hc
        this.url = url
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
