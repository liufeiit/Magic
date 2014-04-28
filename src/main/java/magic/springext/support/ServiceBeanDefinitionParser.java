package magic.springext.support;

import magic.service.HelloService;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.Conventions;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Spring Extension Service XML Parser.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月26日 下午2:33:48
 */
public class ServiceBeanDefinitionParser extends AbstractBeanDefinitionParser {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/service-demo.xml");
		HelloService helloService = context.getBean("hello", HelloService.class);
		System.out.println(helloService.say());
	}
	
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		String clazz = element.getAttribute("class");
		BeanDefinitionBuilder service = null;
		try {
			service = BeanDefinitionBuilder.rootBeanDefinition(ClassUtils.forName(clazz, getClass().getClassLoader()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (LinkageError e) {
			e.printStackTrace();
		}
		NodeList props = element.getElementsByTagName("property");
		int len = props.getLength();
		for (int i = 0; i < len ; i++) {
			Element e = Element.class.cast(props.item(i));
			String propertyName = Conventions.attributeNameToPropertyName(e.getAttribute("name"));
			String propertyValue = e.getAttribute("value");
			String propertyRef = e.getAttribute("ref");
			if (!StringUtils.isEmpty(propertyRef)) {
				service.addPropertyValue(propertyName, new RuntimeBeanReference(propertyRef));
			} else {
				service.addPropertyValue(propertyName, propertyValue);
			}
		}
		AbstractBeanDefinition beanDefinition = service.getBeanDefinition();
		System.out.println("Registry : " + (parserContext.getRegistry()));
//		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}
}