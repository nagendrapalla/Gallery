package com.palla.gallery.messaging;

import com.palla.gallery.dto.KafkaEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private static final String TOPIC = "imgur_api_topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void writeMessage(KafkaEventDto kafkaEventDto) {
        this.kafkaTemplate.send(TOPIC, kafkaEventDto.toString());
    }

}
