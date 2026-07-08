package com.grup6.telco_ticket_analyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI telcoTicketAnalyzerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telco Ticket Analyzer API")
                        .version("v1")
                        .description("Telekom müşteri destek taleplerini (ticket) yöneten ve analiz eden API. "
                                + "Müşteri, ticket, temsilci (agent) ve bölgesel içgörü verilerine erişim sağlar; "
                                + "frontend ekibinin ihtiyaç duyacağı tüm uçlar aşağıdaki gruplarda listelenmiştir."));
    }
}
