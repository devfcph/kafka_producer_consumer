package org.example.producer;

import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.StringSerializer;


public class KafkaProducerExample {
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092"; // Cambia esto por la dirección de tu servidor Kafka
        String topicName = "TP-PAGOS-CAJA-CATALOGOS-SUCURSALES-V1-AUTOSERVICIOS-ADD";

        // Configuración del productor
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Garantía de entrega de al menos una vez
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10); // Tamaño del lote (10 en este caso)

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 100; i++) {
                // Construye tu mensaje de acuerdo al esquema
                String message = buildMessage(i);
                ProducerRecord<String, String> record = new ProducerRecord<>(topicName, message);

                // Envía el mensaje al topic
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception == null) {
                            System.out.printf("Mensaje enviado: offset = %d, partition = %d%n",
                                    metadata.offset(), metadata.partition());
                        } else {
                            System.err.println("Error al enviar el mensaje: " + exception.getMessage());
                        }
                    }
                });
            }
        } finally {
            producer.close();
        }
    }

    private static String buildMessage(int i) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode messageNode = objectMapper.createObjectNode();

            // Construye el objeto 'transaccionautoservicios'
            ObjectNode transaccionNode = objectMapper.createObjectNode();
            transaccionNode.put("id", "4924f0a2-6b22-4690-b01a-0ff215b7ac10");
            transaccionNode.put("fechaHora", "2023-01-02T15:18:26.400-06:00");
            transaccionNode.put("nombre", "PAGOS-CAJA-CATALOGOS-SUCURSALES-V1-AUTOSERVICIOS");
            transaccionNode.put("accion", "ADD");
            transaccionNode.put("fechaInicioProceso", "2023-01-02T15:18:26.400-06:00");
            transaccionNode.put("fechaFinProceso", "2023-01-02T15:18:26.400-06:00");

            // Construye el objeto 'operacion'
            ObjectNode operacionNode = objectMapper.createObjectNode();
            operacionNode.put("idPais", 1);
            operacionNode.put("idCanal", 1);
            operacionNode.put("numero", 4624);
            operacionNode.put("numeroCajero", 2);
            operacionNode.put("numeroMultifuncional", 3);
            operacionNode.put("numeroCajeroAutoservicio", 3);
            operacionNode.put("anioSemanaPago", 202318);
            operacionNode.put("multifuncional", true);
            operacionNode.put("metaAutoservicio", 500);
            operacionNode.put("fechaHoraRegistroCentral", "2023-05-08T15:18:26.400-06:00");

            // Combina 'transaccion' y 'operacion' en el mensaje
            messageNode.set("transaccion", transaccionNode);
            messageNode.set("operacion", operacionNode);

            return objectMapper.writeValueAsString(messageNode);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
