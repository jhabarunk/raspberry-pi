package com.ge.sos.rmd.datasource;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/*@EnableAutoConfiguration(exclude = {
      org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class})*/
@PropertySources({
	@PropertySource("classpath:application.properties"),
  @PropertySource("classpath:config.properties")
})
@ComponentScan(basePackages = "com.ge")
@Controller
@SpringBootApplication
@EnableResourceServer
public class SOSRmdDatasourceApplication extends ResourceServerConfigurerAdapter{

	public static void main(String[] args) {
		//SOSRmdDatasourceApplication.run(SOSRmdDatasourceApplication.class, args);
		new SpringApplicationBuilder(SOSRmdDatasourceApplication.class).web(true).run(args);

	}
	public void configure(HttpSecurity http) throws Exception {
		//http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
		http.antMatcher("/**")
		.authorizeRequests()
		.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
		.anyRequest().authenticated(); 
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("openid");
	}
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            	System.out.println("::In WebMvcConfigurer ---");
                registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*");
            }
        };
    }

}