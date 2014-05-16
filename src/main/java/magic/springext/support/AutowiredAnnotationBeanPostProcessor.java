package magic.springext.support;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import magic.springext.annotation.Injectable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年5月16日 下午7:37:14
 */
public class AutowiredAnnotationBeanPostProcessor extends org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor {

	public AutowiredAnnotationBeanPostProcessor() {
		Set<Class<? extends Annotation>> autowiredAnnotationTypes = new HashSet<Class<? extends Annotation>>();
		autowiredAnnotationTypes.add(Inject.class);
		autowiredAnnotationTypes.add(Injectable.class);
		autowiredAnnotationTypes.add(Autowired.class);
		autowiredAnnotationTypes.add(Value.class);
		setAutowiredAnnotationTypes(autowiredAnnotationTypes);
	}
}