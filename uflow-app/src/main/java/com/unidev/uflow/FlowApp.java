package com.unidev.uflow;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidev.platform.Randoms;
import com.unidev.uflow.model.FlowProcessors;
import com.unidev.uflow.service.SQSMq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@SpringBootApplication
@ComponentScan("com.unidev.uflow")
@EnableWebMvc
@Slf4j
@EnableScheduling
public class FlowApp {

    public static void main(String[] args) {

        SpringApplication.run(FlowApp.class, args);
    }

    @Bean
    public Randoms randoms() {
        return new Randoms();
    }

    @Bean
    public ThreadPoolTaskScheduler scheduledExecutorService() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(100);
        scheduler.setThreadNamePrefix("uflow");
        return scheduler;
    }

    @Bean
    public SQSConnectionFactory connectionFactory() {
        return SQSConnectionFactory.builder()
                .withRegion(Region.getRegion(Regions.US_EAST_1))
                .withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    public JmsTemplate jmsTemplate(SQSConnectionFactory sqsConnectionFactory) {
        return new JmsTemplate(sqsConnectionFactory);
    }

    @Bean
    public Mq mqService(
            ObjectMapper objectMapper,
            SQSConnectionFactory sqsConnectionFactory,
            JmsTemplate jmsTemplate,
            FlowProcessors flowProcessors
    ) {
        return new SQSMq(objectMapper, sqsConnectionFactory, jmsTemplate, flowProcessors);
    }

    @Bean
    public FlowCore flowCore(
            @Autowired Mq mqService) {
        return new FlowCore(mqService);
    }


}
