package com.quebecteh.modules;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BModulePicklistApplication extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BModulePicklistApplication.class);
    }

	public static void main(String[] args) {
		//SpringApplication.run(BModulePicklistApplication.class, args);
		
		 // Inicia a aplicação Spring Boot
        var context = SpringApplication.run(BModulePicklistApplication.class, args);

        // Cria um contexto de configuração e registra o pacote
        AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
        annotationContext.scan("com.quebecteh.connectors");

        // Atualiza o contexto para aplicar a configuração
        annotationContext.refresh();

        // Mescla o contexto anotado com o contexto principal
        ((GenericApplicationContext) context).registerBean(AnnotationConfigApplicationContext.class, () -> annotationContext);
	}
}
