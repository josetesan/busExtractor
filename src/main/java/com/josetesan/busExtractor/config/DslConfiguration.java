package com.josetesan.busExtractor.config;

import com.josetesan.busExtractor.beans.RowEventMapper;
import org.apache.avro.generic.GenericRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHandler;

import javax.sql.DataSource;


@Configuration
@EnableIntegration
public class DslConfiguration {


    private static final String SELECT_QUERY  = "select * from databus_event where processed = 0";


    @Bean
    public JdbcPollingChannelAdapter jdbPollingChannelAdapter(DataSource datasource) {

        JdbcPollingChannelAdapter jdbcPollingChannelAdapter = new JdbcPollingChannelAdapter(datasource, SELECT_QUERY);

        jdbcPollingChannelAdapter.setRowMapper(new RowEventMapper());
        jdbcPollingChannelAdapter.setUpdateSql("UPDATE DATABUS_EVENT SET PROCESSED = 1 where ID in (:id)");
        jdbcPollingChannelAdapter.setMaxRows(5);

        return jdbcPollingChannelAdapter;
    }



    @Bean
    public IntegrationFlow pollingAdapterFlow(JdbcPollingChannelAdapter jdbcPollingChannelAdapter) {
        return IntegrationFlows
                .from(jdbcPollingChannelAdapter,e -> e.poller(poller()))
                .channel(c -> c.flux("pollingEvents"))
                .get();
    }



    @Bean
    public IntegrationFlow splitter() {
        return IntegrationFlows
                .from("pollingEvents")
                .split()
                .channel(c-> c.flux("event"))
                .get();
    }



    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedDelay(30000).maxMessagesPerPoll(2).get();
    }

    @Bean
    public IntegrationFlow logger() {
        return IntegrationFlows
                .from("event")
                .log()
                .get();
    }


//    @Transformer(inputChannel = "event", outputChannel = "avro")
//    @Bean
//    public Message tranformObject(Message source) {
//
//    }


    @ServiceActivator(inputChannel = "avro")
    @Bean
    public MessageHandler handler(KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        KafkaProducerMessageHandler<String, GenericRecord> handler =
                new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setTopicExpression(new LiteralExpression("jdbc-topic"));
        return handler;
    }

}
