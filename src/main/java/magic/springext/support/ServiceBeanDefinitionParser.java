package magic.springext.support;

import magic.service.HelloService;

import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.Conventions;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Spring Extension Service XML Parser.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月26日 下午2:33:48
 */
public class ServiceBeanDefinitionParser extends AbstractBeanDefinitionParser {

	private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final String TRUE_VALUE = "true";
	public static final String FALSE_VALUE = "false";

	public static final String DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE = "all";
	public static final String DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE = "simple";
	public static final String DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE = "objects";
	
	public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";

	public static final String CLASS_ATTRIBUTE = "class";
	public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";
	public static final String PARENT_ATTRIBUTE = "parent";
	public static final String ABSTRACT_ATTRIBUTE = "abstract";
	public static final String DEPENDENCY_CHECK_ATTRIBUTE = "dependency-check";
	public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
	public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";
	public static final String PRIMARY_ATTRIBUTE = "primary";
	public static final String INIT_METHOD_ATTRIBUTE = "init-method";
	public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
	public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";
	public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";
	public static final String AUTOWIRE_ATTRIBUTE = "autowire";
	public static final String SCOPE_ATTRIBUTE = "scope";

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/service-demo.xml");
		HelloService helloService = context.getBean("hello", HelloService.class);
		System.out.println(helloService.say());
	}

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		Class<?> beanClass = getBeanClass(element, parserContext);
		GenericBeanDefinition definition = new GenericBeanDefinition();
		definition.setBeanClass(beanClass);
		definition.setAbstract(isAbstract(element, parserContext));
		definition.setAutowireCandidate(isAutowireCandidate(element, parserContext));
		definition.setAutowireMode(isAutowireMode(element, parserContext));
		definition.setDependencyCheck(getDependencyCheck(element, parserContext));
		if (element.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
			String dependsOn = element.getAttribute(DEPENDS_ON_ATTRIBUTE);
			definition.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, MULTI_VALUE_ATTRIBUTE_DELIMITERS));
		}
		definition.setDestroyMethodName(getDestroyMethodName(element, parserContext));
		definition.setEnforceDestroyMethod(false);
		definition.setEnforceInitMethod(false);
		definition.setFactoryBeanName(getFactoryBeanName(element, parserContext));
		definition.setFactoryMethodName(getFactoryMethodName(element, parserContext));
		definition.setInitMethodName(getInitMethodName(element, parserContext));
		definition.setLazyInit(isLazyInit(element, parserContext));
		definition.setParentName(getParentName(element, parserContext));
		if (element.hasAttribute(PRIMARY_ATTRIBUTE)) {
			definition.setPrimary(TRUE_VALUE.equals(element.getAttribute(PRIMARY_ATTRIBUTE)));
		}
		definition.setScope(getScope(element, parserContext));
		
		//BeanDefinitionParserDelegate
//		parseLookupOverrideSubElements(element, definition.getMethodOverrides());
//		parseReplacedMethodSubElements(element, definition.getMethodOverrides());
//
//		parseConstructorArgElements(element, definition);
//		parsePropertyElements(element, definition);
//		parseQualifierElements(element, definition);
		
		NodeList props = element.getElementsByTagName("property");
		int len = props.getLength();
		for (int i = 0; i < len; i++) {
			Element e = Element.class.cast(props.item(i));
			String propertyName = Conventions.attributeNameToPropertyName(e.getAttribute("name"));
			String propertyValue = e.getAttribute("value");
			String propertyRef = e.getAttribute("ref");
			if (!StringUtils.isEmpty(propertyRef)) {
				definition.getPropertyValues().add(propertyName, new RuntimeBeanReference(propertyRef));
			} else {
				definition.getPropertyValues().add(propertyName, propertyValue);
			}
		}
		return definition;
	}

	protected int getDependencyCheck(Element element, ParserContext parserContext) {
		if(!element.hasAttribute(DEPENDENCY_CHECK_ATTRIBUTE)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_NONE;
		}
		String dependencyCheck = element.getAttribute(DEPENDENCY_CHECK_ATTRIBUTE);
		if (DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE.equals(dependencyCheck)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_ALL;
		} else if (DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE.equals(dependencyCheck)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_OBJECTS;
		} else if (DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE.equals(dependencyCheck)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_SIMPLE;
		} else {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_NONE;
		}
	}

	protected String getScope(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(SCOPE_ATTRIBUTE)) {
			return Scope.SINGLETON.name;
		}
		return Scope.parse(element.getAttribute(SCOPE_ATTRIBUTE)).name;
	}

	protected boolean isPrimary(Element element, ParserContext parserContext) {
		return TRUE_VALUE.equals(element.getAttribute(PRIMARY_ATTRIBUTE));
	}

	protected String getParentName(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(PARENT_ATTRIBUTE)) {
			return null;
		}
		return element.getAttribute(PARENT_ATTRIBUTE);
	}

	protected String getInitMethodName(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
			return null;
		}
		return element.getAttribute(INIT_METHOD_ATTRIBUTE);
	}

	protected String getFactoryMethodName(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
			return null;
		}
		return element.getAttribute(FACTORY_METHOD_ATTRIBUTE);
	}

	protected String getFactoryBeanName(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
			return null;
		}
		return element.getAttribute(FACTORY_BEAN_ATTRIBUTE);
	}

	protected String getDestroyMethodName(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
			return null;
		}
		return element.getAttribute(DESTROY_METHOD_ATTRIBUTE);
	}

	protected int isAutowireMode(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(AUTOWIRE_ATTRIBUTE)) {
			return Autowire.BY_NAME.type;
		}
		String autowire = element.getAttribute(AUTOWIRE_ATTRIBUTE);
		return Autowire.parse(autowire).type;
	}

	protected boolean isAutowireCandidate(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE)) {
			return true;
		}
		String autowireCandidate = element.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE);
		return parse(autowireCandidate, true);
	}

	protected boolean isAbstract(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(ABSTRACT_ATTRIBUTE)) {
			return false;
		}
		String abs = element.getAttribute(ABSTRACT_ATTRIBUTE);
		return parse(abs, false);
	}

	protected boolean isLazyInit(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(LAZY_INIT_ATTRIBUTE)) {
			return parserContext.isDefaultLazyInit();
		}
		String lazyInit = element.getAttribute(LAZY_INIT_ATTRIBUTE);
		return parse(lazyInit, parserContext.isDefaultLazyInit());
	}

	protected Class<?> getBeanClass(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(CLASS_ATTRIBUTE)) {
			throw new FatalBeanException("Service class for ServiceBeanDefinitionParser not found.");
		}
		String className = element.getAttribute(CLASS_ATTRIBUTE);
		try {
			return ClassUtils.forName(className, CLASS_LOADER);
		} catch (ClassNotFoundException ex) {
			throw new FatalBeanException("Service class [" + className + "] for ServiceBeanDefinitionParser not found",
					ex);
		} catch (LinkageError err) {
			throw new FatalBeanException("Invalid Service class [" + className
					+ "] for nServiceBeanDefinitionParser: problem with handler class file or dependent class", err);
		}
	}

	private int parse(String str, int defaultValue) {
		if (str == null || str.isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private boolean parse(String str, boolean defaultValue) {
		if (str == null || str.isEmpty()) {
			return defaultValue;
		}
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}