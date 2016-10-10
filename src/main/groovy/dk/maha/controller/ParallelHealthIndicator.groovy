package dk.maha.controller

import dk.maha.indicators.ApiHealthIndicator
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ParallelHealthIndicator {

    def log = LogFactory.getLog(ParallelHealthIndicator.class)

    @Autowired
    private Map<String, ApiHealthIndicator> indicators

    @Autowired
    @Qualifier('healthExecutor')
    def executor

    @RequestMapping(value='/something', method=RequestMethod.GET)
    def @ResponseBody checkHealth() {
        def result = [:]

        executor.inParallel(indicators, { indicator ->
            result.put(indicator.key, indicator.value.health())
        })

        result.sort({ it.key })
    }
}
