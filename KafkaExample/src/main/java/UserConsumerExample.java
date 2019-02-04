import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

public class UserConsumerExample {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "UserDeserializer");
    props.put("group.id", "test2");
    KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList("user-topic"));
    while (true) {
      ConsumerRecords<String, User> records = consumer.poll(100);
      for (ConsumerRecord<String, User> record : records)
        System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
    }
  }
}
