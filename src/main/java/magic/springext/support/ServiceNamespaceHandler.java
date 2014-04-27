package magic.springext.support;

import java.util.Date;

import magic.service.HelloService;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring Extension Service schema Handler.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月26日 下午2:24:23
 */
public class ServiceNamespaceHandler extends NamespaceHandlerSupport {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/service-demo.xml");
		HelloService helloService = context.getBean("hello", HelloService.class);
		System.out.println(helloService.say());
	}
	
	@Override
	public void init() {
		registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
	}
}