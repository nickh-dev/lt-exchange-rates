package com.example.demo.config;

import com.example.demo.client.FxRatesClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SOAPConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.demo.wsdl");
        return marshaller;
    }

    @Bean
    public FxRatesClient fxRatesClient(Jaxb2Marshaller marshaller) {
        FxRatesClient client = new FxRatesClient();
        client.setDefaultUri("https://www.lb.lt/webservices/FxRates/FxRates.asmx");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
