package org.automation.datamover.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

	/*@Bean
	public RemoteIpFilter remoteIpFilter() {
		System.out.println("================================================");
		return new RemoteIpFilter();
	}*/

	@Bean
	public FilterRegistrationBean<Filter> filterRegistrationBean() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new MyFilter());
		registration.addUrlPatterns("/*");
		registration.setName("MyFilter");
		registration.setOrder(1);
		return registration;
	}

	public class MyFilter implements Filter {
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
		}

		@Override
		public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain filterChain)
				throws IOException, ServletException {
			//HttpServletRequest request = (HttpServletRequest) srequest;
			//System.out.println("this is MyFilter,url :"+request.getRequestURI());
			//request.setAttribute("ctxPath", request.getContextPath());
			filterChain.doFilter(srequest, sresponse);
		}

		@Override
		public void init(FilterConfig arg0) throws ServletException {
			// TODO Auto-generated method stub
		}
	}

}
