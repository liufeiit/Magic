package magic.springext.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.beans.factory.parsing.ConstructorArgumentEntry;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.parsing.PropertyEntry;
import org.springframework.beans.factory.parsing.QualifierEntry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @see org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年5月17日 下午7:58:51
 */
public class ServiceDefinitionParserDelegate {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public static final String SERVICES_NAMESPACE_URI = "http://www.itjiehun.com/schema/magic/service";

	public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";

	/**
	 * Value of a T/F attribute that represents true. Anything else represents
	 * false. Case seNsItive.
	 */
	public static final String TRUE_VALUE = "true";

	public static final String FALSE_VALUE = "false";

	public static final String DEFAULT_VALUE = "default";

	public static final String DESCRIPTION_ELEMENT = "description";

	public static final String AUTOWIRE_NO_VALUE = "no";

	public static final String AUTOWIRE_BY_NAME_VALUE = "byName";

	public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";

	public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";

	public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";

	public static final String DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE = "all";

	public static final String DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE = "simple";

	public static final String DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE = "objects";

	public static final String NAME_ATTRIBUTE = "name";

	public static final String SERVICE_ELEMENT = "service";

	public static final String META_ELEMENT = "meta";

	public static final String ID_ATTRIBUTE = "id";

	public static final String PARENT_ATTRIBUTE = "parent";

	public static final String CLASS_ATTRIBUTE = "class";

	public static final String ABSTRACT_ATTRIBUTE = "abstract";

	public static final String SCOPE_ATTRIBUTE = "scope";

	public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";

	public static final String AUTOWIRE_ATTRIBUTE = "autowire";

	public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";

	public static final String PRIMARY_ATTRIBUTE = "primary";

	public static final String DEPENDENCY_CHECK_ATTRIBUTE = "dependency-check";

	public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";

	public static final String INIT_METHOD_ATTRIBUTE = "init-method";

	public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";

	public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";

	public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";

	public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";

	public static final String INDEX_ATTRIBUTE = "index";

	public static final String TYPE_ATTRIBUTE = "type";

	public static final String VALUE_TYPE_ATTRIBUTE = "value-type";

	public static final String KEY_TYPE_ATTRIBUTE = "key-type";

	public static final String PROPERTY_ELEMENT = "property";

	public static final String REF_ATTRIBUTE = "ref";

	public static final String VALUE_ATTRIBUTE = "value";

	public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";

	public static final String REPLACED_METHOD_ELEMENT = "replaced-method";

	public static final String REPLACER_ATTRIBUTE = "replacer";

	public static final String ARG_TYPE_ELEMENT = "arg-type";

	public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";

	public static final String REF_ELEMENT = "ref";

	public static final String IDREF_ELEMENT = "idref";

	public static final String SERVICE_REF_ATTRIBUTE = "service";

	public static final String LOCAL_REF_ATTRIBUTE = "local";

	public static final String PARENT_REF_ATTRIBUTE = "parent";

	public static final String VALUE_ELEMENT = "value";

	public static final String NULL_ELEMENT = "null";

	public static final String ARRAY_ELEMENT = "array";

	public static final String LIST_ELEMENT = "list";

	public static final String SET_ELEMENT = "set";

	public static final String MAP_ELEMENT = "map";

	public static final String ENTRY_ELEMENT = "entry";

	public static final String KEY_ELEMENT = "key";

	public static final String KEY_ATTRIBUTE = "key";

	public static final String KEY_REF_ATTRIBUTE = "key-ref";

	public static final String VALUE_REF_ATTRIBUTE = "value-ref";

	public static final String PROPS_ELEMENT = "props";

	public static final String PROP_ELEMENT = "prop";

	public static final String MERGE_ATTRIBUTE = "merge";

	public static final String QUALIFIER_ELEMENT = "qualifier";

	public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";

	private final XmlReaderContext readerContext;

	private final ParseState parseState = new ParseState();

	private Environment environment;

	private final Set<String> usedNames = new HashSet<String>();

	private ParserContext parserContext;

	public ServiceDefinitionParserDelegate(ParserContext parserContext) {
		Assert.notNull(parserContext, "ParserContext must not be null");
		this.readerContext = parserContext.getReaderContext();
		this.environment = parserContext.getDelegate().getEnvironment();
		this.parserContext = parserContext;
	}

	public BeanDefinitionHolder parseServiceDefinitionElement(Element element) {
		return parseServiceDefinitionElement(element, null);
	}

