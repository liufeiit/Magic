package magic.springext.support;

import magic.service.HelloService;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Element;

/**
 * ServiceDefinitionParser.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年5月18日 下午9:52:55
 */
public class ServiceDefinitionParser implements BeanDefinitionParser {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/service-1.1.0-demo.xml");
		HelloService helloService = context.getBean("hello", HelloService.class);
		System.out.println(helloService.say());
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionHolder definitionHolder = new ServiceDefinitionParserDelegate(parserContext)
				.parseServiceDefinitionElement(element);
		if (definitionHolder == null) {
			return null;
		}
		if (parserContext.isNested()) {
			return definitionHolder.getBeanDefinition();
		}
		try {
			registerBeanDefinition(definitionHolder, parserContext.getRegistry());
			if (shouldFireEvents()) {
				BeanComponentDefinition componentDefinition = new BeanComponentDefinition(definitionHolder);
				postProcessComponentDefinition(componentDefinition);
				parserContext.registerComponent(componentDefinition);
			}
		} catch (BeanDefinitionStoreException ex) {
			parserContext.getReaderContext().error(ex.getMessage(), element);
			return null;
		}
		return definitionHolder.getBeanDefinition();
	}

	protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
	}

	protected boolean shouldFireEvents() {
		return true;
	}

	protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {

	}
}
