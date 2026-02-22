package dhbw.audiostreamer.config;

import java.util.List;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
	public static final String QUEUE = "dhbw_volume_queue";
	public static final String EXCHANGE = "dhbw_exchange";
	
	@Bean 
	public Queue queue() {
		return new Queue(QUEUE,false);
	}
	
	@Bean 
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE);
	}
	
	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("audio.measured");
	}
	
	@Bean
	public SimpleMessageConverter messageConverter() {
		SimpleMessageConverter converter = new SimpleMessageConverter();
		converter.setAllowedListPatterns(List.of("dhbw.*","java.*"));
		return converter;
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, SimpleMessageConverter messageConverter ) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}
}
