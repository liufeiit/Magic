package magic.service;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月27日 下午10:39:56
 */
public class HelloService implements Service {

	private String name;
	
	private String firstName;
	
	private HelloService service;
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setService(HelloService service) {
		this.service = service;
	}
	
	public String say() {
		if(service != null)
		return "Hello " + name + ", " + firstName + " | " + service.say();
		return "Hello " + name + ", " + firstName;
	}

	/**
	 * @param configurer
	 */
	@Override
	public void setConfigurer(Configuration configurer) {
		Iterator<String> keys = configurer.getKeys();
		while(keys.hasNext()) {
			String key = keys.next();
			System.out.println(key + "=" + configurer.getProperty(key));
		}
	}
}