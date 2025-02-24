package com.example.pocconsumetransaction.repository;

import com.example.pocconsumetransaction.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Record> records) {
        String sql = "INSERT INTO transaction (id, transactionId, transactionType, transactionDate, transactionAmount, numberOfTransactions) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, records, 100, (ps, record) -> {
            UUID id = record.getId() != null ? record.getId() : UUID.randomUUID();
            ps.setObject(1, id);
            ps.setString(2, record.getTransactionId());
            ps.setString(3, record.getTransactionType());
            ps.setString(4, record.getTransactionDate());
            ps.setString(5, record.getTransactionAmount());
            ps.setInt(6, record.getNumberOfTransactions());
        });

        System.out.println("Batch insert completed: " + records.size() + " records");
    }
}
