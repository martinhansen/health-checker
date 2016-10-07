package dk.maha.indicators

import groovyx.net.http.RESTClient
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

import static groovyx.net.http.Method.GET

class AsyncIndicator implements HealthIndicator {

    RESTClient client

    AsyncIndicator(URL url) {
        this.client = new RESTClient(url)
    }

    @Override
    Health health() {
        def response
        try {
            response = client.request(GET) { req ->
                getResponse().failure = { resp -> resp }
            }
        } catch (InterruptedIOException | IOException e) {
            return Health.outOfService()
                    .build()
        }

        switch (response.status) {
            case 200:
                return Health.up()
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
