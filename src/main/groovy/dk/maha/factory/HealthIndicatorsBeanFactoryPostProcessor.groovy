package dk.maha.factory

import dk.maha.indicators.AsyncIndicator
import org.apache.commons.lang.StringUtils
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.Configuration

@Configuration
class HealthIndicatorsBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private List<URL> urls;

    HealthIndicatorsBeanFactoryPostProcessor() {
        urls = parseUrls("http://google.dk,http://google.com")
    }

    @Override
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        urls.each { url ->
            String beanName = 'healthCheck@'+url.host
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AsyncIndicator.class).setLazyInit(true)
            builder.addConstructorArgValue(url)
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition())
        }
    }

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // we actually only add new beans, but do not post process the existing definitions
    }

    private static List<String> parseUrls(String rawUrls) {
        if (StringUtils.isEmpty(rawUrls)){
            throw new IllegalArgumentException("Property 'apiUrls' is undefined.");
        }
        List<URL> urls = new ArrayList<>()
        rawUrls.split(',').each { url ->
            urls.add(new URL(url))
        }
        return Collections.unmodifiableList(urls)
    }
}
