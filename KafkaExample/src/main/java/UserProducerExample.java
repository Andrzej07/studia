import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class UserProducerExample {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "UserSerializer");

    Producer<String, User> producer = new KafkaProducer<>(props);

    for (int i = 0; i < 100; i++)
      producer.send(new ProducerRecord<>("user-topic", new User("John", i)));

    producer.close();
  }
}
