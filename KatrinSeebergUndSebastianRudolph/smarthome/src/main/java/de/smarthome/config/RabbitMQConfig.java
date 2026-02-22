package de.smarthome.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfig {
    
    // Exchange und Queues für jedes Event definieren
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("smarthome-exchange");
    }

    @Bean
    public Queue lightQueue() {
        return new Queue("light-queue");
    }

    @Bean
    public Queue heatingQueue() {
        return new Queue("heating-queue");
    }

    @Bean
    public Queue queryQueue() {
        return new Queue("query-queue");
    }

    // Bindings für die Queues definieren
    @Bean
    public Binding lightBinding(Queue lightQueue, TopicExchange exchange) {
        return BindingBuilder.bind(lightQueue).to(exchange).with("person.#");
    }

    @Bean
    public Binding heatingBinding(Queue heatingQueue, TopicExchange exchange) {
        return BindingBuilder.bind(heatingQueue).to(exchange).with("person.#");
    }

    @Bean
    public Binding queryBinding(Queue queryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(queryQueue).to(exchange).with("person.#");
    }

    //Jackson2JsonMessageConverter, zwat deprecated, aber noch nutzbar für diese Aufgabe, (nötig sonst Fehler wegen LocalTime)
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
