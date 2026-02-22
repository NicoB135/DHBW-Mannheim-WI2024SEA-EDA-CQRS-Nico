package smarthome.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "smarthome.exchange";
    public static final String Q_COMMANDS = "q.commands";

    public static final String Q_EVENTS_ACTUATORS = "q.events.actuators";
    public static final String Q_EVENTS_QUERY     = "q.events.query";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }


    @Bean
    public Queue commandsQueue() {
        return new Queue(Q_COMMANDS, true);
    }

    @Bean
    public Queue eventsActuatorsQueue() {
        return new Queue(Q_EVENTS_ACTUATORS, true);
    }

    @Bean
    public Queue eventsQueryQueue() {
        return new Queue(Q_EVENTS_QUERY, true);
    }

    @Bean
    public Binding bindCommands(TopicExchange ex) {
        return BindingBuilder
                .bind(commandsQueue())
                .to(ex)
                .with("cmd.#");
    }

    @Bean
    public Binding bindEventsActuators(TopicExchange ex) {
        return BindingBuilder
                .bind(eventsActuatorsQueue())
                .to(ex)
                .with("evt.#");
    }

    @Bean
    public Binding bindEventsQuery(TopicExchange ex) {
        return BindingBuilder
                .bind(eventsQueryQueue())
                .to(ex)
                .with("evt.#");
    }


    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter conv
    ) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(conv);
        return tpl;
    }
}