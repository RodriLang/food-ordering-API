package com.group_three.food_ordering.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Food Ordering API",
                version = "1.0",
                description = "API para gestionar pedidos de comida, usuarios y pagos.",
                contact = @Contact(
                        name = "Facundo Aguilera, Rodrigo Lang, Ezequiel Santalla, Yago Sosa",
                        email = "grupo3.apifinal@utn.com",
                        url = "https://github.com/RodriLang/food-ordering-API"
                )
        )
)
public class OpenApiConfig {}