	public BeanDefinitionHolder parseServiceDefinitionElement(Element element, BeanDefinition containingService) {
		String id = element.getAttribute(ID_ATTRIBUTE);
		String nameAttr = element.getAttribute(NAME_ATTRIBUTE);
		List<String> aliases = new ArrayList<String>();
		if (StringUtils.hasLength(nameAttr)) {
			String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
			aliases.addAll(Arrays.asList(nameArr));
		}
		String serviceName = id;
		if (!StringUtils.hasText(serviceName) && !aliases.isEmpty()) {
			serviceName = aliases.remove(0);
			if (log.isDebugEnabled()) {
				log.debug("No XML 'id' specified - using '" + serviceName + "' as service name and " + aliases
						+ " as aliases");
			}
		}
		if (containingService == null) {
			checkNameUniqueness(serviceName, aliases, element);
		}
		AbstractBeanDefinition serviceDefinition = parseServiceDefinitionElement(element, serviceName, containingService);
		if (serviceDefinition == null) {
			return null;
		}
		if (!StringUtils.hasText(serviceName)) {
			try {
				if (containingService != null) {
					serviceName = BeanDefinitionReaderUtils.generateBeanName(serviceDefinition,
							this.readerContext.getRegistry(), true);
				} else {
					serviceName = this.readerContext.generateBeanName(serviceDefinition);
					String serviceClassName = serviceDefinition.getBeanClassName();
					if (serviceClassName != null && serviceName.startsWith(serviceClassName)
							&& serviceName.length() > serviceClassName.length()
							&& !this.readerContext.getRegistry().isBeanNameInUse(serviceClassName)) {
						aliases.add(serviceClassName);
					}
				}
				if (log.isDebugEnabled()) {
					log.debug("Neither XML 'id' nor 'name' specified - " + "using generated service name ["
							+ serviceName + "]");
				}
			} catch (Exception ex) {
				error(ex.getMessage(), element);
				return null;
			}
		}
		String[] aliasesArray = StringUtils.toStringArray(aliases);
		return new BeanDefinitionHolder(serviceDefinition, serviceName, aliasesArray);
	}

	protected void checkNameUniqueness(String serviceName, List<String> aliases, Element serviceElement) {
		String foundName = null;
		if (StringUtils.hasText(serviceName) && this.usedNames.contains(serviceName)) {
			foundName = serviceName;
		}
		if (foundName == null) {
			foundName = CollectionUtils.findFirstMatch(this.usedNames, aliases);
		}
		if (foundName != null) {
			error("Service name '" + foundName + "' is already used in this <services> element", serviceElement);
		}
		usedNames.add(serviceName);
		usedNames.addAll(aliases);
	}

	public AbstractBeanDefinition parseServiceDefinitionElement(Element element, String serviceName,
			BeanDefinition containingService) {
		parseState.push(new BeanEntry(serviceName));
		String className = null;
		if (element.hasAttribute(CLASS_ATTRIBUTE)) {
			className = element.getAttribute(CLASS_ATTRIBUTE).trim();
		}
		try {
			String parent = null;
			if (element.hasAttribute(PARENT_ATTRIBUTE)) {
				parent = element.getAttribute(PARENT_ATTRIBUTE);
			}
			AbstractBeanDefinition serviceDefinition = createServiceDefinition(className, parent);
			parseServiceDefinitionAttributes(element, serviceName, containingService, serviceDefinition);
			serviceDefinition.setDescription(DomUtils.getChildElementValueByTagName(element, DESCRIPTION_ELEMENT));
			parseMetaElements(element, serviceDefinition);
			parseLookupOverrideSubElements(element, serviceDefinition.getMethodOverrides());
			parseReplacedMethodSubElements(element, serviceDefinition.getMethodOverrides());
			parseConstructorArgElements(element, serviceDefinition);
			parsePropertyElements(element, serviceDefinition);
			parseQualifierElements(element, serviceDefinition);
			serviceDefinition.setResource(this.readerContext.getResource());
			serviceDefinition.setSource(extractSource(element));
			return serviceDefinition;
		} catch (ClassNotFoundException ex) {
			error("Service class [" + className + "] not found", element, ex);
		} catch (NoClassDefFoundError err) {
			error("Class that service class [" + className + "] depends on not found", element, err);
		} catch (Throwable ex) {
			error("Unexpected failure during Service definition parsing", element, ex);
		} finally {
			this.parseState.pop();
		}

		return null;
	}

