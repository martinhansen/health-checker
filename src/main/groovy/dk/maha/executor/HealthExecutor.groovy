package dk.maha.executor

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Component
class HealthExecutor {

    def log = LogFactory.getLog(HealthExecutor.class)

    @Autowired
    AsyncTaskExecutor taskExecutor

    @Value('${httpClient.socketTimeout}')
    int httpClientSocketTimeout

    @Value('${httpClient.connectionTimeout}')
    int httpClientConnectionTimeout

    int timeoutSeconds

    @PostConstruct
    def init() {
        timeoutSeconds = (httpClientConnectionTimeout / 1000) + (httpClientSocketTimeout / 1000) + 2
    }

    def inParallel(collection, closure) {
        def result = []
        def futures = [:]

        collection.each { element ->
            futures[element] = taskExecutor.submit({
                closure.call element
            } as Runnable)
        }

        futures.each { element, future ->
            try {
                def futureResult = future.get(timeoutSeconds, TimeUnit.SECONDS)
            } catch (ExecutionException|InterruptedException e) {
                log.warn("Excecution exception or interrupted exception")
            } catch (TimeoutException ignored) {
                def msg = "Timeout after ${timeoutSeconds} seconds for future for element ${element}"
                log.warn(msg);
            }
        }

        return result
    }

}
