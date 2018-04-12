package au.com.acumen.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableZuulProxy
@EnableSwagger2
@RestController

public class ApplicationGateway {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationGateway.class, args);
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
				UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}


}
