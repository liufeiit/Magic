package magic.springext.service;

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
}