package in.webkorps.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/users/**", "/posts/**")
                .addResourceLocations(
                        "file:///C:/Users/Dell/Documents/FriendBook/users/",
                        "file:///C:/Users/Dell/Documents/FriendBook/posts/")
                .setCachePeriod(0);
    }
}
