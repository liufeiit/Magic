package magic.service;

import org.apache.commons.configuration.Configuration;

/**
 * 表示一个Service接口。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月28日 下午4:27:22
 */
public interface Service {
	void setConfigurer(Configuration configurer);

	String SERVICE_NAME = "service.name";
	String SERVICE_LAZY_INIT = "service.lazy-init";
	String SERVICE_CLASS = "service.class";
	String SERVICE_PARENT = "service.parent";
	String SERVICE_ABSTRACT = "service.abstract";
	String SERVICE_DEPENDS_ON = "service.depends-on";
	String SERVICE_AUTOWIRE_CANDIDATE = "service.autowire-candidate";
	String SERVICE_PRIMARY = "service.primary";
	String SERVICE_INIT_METHOD = "service.init-method";
	String SERVICE_DESTROY_METHOD = "service.destroy-method";
	String SERVICE_FACTORY_METHOD = "service.factory-method";
	String SERVICE_FACTORY_BEAN = "service.factory-bean";
	String SERVICE_AUTOWIRE = "service.autowire";
	String SERVICE_SCOPE = "service.scope";
	/**
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
	 */
	String SERVICE_REGISTRY = "service.registry";
}