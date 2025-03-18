package com.github.rag.tutorials.helpdesk.infrastructure.config.camel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
@Setter
public class WhatsAppChannelConfig {

    @Bean
    public RouteBuilder whatsAppRoutes() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:whatsAppInput")
                        .id("whatsAppInputRoute")
                        .log("Processing WhatsApp message: ${body}")
                        .to("bean:whatsAppInputProcessor");

                from("direct:whatsAppOutput")
                        .routeId("whatsAppOutputRoute")
                        .log("Sending response to WhatsApp: ${body}")
                        .bean("whatsAppOutputProcessor")
                        .to("bean:whatsAppSender");
            }
        };
    }
}
