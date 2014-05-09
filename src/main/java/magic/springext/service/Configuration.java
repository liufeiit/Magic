package magic.springext.service;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.MapConfiguration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Service Configuration {@link Service#setConfigurer(Configuration)}
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年5月9日 下午4:38:52
 */
public class Configuration extends MapConfiguration {

	protected BeanFactory beanFactory;
	protected BeanDefinition definition;

	public Configuration(Map<String, ?> map, BeanFactory beanFactory, BeanDefinition definition) {
		super(map);
		this.beanFactory = beanFactory;
		this.definition = definition;
	}

	public Configuration(Properties props, BeanFactory beanFactory, BeanDefinition definition) {
		super(props);
		this.beanFactory = beanFactory;
		this.definition = definition;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public BeanDefinition getDefinition() {
		return definition;
	}
}