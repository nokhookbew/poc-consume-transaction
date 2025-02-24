package com.example.pocconsumetransaction.service;

import com.example.pocconsumetransaction.model.Record;
import com.example.pocconsumetransaction.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Service
public class SubmitService {

    private static final int CHUNK_SIZE = 100;
    private static final int BATCH_SIZE = 500;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private StreamBridge streamBridge;
    public String submit(String transactionId) {
        List<Record> recordList;
        int offset = 0;
        do {
            recordList = recordRepository.findRecordsByTransactionId(transactionId, offset, CHUNK_SIZE);
            System.out.println("recordList: " + recordList.size() + " , offset: " + offset);
            sendMessage(recordList);
            offset += CHUNK_SIZE;
        } while (!recordList.isEmpty());

        System.out.println("recordList: " + recordList.size());
        System.out.println("All users sent to Kafka.");
        return "success";
    }

    private void sendMessage(List<Record> batch) {
        streamBridge.send("submitOutput", batch);
        System.out.println("Send record to kafka submitOutput : " + batch);
    }

    public String batchInsert(MultipartFile file) {
        String executeTime = "execution time : ";
        long startTime = System.currentTimeMillis();
        List<Record> batch = new ArrayList<>();
        try {
            // Optionally: Read the file stream to check line length and row count (streaming using BufferedReader)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                Integer rowCount = 0;
                while ((line = reader.readLine()) != null) {
                    rowCount++;
                    Record record = parshLineToRecord(line);
                    record.setNumberOfTransactions(rowCount);
                    batch.add(record);
                    if (batch.size() >= BATCH_SIZE) {
                        // insert to db
                        recordRepository.batchInsert(batch);
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    // send kafka
                    recordRepository.batchInsert(batch);
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println(executeTime + (endTime) + " ms");
            return executeTime + (endTime);
        } catch (Exception e) {
            System.out.println(e);
            return "Error";
        }
    }

    public String singleInsert(MultipartFile file) {
        String executeTime = "execution time : ";
        long startTime = System.currentTimeMillis();
        List<Record> batch = new ArrayList<>();
        try {
            // Optionally: Read the file stream to check line length and row count (streaming using BufferedReader)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                Integer rowCount = 0;
                while ((line = reader.readLine()) != null) {
                    rowCount++;
                    Record record = parshLineToRecord(line);
                    record.setNumberOfTransactions(rowCount);
                    batch.add(record);
                    if (batch.size() >= BATCH_SIZE) {
                        // insert to db
                        recordRepository.insertSingleRecordWithTransaction(batch);
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    // send kafka
                    recordRepository.insertSingleRecordWithTransaction(batch);
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println(executeTime + (endTime) + " ms");
            return executeTime + (endTime);
        } catch (Exception e) {
            System.out.println(e);
            return "Error";
        }
    }

    private Record parshLineToRecord(String line) {
        Record record = new Record();
        record.setTransactionId(line.substring(0,5));
        record.setTransactionType(line.substring(5,7));
        record.setTransactionAmount(line.substring(7, 40));
        record.setTransactionDate(line.substring(40, 41));
        return record;
    }
}
