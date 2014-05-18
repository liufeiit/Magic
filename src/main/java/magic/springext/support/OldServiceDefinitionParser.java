package magic.springext.support;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import magic.service.HelloService;
import magic.springext.service.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.parsing.PropertyEntry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
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
 * @see BeanDefinitionParserDelegate
 *      {@link BeanDefinitionParserDelegate#parseBeanDefinitionElement(Element)}
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月26日 下午2:33:48
 */
public class OldServiceDefinitionParser extends AbstractBeanDefinitionParser {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	public static final String SERVICES_NAMESPACE_URI = "http://www.itjiehun.com/schema/magic/service";

	private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final String SET_CONFIGURER_METHOD_NAME = "setConfigurer";

	public static final Class<?>[] SET_CONFIGURER_METHOD_PARAM_TYPES = new Class<?>[] { Configuration.class };

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

	public static final String PROPERTY_ELEMENT = "property";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String CONFIGURER_ELEMENT = "configurer";
	public static final String KEY_ATTRIBUTE = "key";
	public static final String ITEM_ELEMENT = "item";
	public static final String SERVICE_ATTRIBUTE = "service";
	public static final String REF_ELEMENT = "ref";
	public static final String REF_ATTRIBUTE = "ref";
	public static final String VALUE_ATTRIBUTE = "value";

	private final ParseState parseState = new ParseState();

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/service-demo.xml");
		HelloService helloService = context.getBean("hello", HelloService.class);
		System.out.println(helloService.say());
		System.out.println(helloService.fetchService.ss());
	}
	
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		parseState.push(new BeanEntry(element.getAttribute(ID_ATTRIBUTE)));
		GenericBeanDefinition definition = null;
		try {
			Class<?> beanClass = getBeanClass(element, parserContext);
			definition = new GenericBeanDefinition();
			parseBeanDefinitionAttributes(element, parserContext, beanClass, definition);
			parsePropertyElements(element, parserContext, definition);
			if (ClassUtils.hasMethod(beanClass, SET_CONFIGURER_METHOD_NAME, SET_CONFIGURER_METHOD_PARAM_TYPES)) {
				parseConfigurerElement(element, parserContext, definition);
			}
		} finally {
			parseState.pop();
		}
		return definition;
	}

	protected void parseBeanDefinitionAttributes(Element element, ParserContext parserContext, Class<?> beanClass,
			GenericBeanDefinition definition) {
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
	}

	protected void parseConfigurerElement(Element element, ParserContext parserContext, BeanDefinition definition) {
		NodeList configurerElement = element.getElementsByTagName(CONFIGURER_ELEMENT);
		if (configurerElement.getLength() <= 0) {
			return;
		}
		Map<String, Object> env = new HashMap<String, Object>();
		env.putAll(System.getenv());
		env.putAll(convertPropertiesToMap(System.getProperties()));
		Configuration configuration = new Configuration(env, (DefaultListableBeanFactory) parserContext.getRegistry(),
				definition);
		NodeList itemElements = ((Element) configurerElement.item(0)).getElementsByTagName(ITEM_ELEMENT);
		int len = itemElements.getLength();
		if (len <= 0) {
			PropertyValue pv = new PropertyValue(CONFIGURER_ELEMENT, configuration);
			definition.getPropertyValues().addPropertyValue(pv);
			return;
		}
		for (int i = 0; i < len; i++) {
			Node itemElement = itemElements.item(i);
			if (!isCandidateElement(itemElement)) {
				continue;
			}
			Element item = (Element) itemElement;
			String key = item.getAttribute(KEY_ATTRIBUTE);
			if (!StringUtils.hasLength(key)) {
				log.error("Tag 'item' must have a 'key' attribute");
				continue;
			}
			Object val = parseItemValue(item, parserContext, key);
			if (val == null) {
				continue;
			}
			configuration.setProperty(key, val);
		}
		PropertyValue pv = new PropertyValue(CONFIGURER_ELEMENT, configuration);
		definition.getPropertyValues().addPropertyValue(pv);
	}

	private Object parseItemValue(Element item, ParserContext parserContext, String key) {
		return item.getAttribute(VALUE_ATTRIBUTE);
	}

	protected void parsePropertyElements(Element element, ParserContext parserContext, BeanDefinition definition) {
		NodeList propertyElements = element.getElementsByTagName(PROPERTY_ELEMENT);
		int len = propertyElements.getLength();
		if (len <= 0) {
			return;
		}
		for (int i = 0; i < len; i++) {
			Node propertyElement = propertyElements.item(i);
			if (!isCandidateElement(propertyElement)) {
				continue;
			}
			parsePropertyElement((Element) propertyElement, parserContext, definition);
		}
	}

	protected void parsePropertyElement(Element propertyElement, ParserContext parserContext, BeanDefinition definition) {
		String propertyName = propertyElement.getAttribute(NAME_ATTRIBUTE);
		propertyName = Conventions.attributeNameToPropertyName(propertyName);
		if (!StringUtils.hasLength(propertyName)) {
			log.error("Tag 'property' must have a 'name' attribute");
			return;
		}
		parseState.push(new PropertyEntry(propertyName));
		try {
			if (definition.getPropertyValues().contains(propertyName)) {
				log.error("Multiple 'property' definitions for property '" + propertyName + "'");
				return;
			}
			Object val = parsePropertyValue(propertyElement, parserContext, propertyName);
			PropertyValue pv = new PropertyValue(propertyName, val);
			definition.getPropertyValues().addPropertyValue(pv);
		} finally {
			parseState.pop();
		}
	}

	protected Object parsePropertyValue(Element propertyElement, ParserContext parserContext, String propertyName) {
		NodeList subNode = propertyElement.getElementsByTagName(REF_ELEMENT);
		Element subElement = null;
		if (subNode.getLength() > 0) {
			subElement = (Element) subNode.item(0);
		}
		boolean hasRefAttribute = propertyElement.hasAttribute(REF_ATTRIBUTE);
		boolean hasValueAttribute = propertyElement.hasAttribute(VALUE_ATTRIBUTE);
		if ((hasRefAttribute && hasValueAttribute) || ((hasRefAttribute || hasValueAttribute) && subElement != null)) {
			log.error("<property> element for property '" + propertyName
					+ "' is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element");
		}
		if (hasRefAttribute) {
			String refName = propertyElement.getAttribute(REF_ATTRIBUTE);
			if (!StringUtils.hasText(refName)) {
				log.error("<property> element for property '" + propertyName + "' contains empty 'ref' attribute");
			}
			RuntimeBeanReference ref = new RuntimeBeanReference(refName);
			ref.setSource(parserContext.extractSource(propertyElement));
			return ref;
		} else if (hasValueAttribute) {
			TypedStringValue valueHolder = new TypedStringValue(propertyElement.getAttribute(VALUE_ATTRIBUTE));
			valueHolder.setSource(parserContext.extractSource(propertyElement));
			return valueHolder;
		} else if (subElement != null) {
			return parsePropertySubElement(subElement, parserContext);
		} else {
			log.error("<property> element for property '" + propertyName + "' must specify a ref or value");
			return new TypedStringValue(null);
		}
	}

	public Object parsePropertySubElement(Element subElement, ParserContext parserContext) {
		boolean hasServiceAttribute = subElement.hasAttribute(SERVICE_ATTRIBUTE);
		if (hasServiceAttribute) {
			RuntimeBeanReference ref = new RuntimeBeanReference(subElement.getAttribute(SERVICE_ATTRIBUTE));
			ref.setSource(parserContext.extractSource(subElement));
			return ref;
		}
		return new TypedStringValue(null);
	}

	protected boolean isDefaultNamespace(String namespaceUri) {
		return (!StringUtils.hasLength(namespaceUri) || SERVICES_NAMESPACE_URI.equals(namespaceUri));
	}

	protected boolean isDefaultNamespace(Node node) {
		return isDefaultNamespace(getNamespaceURI(node));
	}

	protected String getNamespaceURI(Node node) {
		return node.getNamespaceURI();
	}

	protected String getLocalName(Node node) {
		return node.getLocalName();
	}

	protected boolean isCandidateElement(Node node) {
		return (node instanceof Element && (isDefaultNamespace(node) || !isDefaultNamespace(node.getParentNode())));
	}

	protected int getDependencyCheck(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(DEPENDENCY_CHECK_ATTRIBUTE)) {
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
		return TRUE_VALUE.equals(element.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE));
	}

	protected boolean isAbstract(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(ABSTRACT_ATTRIBUTE)) {
			return false;
		}
		return TRUE_VALUE.equals(element.getAttribute(ABSTRACT_ATTRIBUTE));
	}

	protected boolean isLazyInit(Element element, ParserContext parserContext) {
		if (!element.hasAttribute(LAZY_INIT_ATTRIBUTE)) {
			return parserContext.isDefaultLazyInit();
		}
		return TRUE_VALUE.equals(element.getAttribute(LAZY_INIT_ATTRIBUTE));
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

	private Map<String, Object> convertPropertiesToMap(final Properties props) {
		return new AbstractMap<String, Object>() {
			@Override
			public Set<Map.Entry<String, Object>> entrySet() {
				Set<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();
				for (final Map.Entry<Object, Object> propertyEntry : props.entrySet()) {
					if (propertyEntry.getKey() instanceof String) {
						entries.add(new Map.Entry<String, Object>() {
							public String getKey() {
								return propertyEntry.getKey().toString();
							}

							public Object getValue() {
								return propertyEntry.getValue();
							}

							public Object setValue(Object value) {
								throw new UnsupportedOperationException();
							}
						});
					}
				}
				return entries;
			}
		};
	}
}