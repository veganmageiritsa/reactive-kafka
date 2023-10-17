package com.nl.analyticsservice;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.List;
import java.util.function.UnaryOperator;

@SpringBootTest
@Testcontainers
@EmbeddedKafka(
        topics = {AbstractIntegrationTest.PRODUCT_VIEW_EVENTS},
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public class AbstractIntegrationTest {

    protected static final String PRODUCT_VIEW_EVENTS = "product-view-events";

    @Autowired
    private EmbeddedKafkaBroker broker;

    private static final String KEYSPACE_NAME = "test";

    @Container
    private static final CassandraContainer cassandra = (CassandraContainer) new CassandraContainer("cassandra:3.11.2")
            .withExposedPorts(9042);

    @BeforeAll
    static void setupCassandraConnectionProperties() {
        System.setProperty("spring.cassandra.keyspace-name", KEYSPACE_NAME);
        System.setProperty("spring.cassandra.contact-points", cassandra.getContainerIpAddress());
        System.setProperty("spring.cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));

        createKeyspace(cassandra.getCluster());
    }

    static void createKeyspace(Cluster cluster) {
        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME + " WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    protected <V> KafkaReceiver<String, V> createReceiver(String... topics) {
        return createReceiver(options ->
                options.withKeyDeserializer(new StringDeserializer())
                        .withValueDeserializer(new JsonDeserializer<V>().trustedPackages("*"))
                        .subscription(List.of(topics))
        );
    }

    protected <K, V> KafkaReceiver<K, V> createReceiver(UnaryOperator<ReceiverOptions<K, V>> builder) {
        var props = KafkaTestUtils.consumerProps("test-group", "true", broker);
        var options = ReceiverOptions.<K, V>create(props);
        options = builder.apply(options);
        return KafkaReceiver.create(options);
    }

    protected <V> KafkaSender<String, V> createSender() {
        return createSender(options ->
                options.withKeySerializer(new StringSerializer())
                        .withValueSerializer(new JsonSerializer<V>())
        );
    }

    protected <K, V> KafkaSender<K, V> createSender(UnaryOperator<SenderOptions<K, V>> builder) {
        var props = KafkaTestUtils.producerProps(broker);
        var options = SenderOptions.<K, V>create(props);
        options = builder.apply(options);
        return KafkaSender.create(options);
    }

    protected <K, V> SenderRecord<K, V, K> toSenderRecord(String topic, K key, V value) {
        return SenderRecord.create(topic, null, null, key, value, key);
    }

}