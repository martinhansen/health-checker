package dk.maha.executor

import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class HealthExecutorConfiguration {

    def log = LogFactory.getLog(HealthExecutorConfiguration.class)

    def poolSize = 10

    def queueCapacity = 10

    @Bean
    public AsyncTaskExecutor siteExecutor() {
        def executor = new ThreadPoolTaskExecutor()
        executor.corePoolSize = poolSize
        executor.maxPoolSize = poolSize
        executor.queueCapacity = queueCapacity
        executor.threadNamePrefix = 'HealthExecutor'
        log.debug "Configured Site executor with pool size ${poolSize}, queue capacity ${queueCapacity}"
        return executor
    }
}
