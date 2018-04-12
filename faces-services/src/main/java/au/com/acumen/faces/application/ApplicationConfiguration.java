package au.com.acumen.faces.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@PropertySource("classpath:application.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "au.com.acumen.faces.api",
        "au.com.acumen.faces.repository",
        "au.com.acumen.faces.model",
        "au.com.acumen.faces.utils"


  })

@EnableSwagger2
public class ApplicationConfiguration {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Bean
  public static PropertySourcesPlaceholderConfigurer
  propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public MultipartResolver multipartResolver() {
    StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
    return resolver;
  }
  @Bean
  public AlwaysSampler defaultSampler() {
    return new AlwaysSampler();
  }

}
