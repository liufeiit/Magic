package magic.springext.support;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Spring Extension Service schema Handler.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月26日 下午2:24:23
 */
public class ServiceNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
	}
}