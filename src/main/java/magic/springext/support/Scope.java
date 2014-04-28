package magic.springext.support;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月28日 下午8:43:44
 */
public enum Scope {
	SINGLETON(1, ConfigurableBeanFactory.SCOPE_SINGLETON),
	PROTOTYPE(2, ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	;
	public int scope;
	public String name;
	private Scope(int scope, String name) {
		this.scope = scope;
		this.name = name;
	}
	
	public static Scope parse(int scope) {
		for (Scope s : values()) {
			if(s.scope == scope) {
				return s;
			}
		}
		return Scope.SINGLETON;
	}
	
	public static Scope parse(String name) {
		for (Scope s : values()) {
			if(s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return Scope.SINGLETON;
	}
}