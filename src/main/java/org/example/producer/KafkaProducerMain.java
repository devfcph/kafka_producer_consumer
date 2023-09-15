package org.example.producer;

import org.apache.kafka.clients.producer.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.StringSerializer;


public class KafkaProducerMain {
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topicName = "TP-PAGOS-CAJA-CATALOGOS-SUCURSALES-V1-AUTOSERVICIOS-ADD";

        // Configuración del productor
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Garantía de entrega de al menos una vez
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10); // Tamaño del lote

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 100; i++) {
                String message = buildMessage(i);
                ProducerRecord<String, String> record = new ProducerRecord<>(topicName, message);

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


            ObjectNode transaccionNode = objectMapper.createObjectNode();
            transaccionNode.put("id", String.valueOf(UUID.randomUUID()));
            transaccionNode.put("fechaHora", getTimestamp());
            transaccionNode.put("nombre", "PAGOS-CAJA-CATALOGOS-SUCURSALES-V1-AUTOSERVICIOS");
            transaccionNode.put("accion", "ADD");
            transaccionNode.put("fechaInicioProceso", "2023-01-02T15:18:26.400-06:00");
            transaccionNode.put("fechaFinProceso", "2023-01-02T15:18:26.400-06:00");


            ObjectNode operacionNode = objectMapper.createObjectNode();
            operacionNode.put("idPais", 1);
            operacionNode.put("idCanal", 1);
            operacionNode.put("numero", i + 1);
            operacionNode.put("numeroCajero", 2);
            operacionNode.put("numeroMultifuncional", 3);
            operacionNode.put("numeroCajeroAutoservicio", 3);
            operacionNode.put("anioSemanaPago", 202318);
            operacionNode.put("multifuncional", new Random().nextBoolean());
            operacionNode.put("metaAutoservicio", 500);
            operacionNode.put("fechaHoraRegistroCentral", getTimestamp());


            messageNode.set("transaccion", transaccionNode);
            messageNode.set("operacion", operacionNode);

            return objectMapper.writeValueAsString(messageNode);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getTimestamp() {
        return String.valueOf(new Timestamp(new Date().getTime()));
    }
}
