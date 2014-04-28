package magic.springext.support;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * 自动装配类型。
 * {@link AutowireCapableBeanFactory#AUTOWIRE_NO}
 * {@link AutowireCapableBeanFactory#AUTOWIRE_BY_TYPE}
 * {@link AutowireCapableBeanFactory#AUTOWIRE_BY_NAME}
 * {@link AutowireCapableBeanFactory#AUTOWIRE_CONSTRUCTOR}
 * {@link AutowireCapableBeanFactory#AUTOWIRE_AUTODETECT}
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月28日 下午8:08:29
 */
public enum Autowire {
	NO(AutowireCapableBeanFactory.AUTOWIRE_NO, "no"),
	BY_TYPE(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, "byType"),
	BY_NAME(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, "byName"),
	CONSTRUCTOR(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, "constructor"),
	;
	public int type;
	public String name;
	private Autowire(int type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public static Autowire parse(int type) {
		for (Autowire autowire : values()) {
			if(autowire.type == type) {
				return autowire;
			}
		}
		return Autowire.BY_NAME;
	}
	
	public static Autowire parse(String name) {
		for (Autowire autowire : values()) {
			if(autowire.name.equalsIgnoreCase(name)) {
				return autowire;
			}
		}
		return Autowire.BY_NAME;
	}
}