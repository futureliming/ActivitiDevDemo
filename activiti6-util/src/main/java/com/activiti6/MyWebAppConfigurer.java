package com.activiti6;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SuppressWarnings("deprecation")
@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {
	/**
	 * 配置拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(createMyInterceptor1()).addPathPatterns("/**/**");
	}
	/**
	 * MyInterceptor1注入bean，解决	 * MyInterceptor1无法调用bean问题
	 * @return
	 */
	@Bean
	public MyInterceptor1 createMyInterceptor1() {
		return new MyInterceptor1();
	}
}