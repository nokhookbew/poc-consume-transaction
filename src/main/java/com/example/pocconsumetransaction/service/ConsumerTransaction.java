package com.example.pocconsumetransaction.service;

import com.example.pocconsumetransaction.model.Record;
import com.example.pocconsumetransaction.repository.RecordRepository;
import com.example.pocconsumetransaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class ConsumerTransaction {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Bean
    public Consumer<Message<List<Record>>> processMessage() {
        return message -> {
            System.out.println("Received message: " + message.getPayload());
            recordRepository.batchInsert(message.getPayload());
            System.out.println("Batch record inserted");
        };
    }

    @Bean
    public Consumer<Message<List<Record>>> submitMessage() {
        return message -> {
            System.out.println("Received message submitMessage : " + message.getPayload());
            transactionRepository.batchInsert(message.getPayload());
            System.out.println("Batch transaction inserted");
        };
    }

}
