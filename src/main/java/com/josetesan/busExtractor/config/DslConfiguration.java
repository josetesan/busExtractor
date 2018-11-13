package com.josetesan.busExtractor.config;

import com.josetesan.busExtractor.beans.RowEventMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import javax.sql.DataSource;


@Configuration
@EnableIntegration
public class DslConfiguration {


    private static final String SELECT_QUERY  = "select * from databus_event where processed is false";


    @Autowired
    private DataSource datasource;


    @Bean
    public JdbcPollingChannelAdapter jdbPollingChannelAdapter() {

        JdbcPollingChannelAdapter jdbcPollingChannelAdapter = new JdbcPollingChannelAdapter(datasource, SELECT_QUERY);

        jdbcPollingChannelAdapter.setRowMapper(new RowEventMapper());
        jdbcPollingChannelAdapter.setUpdateSql("UPDATE DATABUS_EVENT SET PROCESSED=true where ID in (:id)");
        jdbcPollingChannelAdapter.setMaxRows(5);

        return jdbcPollingChannelAdapter;
    }



    @Bean
    public IntegrationFlow pollingAdapterFlow() {
        return IntegrationFlows
                .from(jdbPollingChannelAdapter(),e -> e.poller(poller()))
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



    @Bean
    public IntegrationFlow amqpOutboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from("event")
                .transform(new ObjectToJsonTransformer())
                .handle(Amqp
                        .outboundAdapter(amqpTemplate)
//                        .routingKeyExpression("headers.routingKey")
                        .exchangeName("jdbc-event"))
                .get();
    }

}