	public AbstractBeanDefinition parseServiceDefinitionAttributes(Element element, String serviceName,
			BeanDefinition containingService, AbstractBeanDefinition serviceDefinition) {
		if (element.hasAttribute(SCOPE_ATTRIBUTE)) {
			serviceDefinition.setScope(element.getAttribute(SCOPE_ATTRIBUTE));
		} else if (containingService != null) {
			serviceDefinition.setScope(containingService.getScope());
		}
		if (element.hasAttribute(ABSTRACT_ATTRIBUTE)) {
			serviceDefinition.setAbstract(TRUE_VALUE.equals(element.getAttribute(ABSTRACT_ATTRIBUTE)));
		}
		String lazyInit = element.getAttribute(LAZY_INIT_ATTRIBUTE);
		if (DEFAULT_VALUE.equals(lazyInit)) {
			lazyInit = parserContext.getDelegate().getDefaults().getLazyInit();
		}
		serviceDefinition.setLazyInit(TRUE_VALUE.equals(lazyInit));
		String autowire = element.getAttribute(AUTOWIRE_ATTRIBUTE);
		serviceDefinition.setAutowireMode(getAutowireMode(autowire));
		String dependencyCheck = element.getAttribute(DEPENDENCY_CHECK_ATTRIBUTE);
		serviceDefinition.setDependencyCheck(getDependencyCheck(dependencyCheck));
		if (element.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
			String dependsOn = element.getAttribute(DEPENDS_ON_ATTRIBUTE);
			serviceDefinition.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn,
					MULTI_VALUE_ATTRIBUTE_DELIMITERS));
		}
		String autowireCandidate = element.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE);
		if ("".equals(autowireCandidate) || DEFAULT_VALUE.equals(autowireCandidate)) {
			String candidatePattern = parserContext.getDelegate().getDefaults().getAutowireCandidates();
			if (candidatePattern != null) {
				String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
				serviceDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, serviceName));
			}
		} else {
			serviceDefinition.setAutowireCandidate(TRUE_VALUE.equals(autowireCandidate));
		}
		if (element.hasAttribute(PRIMARY_ATTRIBUTE)) {
			serviceDefinition.setPrimary(TRUE_VALUE.equals(element.getAttribute(PRIMARY_ATTRIBUTE)));
		}
		if (element.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
			String initMethodName = element.getAttribute(INIT_METHOD_ATTRIBUTE);
			if (!"".equals(initMethodName)) {
				serviceDefinition.setInitMethodName(initMethodName);
			}
		} else {
			if (parserContext.getDelegate().getDefaults().getInitMethod() != null) {
				serviceDefinition.setInitMethodName(parserContext.getDelegate().getDefaults().getInitMethod());
				serviceDefinition.setEnforceInitMethod(false);
			}
		}
		if (element.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
			String destroyMethodName = element.getAttribute(DESTROY_METHOD_ATTRIBUTE);
			if (!"".equals(destroyMethodName)) {
				serviceDefinition.setDestroyMethodName(destroyMethodName);
			}
		} else {
			if (parserContext.getDelegate().getDefaults().getDestroyMethod() != null) {
				serviceDefinition.setDestroyMethodName(parserContext.getDelegate().getDefaults().getDestroyMethod());
				serviceDefinition.setEnforceDestroyMethod(false);
			}
		}
		if (element.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
			serviceDefinition.setFactoryMethodName(element.getAttribute(FACTORY_METHOD_ATTRIBUTE));
		}
		if (element.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
			serviceDefinition.setFactoryBeanName(element.getAttribute(FACTORY_BEAN_ATTRIBUTE));
		}
		return serviceDefinition;
	}

	protected AbstractBeanDefinition createServiceDefinition(String className, String parentName)
			throws ClassNotFoundException {
		return BeanDefinitionReaderUtils.createBeanDefinition(parentName, className,
				readerContext.getBeanClassLoader());
	}

	public void parseMetaElements(Element element, BeanMetadataAttributeAccessor attributeAccessor) {
		NodeList nl = element.getChildNodes();
		int length = nl.getLength();
		if(length <= 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, META_ELEMENT)) {
				Element metaElement = (Element) node;
				String key = metaElement.getAttribute(KEY_ATTRIBUTE);
				String value = metaElement.getAttribute(VALUE_ATTRIBUTE);
				BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
				attribute.setSource(extractSource(metaElement));
				attributeAccessor.addMetadataAttribute(attribute);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public int getAutowireMode(String attValue) {
		String att = attValue;
		if (DEFAULT_VALUE.equals(att)) {
			att = parserContext.getDelegate().getDefaults().getAutowire();
		}
		int autowire = AbstractBeanDefinition.AUTOWIRE_BY_NAME;
		if (AUTOWIRE_BY_NAME_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_BY_NAME;
		} else if (AUTOWIRE_BY_TYPE_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_BY_TYPE;
		} else if (AUTOWIRE_CONSTRUCTOR_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
		} else if (AUTOWIRE_AUTODETECT_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_AUTODETECT;
		}
		return autowire;
	}

	public int getDependencyCheck(String attValue) {
		String att = attValue;
		if (DEFAULT_VALUE.equals(att)) {
			att = parserContext.getDelegate().getDefaults().getDependencyCheck();
		}
		if (DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE.equals(att)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_ALL;
		} else if (DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE.equals(att)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_OBJECTS;
		} else if (DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE.equals(att)) {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_SIMPLE;
		} else {
			return AbstractBeanDefinition.DEPENDENCY_CHECK_NONE;
		}
	}

	public void parseConstructorArgElements(Element serviceEle, BeanDefinition serviceDefinition) {
		NodeList nl = serviceEle.getChildNodes();
		int length = nl.getLength();
		if(length <= 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, CONSTRUCTOR_ARG_ELEMENT)) {
				parseConstructorArgElement((Element) node, serviceDefinition);
			}
		}
	}

	public void parsePropertyElements(Element serviceEle, BeanDefinition serviceDefinition) {
		NodeList nl = serviceEle.getChildNodes();
		int length = nl.getLength();
		if(length <= 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, PROPERTY_ELEMENT)) {
				parsePropertyElement((Element) node, serviceDefinition);
			}
		}
	}

	public void parseQualifierElements(Element serviceEle, AbstractBeanDefinition serviceDefinition) {
		NodeList nl = serviceEle.getChildNodes();
		int length = nl.getLength();
		if(length <= 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, QUALIFIER_ELEMENT)) {
				parseQualifierElement((Element) node, serviceDefinition);
			}
		}
	}

	public void parseLookupOverrideSubElements(Element serviceEle, MethodOverrides overrides) {
		NodeList nl = serviceEle.getChildNodes();
		int length = nl.getLength();
		if(length <= 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, LOOKUP_METHOD_ELEMENT)) {
				Element ele = (Element) node;
				String methodName = ele.getAttribute(NAME_ATTRIBUTE);
				String serviceRef = ele.getAttribute(SERVICE_ELEMENT);
				LookupOverride override = new LookupOverride(methodName, serviceRef);
				override.setSource(extractSource(ele));
				overrides.addOverride(override);
			}
		}
	}

	public void parseReplacedMethodSubElements(Element serviceEle, MethodOverrides overrides) {
		NodeList nl = serviceEle.getChildNodes();
		int length = nl.getLength();
		if(length <= 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, REPLACED_METHOD_ELEMENT)) {
				Element replacedMethodEle = (Element) node;
				String name = replacedMethodEle.getAttribute(NAME_ATTRIBUTE);
				String callback = replacedMethodEle.getAttribute(REPLACER_ATTRIBUTE);
				ReplaceOverride replaceOverride = new ReplaceOverride(name, callback);
				// Look for arg-type match elements.
				List<Element> argTypeEles = DomUtils.getChildElementsByTagName(replacedMethodEle, ARG_TYPE_ELEMENT);
				for (Element argTypeEle : argTypeEles) {
					String match = argTypeEle.getAttribute(ARG_TYPE_MATCH_ATTRIBUTE);
					match = (StringUtils.hasText(match) ? match : DomUtils.getTextValue(argTypeEle));
					if (StringUtils.hasText(match)) {
						replaceOverride.addTypeIdentifier(match);
					}
				}
				replaceOverride.setSource(extractSource(replacedMethodEle));
				overrides.addOverride(replaceOverride);
			}
		}
	}

	public void parseConstructorArgElement(Element ele, BeanDefinition serviceDefinition) {
		String indexAttr = ele.getAttribute(INDEX_ATTRIBUTE);
		String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);
		String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
		if (StringUtils.hasLength(indexAttr)) {
			try {
				int index = Integer.parseInt(indexAttr);
				if (index < 0) {
					error("'index' cannot be lower than 0", ele);
				} else {
					try {
						this.parseState.push(new ConstructorArgumentEntry(index));
						Object value = parsePropertyValue(ele, serviceDefinition, null);
						ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(
								value);
						if (StringUtils.hasLength(typeAttr)) {
							valueHolder.setType(typeAttr);
						}
						if (StringUtils.hasLength(nameAttr)) {
							valueHolder.setName(nameAttr);
						}
						valueHolder.setSource(extractSource(ele));
						if (serviceDefinition.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
							error("Ambiguous constructor-arg entries for index " + index, ele);
						} else {
							serviceDefinition.getConstructorArgumentValues()
									.addIndexedArgumentValue(index, valueHolder);
						}
					} finally {
						this.parseState.pop();
					}
				}
			} catch (NumberFormatException ex) {
				error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
			}
		} else {
			try {
				this.parseState.push(new ConstructorArgumentEntry());
				Object value = parsePropertyValue(ele, serviceDefinition, null);
				ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
				if (StringUtils.hasLength(typeAttr)) {
					valueHolder.setType(typeAttr);
				}
				if (StringUtils.hasLength(nameAttr)) {
					valueHolder.setName(nameAttr);
				}
				valueHolder.setSource(extractSource(ele));
				serviceDefinition.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
			} finally {
				this.parseState.pop();
			}
		}
	}

	public void parsePropertyElement(Element ele, BeanDefinition serviceDefinition) {
		String propertyName = ele.getAttribute(NAME_ATTRIBUTE);
		if (!StringUtils.hasLength(propertyName)) {
			error("Tag 'property' must have a 'name' attribute", ele);
			return;
		}
		parseState.push(new PropertyEntry(propertyName));
		try {
			if (serviceDefinition.getPropertyValues().contains(propertyName)) {
				error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
				return;
			}
			Object val = parsePropertyValue(ele, serviceDefinition, propertyName);
			PropertyValue pv = new PropertyValue(propertyName, val);
			parseMetaElements(ele, pv);
			pv.setSource(extractSource(ele));
			serviceDefinition.getPropertyValues().addPropertyValue(pv);
		} finally {
			parseState.pop();
		}
	}

	public void parseQualifierElement(Element ele, AbstractBeanDefinition serviceDefinition) {
		String typeName = ele.getAttribute(TYPE_ATTRIBUTE);
		if (!StringUtils.hasLength(typeName)) {
			error("Tag 'qualifier' must have a 'type' attribute", ele);
			return;
		}
		parseState.push(new QualifierEntry(typeName));
		try {
			AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(typeName);
			qualifier.setSource(extractSource(ele));
			String value = ele.getAttribute(VALUE_ATTRIBUTE);
			if (StringUtils.hasLength(value)) {
				qualifier.setAttribute(AutowireCandidateQualifier.VALUE_KEY, value);
			}
			NodeList nl = ele.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (isCandidateElement(node) && nodeNameEquals(node, QUALIFIER_ATTRIBUTE_ELEMENT)) {
					Element attributeEle = (Element) node;
					String attributeName = attributeEle.getAttribute(KEY_ATTRIBUTE);
					String attributeValue = attributeEle.getAttribute(VALUE_ATTRIBUTE);
					if (StringUtils.hasLength(attributeName) && StringUtils.hasLength(attributeValue)) {
						BeanMetadataAttribute attribute = new BeanMetadataAttribute(attributeName, attributeValue);
						attribute.setSource(extractSource(attributeEle));
						qualifier.addMetadataAttribute(attribute);
					} else {
						error("Qualifier 'attribute' tag must have a 'name' and 'value'", attributeEle);
						return;
					}
				}
			}
			serviceDefinition.addQualifier(qualifier);
		} finally {
			parseState.pop();
		}
	}

	public Object parsePropertyValue(Element ele, BeanDefinition serviceDefinition, String propertyName) {
		String elementName = (propertyName != null) ? "<property> element for property '" + propertyName + "'"
				: "<constructor-arg> element";
		NodeList nl = ele.getChildNodes();
		Element subElement = null;
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			if (node instanceof Element && !nodeNameEquals(node, DESCRIPTION_ELEMENT)
					&& !nodeNameEquals(node, META_ELEMENT)) {
				// Child element is what we're looking for.
				if (subElement != null) {
					error(elementName + " must not contain more than one sub-element", ele);
				} else {
					subElement = (Element) node;
				}
			}
		}
		boolean hasRefAttribute = ele.hasAttribute(REF_ATTRIBUTE);
		boolean hasValueAttribute = ele.hasAttribute(VALUE_ATTRIBUTE);
		if ((hasRefAttribute && hasValueAttribute) || ((hasRefAttribute || hasValueAttribute) && subElement != null)) {
			error(elementName
					+ " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element", ele);
		}
		if (hasRefAttribute) {
			String refName = ele.getAttribute(REF_ATTRIBUTE);
			if (!StringUtils.hasText(refName)) {
				error(elementName + " contains empty 'ref' attribute", ele);
			}
			RuntimeBeanReference ref = new RuntimeBeanReference(refName);
			ref.setSource(extractSource(ele));
			return ref;
		} else if (hasValueAttribute) {
			TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute(VALUE_ATTRIBUTE));
			valueHolder.setSource(extractSource(ele));
			return valueHolder;
		} else if (subElement != null) {
			return parsePropertySubElement(subElement, serviceDefinition);
		} else {
			// Neither child element nor "ref" or "value" attribute found.
			error(elementName + " must specify a ref or value", ele);
			return null;
		}
	}

	public Object parsePropertySubElement(Element ele, BeanDefinition bd) {
		return parsePropertySubElement(ele, bd, null);
	}

	public Object parsePropertySubElement(Element ele, BeanDefinition serviceDefinition, String defaultValueType) {
		if (!isDefaultNamespace(ele)) {
			return parseNestedCustomElement(ele, serviceDefinition);
		} else if (nodeNameEquals(ele, SERVICE_ELEMENT)) {
			BeanDefinitionHolder nestedBd = parseServiceDefinitionElement(ele, serviceDefinition);
			if (nestedBd != null) {
				nestedBd = decorateServiceDefinitionIfRequired(ele, nestedBd, serviceDefinition);
			}
			return nestedBd;
		} else if (nodeNameEquals(ele, REF_ELEMENT)) {
			// A generic reference to any name of any bean.
			String refName = ele.getAttribute(SERVICE_REF_ATTRIBUTE);
			boolean toParent = false;
			if (!StringUtils.hasLength(refName)) {
				// A reference to the id of another bean in the same XML file.
				refName = ele.getAttribute(LOCAL_REF_ATTRIBUTE);
				if (!StringUtils.hasLength(refName)) {
					// A reference to the id of another bean in a parent
					// context.
					refName = ele.getAttribute(PARENT_REF_ATTRIBUTE);
					toParent = true;
					if (!StringUtils.hasLength(refName)) {
						error("'service', 'local' or 'parent' is required for <ref> element", ele);
						return null;
					}
				}
			}
			if (!StringUtils.hasText(refName)) {
				error("<ref> element contains empty target attribute", ele);
				return null;
			}
			RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
			ref.setSource(extractSource(ele));
			return ref;
		} else if (nodeNameEquals(ele, IDREF_ELEMENT)) {
			return parseIdRefElement(ele);
		} else if (nodeNameEquals(ele, VALUE_ELEMENT)) {
			return parseValueElement(ele, defaultValueType);
		} else if (nodeNameEquals(ele, NULL_ELEMENT)) {
			// It's a distinguished null value. Let's wrap it in a
			// TypedStringValue
			// object in order to preserve the source location.
			TypedStringValue nullHolder = new TypedStringValue(null);
			nullHolder.setSource(extractSource(ele));
			return nullHolder;
		} else if (nodeNameEquals(ele, ARRAY_ELEMENT)) {
			return parseArrayElement(ele, serviceDefinition);
		} else if (nodeNameEquals(ele, LIST_ELEMENT)) {
			return parseListElement(ele, serviceDefinition);
		} else if (nodeNameEquals(ele, SET_ELEMENT)) {
			return parseSetElement(ele, serviceDefinition);
		} else if (nodeNameEquals(ele, MAP_ELEMENT)) {
			return parseMapElement(ele, serviceDefinition);
		} else if (nodeNameEquals(ele, PROPS_ELEMENT)) {
			return parsePropsElement(ele);
		} else {
			error("Unknown property sub-element: [" + ele.getNodeName() + "]", ele);
			return null;
		}
	}

	public Object parseIdRefElement(Element ele) {
		// A generic reference to any name of any bean.
		String refName = ele.getAttribute(SERVICE_REF_ATTRIBUTE);
		if (!StringUtils.hasLength(refName)) {
			// A reference to the id of another bean in the same XML file.
			refName = ele.getAttribute(LOCAL_REF_ATTRIBUTE);
			if (!StringUtils.hasLength(refName)) {
				error("Either 'bean' or 'local' is required for <idref> element", ele);
				return null;
			}
		}
		if (!StringUtils.hasText(refName)) {
			error("<idref> element contains empty target attribute", ele);
			return null;
		}
		RuntimeBeanNameReference ref = new RuntimeBeanNameReference(refName);
		ref.setSource(extractSource(ele));
		return ref;
	}

	public Object parseValueElement(Element ele, String defaultTypeName) {
		// It's a literal value.
		String value = DomUtils.getTextValue(ele);
		String specifiedTypeName = ele.getAttribute(TYPE_ATTRIBUTE);
		String typeName = specifiedTypeName;
		if (!StringUtils.hasText(typeName)) {
			typeName = defaultTypeName;
		}
		try {
			TypedStringValue typedValue = buildTypedStringValue(value, typeName);
			typedValue.setSource(extractSource(ele));
			typedValue.setSpecifiedTypeName(specifiedTypeName);
			return typedValue;
		} catch (ClassNotFoundException ex) {
			error("Type class [" + typeName + "] not found for <value> element", ele, ex);
			return value;
		}
	}

	protected TypedStringValue buildTypedStringValue(String value, String targetTypeName) throws ClassNotFoundException {
		ClassLoader classLoader = this.readerContext.getBeanClassLoader();
		TypedStringValue typedValue;
		if (!StringUtils.hasText(targetTypeName)) {
			typedValue = new TypedStringValue(value);
		} else if (classLoader != null) {
			Class<?> targetType = ClassUtils.forName(targetTypeName, classLoader);
			typedValue = new TypedStringValue(value, targetType);
		} else {
			typedValue = new TypedStringValue(value, targetTypeName);
		}
		return typedValue;
	}

	public Object parseArrayElement(Element arrayEle, BeanDefinition serviceDefinition) {
		String elementType = arrayEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		NodeList nl = arrayEle.getChildNodes();
		ManagedArray target = new ManagedArray(elementType, nl.getLength());
		target.setSource(extractSource(arrayEle));
		target.setElementTypeName(elementType);
		target.setMergeEnabled(parseMergeAttribute(arrayEle));
		parseCollectionElements(nl, target, serviceDefinition, elementType);
		return target;
	}

	public List<Object> parseListElement(Element collectionEle, BeanDefinition serviceDefinition) {
		String defaultElementType = collectionEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		NodeList nl = collectionEle.getChildNodes();
		ManagedList<Object> target = new ManagedList<Object>(nl.getLength());
		target.setSource(extractSource(collectionEle));
		target.setElementTypeName(defaultElementType);
		target.setMergeEnabled(parseMergeAttribute(collectionEle));
		parseCollectionElements(nl, target, serviceDefinition, defaultElementType);
		return target;
	}

	public Set<Object> parseSetElement(Element collectionEle, BeanDefinition serviceDefinition) {
		String defaultElementType = collectionEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		NodeList nl = collectionEle.getChildNodes();
		ManagedSet<Object> target = new ManagedSet<Object>(nl.getLength());
		target.setSource(extractSource(collectionEle));
		target.setElementTypeName(defaultElementType);
		target.setMergeEnabled(parseMergeAttribute(collectionEle));
		parseCollectionElements(nl, target, serviceDefinition, defaultElementType);
		return target;
	}

	protected void parseCollectionElements(NodeList elementNodes, Collection<Object> target,
			BeanDefinition serviceDefinition, String defaultElementType) {

		for (int i = 0; i < elementNodes.getLength(); i++) {
			Node node = elementNodes.item(i);
			if (node instanceof Element && !nodeNameEquals(node, DESCRIPTION_ELEMENT)) {
				target.add(parsePropertySubElement((Element) node, serviceDefinition, defaultElementType));
			}
		}
	}

	public Map<Object, Object> parseMapElement(Element mapEle, BeanDefinition serviceDefinition) {
		String defaultKeyType = mapEle.getAttribute(KEY_TYPE_ATTRIBUTE);
		String defaultValueType = mapEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		List<Element> entryEles = DomUtils.getChildElementsByTagName(mapEle, ENTRY_ELEMENT);
		ManagedMap<Object, Object> map = new ManagedMap<Object, Object>(entryEles.size());
		map.setSource(extractSource(mapEle));
		map.setKeyTypeName(defaultKeyType);
		map.setValueTypeName(defaultValueType);
		map.setMergeEnabled(parseMergeAttribute(mapEle));
		for (Element entryEle : entryEles) {
			// Should only have one value child element: ref, value, list, etc.
			// Optionally, there might be a key child element.
			NodeList entrySubNodes = entryEle.getChildNodes();
			Element keyEle = null;
			Element valueEle = null;
			for (int j = 0; j < entrySubNodes.getLength(); j++) {
				Node node = entrySubNodes.item(j);
				if (node instanceof Element) {
					Element candidateEle = (Element) node;
					if (nodeNameEquals(candidateEle, KEY_ELEMENT)) {
						if (keyEle != null) {
							error("<entry> element is only allowed to contain one <key> sub-element", entryEle);
						} else {
							keyEle = candidateEle;
						}
					} else {
						// Child element is what we're looking for.
						if (nodeNameEquals(candidateEle, DESCRIPTION_ELEMENT)) {
							// the element is a <description> -> ignore it
						} else if (valueEle != null) {
							error("<entry> element must not contain more than one value sub-element", entryEle);
						} else {
							valueEle = candidateEle;
						}
					}
				}
			}
			// Extract key from attribute or sub-element.
			Object key = null;
			boolean hasKeyAttribute = entryEle.hasAttribute(KEY_ATTRIBUTE);
			boolean hasKeyRefAttribute = entryEle.hasAttribute(KEY_REF_ATTRIBUTE);
			if ((hasKeyAttribute && hasKeyRefAttribute) || ((hasKeyAttribute || hasKeyRefAttribute)) && keyEle != null) {
				error("<entry> element is only allowed to contain either "
						+ "a 'key' attribute OR a 'key-ref' attribute OR a <key> sub-element", entryEle);
			}
			if (hasKeyAttribute) {
				key = buildTypedStringValueForMap(entryEle.getAttribute(KEY_ATTRIBUTE), defaultKeyType, entryEle);
			} else if (hasKeyRefAttribute) {
				String refName = entryEle.getAttribute(KEY_REF_ATTRIBUTE);
				if (!StringUtils.hasText(refName)) {
					error("<entry> element contains empty 'key-ref' attribute", entryEle);
				}
				RuntimeBeanReference ref = new RuntimeBeanReference(refName);
				ref.setSource(extractSource(entryEle));
				key = ref;
			} else if (keyEle != null) {
				key = parseKeyElement(keyEle, serviceDefinition, defaultKeyType);
			} else {
				error("<entry> element must specify a key", entryEle);
			}
			// Extract value from attribute or sub-element.
			Object value = null;
			boolean hasValueAttribute = entryEle.hasAttribute(VALUE_ATTRIBUTE);
			boolean hasValueRefAttribute = entryEle.hasAttribute(VALUE_REF_ATTRIBUTE);
			boolean hasValueTypeAttribute = entryEle.hasAttribute(VALUE_TYPE_ATTRIBUTE);
			if ((hasValueAttribute && hasValueRefAttribute) || ((hasValueAttribute || hasValueRefAttribute))
					&& valueEle != null) {
				error("<entry> element is only allowed to contain either "
						+ "'value' attribute OR 'value-ref' attribute OR <value> sub-element", entryEle);
			}
			if ((hasValueTypeAttribute && hasValueRefAttribute) || (hasValueTypeAttribute && !hasValueAttribute)
					|| (hasValueTypeAttribute && valueEle != null)) {
				error("<entry> element is only allowed to contain a 'value-type' "
						+ "attribute when it has a 'value' attribute", entryEle);
			}
			if (hasValueAttribute) {
				String valueType = entryEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
				if (!StringUtils.hasText(valueType)) {
					valueType = defaultValueType;
				}
				value = buildTypedStringValueForMap(entryEle.getAttribute(VALUE_ATTRIBUTE), valueType, entryEle);
			} else if (hasValueRefAttribute) {
				String refName = entryEle.getAttribute(VALUE_REF_ATTRIBUTE);
				if (!StringUtils.hasText(refName)) {
					error("<entry> element contains empty 'value-ref' attribute", entryEle);
				}
				RuntimeBeanReference ref = new RuntimeBeanReference(refName);
				ref.setSource(extractSource(entryEle));
				value = ref;
			} else if (valueEle != null) {
				value = parsePropertySubElement(valueEle, serviceDefinition, defaultValueType);
			} else {
				error("<entry> element must specify a value", entryEle);
			}

			// Add final key and value to the Map.
			map.put(key, value);
		}
		return map;
	}

	protected final Object buildTypedStringValueForMap(String value, String defaultTypeName, Element entryEle) {
		try {
			TypedStringValue typedValue = buildTypedStringValue(value, defaultTypeName);
			typedValue.setSource(extractSource(entryEle));
			return typedValue;
		} catch (ClassNotFoundException ex) {
			error("Type class [" + defaultTypeName + "] not found for Map key/value type", entryEle, ex);
			return value;
		}
	}

	protected Object parseKeyElement(Element keyEle, BeanDefinition serviceDefinition, String defaultKeyTypeName) {
		NodeList nl = keyEle.getChildNodes();
		Element subElement = null;
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				// Child element is what we're looking for.
				if (subElement != null) {
					error("<key> element must not contain more than one value sub-element", keyEle);
				} else {
					subElement = (Element) node;
				}
			}
		}
		return parsePropertySubElement(subElement, serviceDefinition, defaultKeyTypeName);
	}

	public Properties parsePropsElement(Element propsEle) {
		ManagedProperties props = new ManagedProperties();
		props.setSource(extractSource(propsEle));
		props.setMergeEnabled(parseMergeAttribute(propsEle));

		List<Element> propEles = DomUtils.getChildElementsByTagName(propsEle, PROP_ELEMENT);
		for (Element propEle : propEles) {
			String key = propEle.getAttribute(KEY_ATTRIBUTE);
			// Trim the text value to avoid unwanted whitespace
			// caused by typical XML formatting.
			String value = DomUtils.getTextValue(propEle).trim();
			TypedStringValue keyHolder = new TypedStringValue(key);
			keyHolder.setSource(extractSource(propEle));
			TypedStringValue valueHolder = new TypedStringValue(value);
			valueHolder.setSource(extractSource(propEle));
			props.put(keyHolder, valueHolder);
		}
		return props;
	}

	public boolean parseMergeAttribute(Element collectionElement) {
		String value = collectionElement.getAttribute(MERGE_ATTRIBUTE);
		if (DEFAULT_VALUE.equals(value)) {
			value = parserContext.getDelegate().getDefaults().getMerge();
		}
		return TRUE_VALUE.equals(value);
	}

	public BeanDefinition parseCustomElement(Element ele) {
		return parseCustomElement(ele, null);
	}

	public BeanDefinition parseCustomElement(Element ele, BeanDefinition containingServiceDefinition) {
		String namespaceUri = getNamespaceURI(ele);
		NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
		if (handler == null) {
			error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
			return null;
		}
		return handler.parse(ele, new ParserContext(this.readerContext, parserContext.getDelegate(),
				containingServiceDefinition));
	}

	public BeanDefinitionHolder decorateServiceDefinitionIfRequired(Element ele, BeanDefinitionHolder definitionHolder) {
		return decorateServiceDefinitionIfRequired(ele, definitionHolder, null);
	}

	public BeanDefinitionHolder decorateServiceDefinitionIfRequired(Element ele, BeanDefinitionHolder definitionHolder,
			BeanDefinition containingServiceDefinition) {
		BeanDefinitionHolder finalDefinition = definitionHolder;
		// Decorate based on custom attributes first.
		NamedNodeMap attributes = ele.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			finalDefinition = decorateIfRequired(node, finalDefinition, containingServiceDefinition);
		}
		// Decorate based on custom nested elements.
		NodeList children = ele.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				finalDefinition = decorateIfRequired(node, finalDefinition, containingServiceDefinition);
			}
		}
		return finalDefinition;
	}

	public BeanDefinitionHolder decorateIfRequired(Node node, BeanDefinitionHolder originalDef,
			BeanDefinition containingServiceDefinition) {
		String namespaceUri = getNamespaceURI(node);
		if (!isDefaultNamespace(namespaceUri)) {
			NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
			if (handler != null) {
				return handler
						.decorate(node, originalDef, new ParserContext(this.readerContext, parserContext.getDelegate(),
								containingServiceDefinition));
			} else if (namespaceUri != null && namespaceUri.startsWith("http://www.springframework.org/")) {
				error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", node);
			} else {
				// A custom namespace, not to be handled by Spring - maybe
				// "xml:...".
				if (log.isDebugEnabled()) {
					log.debug("No Spring NamespaceHandler found for XML schema namespace [" + namespaceUri + "]");
				}
			}
		}
		return originalDef;
	}

	private BeanDefinitionHolder parseNestedCustomElement(Element ele, BeanDefinition containingServiceDefinition) {
		BeanDefinition innerDefinition = parseCustomElement(ele, containingServiceDefinition);
		if (innerDefinition == null) {
			error("Incorrect usage of element '" + ele.getNodeName() + "' in a nested manner. "
					+ "This tag cannot be used nested inside <property>.", ele);
			return null;
		}
		String id = ele.getNodeName() + BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR
				+ ObjectUtils.getIdentityHexString(innerDefinition);
		if (log.isDebugEnabled()) {
			log.debug("Using generated Service name [" + id + "] for nested custom element '" + ele.getNodeName() + "'");
		}
		return new BeanDefinitionHolder(innerDefinition, id);
	}

	public String getNamespaceURI(Node node) {
		return node.getNamespaceURI();
	}

	public String getLocalName(Node node) {
		return node.getLocalName();
	}

	public boolean nodeNameEquals(Node node, String desiredName) {
		return desiredName.equals(node.getNodeName()) || desiredName.equals(getLocalName(node));
	}

	public boolean isDefaultNamespace(String namespaceUri) {
		return (!StringUtils.hasLength(namespaceUri) || SERVICES_NAMESPACE_URI.equals(namespaceUri));
	}

	public boolean isDefaultNamespace(Node node) {
		return isDefaultNamespace(getNamespaceURI(node));
	}

	private boolean isCandidateElement(Node node) {
		return (node instanceof Element && (isDefaultNamespace(node) || !isDefaultNamespace(node.getParentNode())));
	}

	public final XmlReaderContext getReaderContext() {
		return this.readerContext;
	}

	public final Environment getEnvironment() {
		return this.environment;
	}

	protected Object extractSource(Element ele) {
		return this.readerContext.extractSource(ele);
	}

	protected void error(String message, Node source) {
		this.readerContext.error(message, source, this.parseState.snapshot());
	}

	protected void error(String message, Element source) {
		this.readerContext.error(message, source, this.parseState.snapshot());
	}

	protected void error(String message, Element source, Throwable cause) {
		this.readerContext.error(message, source, this.parseState.snapshot(), cause);
	}
}