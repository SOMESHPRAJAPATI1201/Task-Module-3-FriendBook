package in.webkorps.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@SpringBootApplication
public class IntelliJSpringBootWebProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelliJSpringBootWebProjectApplication.class, args);
	}

}
