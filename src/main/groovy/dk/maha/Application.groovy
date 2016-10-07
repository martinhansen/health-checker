package dk.maha

import org.apache.commons.logging.LogFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class Application {
    static log = LogFactory.getLog(Application.class);

    static void main(String[] args) {
        def context = new SpringApplicationBuilder()
                .sources(this)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args)

        log.info("Started " +
                "active profiles: ${context.environment.activeProfiles}")
    }
}
