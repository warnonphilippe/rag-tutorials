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
public class ChatChannelConfig {

    @Bean
    public RouteBuilder chatRoutes() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:webChatInput")
                        .id("webChatInputRoute")
                        .log("Processing web chat message: ${body}")
                        .to("bean:chatInputProcessor");

                from("direct:webChatOutput")
                        .id("webChatOutputRoute")
                        .log("Sending response to web chat: ${body}")
                        .to("bean:chatOutputProcessor");
            }
        };
    }
}
