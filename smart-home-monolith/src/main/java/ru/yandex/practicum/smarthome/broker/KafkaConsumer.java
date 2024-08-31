package ru.yandex.practicum.smarthome.broker;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.service.ProcedureService;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    @Value( "${spring.kafka.consumer.request-topic}" )
    private String[] topics;

    @Value( "${spring.kafka.consumer.group-id}" )
    private String[] groupId;

    private final ProcedureService procedureService;

    @KafkaListener(topics = "${spring.kafka.consumer.request-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, DeviceMessage> record){
        System.out.println("Consumed message: " + record.value().getDeviceId());

        var correlationId = record.key();
        var message = record.value();

        procedureService.execute(correlationId, message);
    }
}
