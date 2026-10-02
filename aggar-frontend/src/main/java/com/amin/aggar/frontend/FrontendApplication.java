package com.amin.aggar.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.client.RestTemplate;
import com.amin.aggar.frontend.config.ApiBearerTokenInterceptor;

import java.util.List;

@SpringBootApplication
public class FrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(ApiBearerTokenInterceptor bearerTokenInterceptor) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(bearerTokenInterceptor);
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setAcceptLanguageAsLocales(List.of(LocaleContextHolder.getLocale()));
			return execution.execute(request, body);
		});
		return restTemplate;
	}

}
