package org.example.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerMain {
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topicName = "TP-PAGOS-CAJA-CATALOGOS-SUCURSALES-V1-AUTOSERVICIOS-ADD";
        String groupId = "mi-grupo";

        // Configuración del consumidor
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));

        ObjectMapper objectMapper = new ObjectMapper();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("Offset = %d, Key = %s, Value = %s%n", record.offset(), record.key(), record.value());

                try {
                    // Parsea el mensaje JSON
                    JsonNode jsonNode = objectMapper.readTree(record.value());

                    // Extrae los datos que necesitas del mensaje
                    String transaccionId = jsonNode.get("transaccion").get("id").asText();
                    String fechaHora = jsonNode.get("transaccion").get("fechaHora").asText();
                    String accion = jsonNode.get("transaccion").get("accion").asText();

                    // Realiza las operaciones necesarias con los datos
                    System.out.println("Transacción ID: " + transaccionId);
                    System.out.println("Fecha y Hora: " + fechaHora);
                    System.out.println("Acción: " + accion);
                    System.out.println("-----------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
