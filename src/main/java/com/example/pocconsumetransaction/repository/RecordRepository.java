package com.example.pocconsumetransaction.repository;

import com.example.pocconsumetransaction.model.Record;
import com.example.pocconsumetransaction.repository.mapper.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.UUID;

@Repository
public class RecordRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Record> records) {
        String sql = "INSERT INTO record (id, transactionId, transactionType, " +
                "transactionDate, transactionAmount, numberOfTransactions) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, records, 1000, (ps, record) -> {
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

    public List<Record> findRecordsByTransactionId(String transactionId, int offset, int limit) {
        String sql = "SELECT transactionid, transactiontype, transactiondate, transactionamount, numberoftransactions FROM record WHERE transactionid = ? ORDER BY id LIMIT ? OFFSET ?";
        System.out.println("SQL: " + sql);
        return jdbcTemplate.query(sql, recordRowMapper, "12345", limit, offset);
    }

    // RowMapper to map DB rows to Record objects
    private static final RowMapper<Record> recordRowMapper = (rs, rowNum) ->
            new Record(
                    rs.getString("transactionid"),
                    rs.getString("transactiontype"),
                    rs.getString("transactiondate"),
                    rs.getString("transactionamount"),
                    rs.getInt("numberoftransactions")
            );

    public void insertSingleRecordWithTransaction(List<Record> records) {
        jdbcTemplate.execute("BEGIN");

        String sql = "INSERT INTO record (id, transactionId, transactionType, transactionDate, transactionAmount, numberOfTransactions) VALUES (?, ?, ?, ?, ?, ?)";

        for (Record record : records) {
            UUID id = record.getId() != null ? record.getId() : UUID.randomUUID();
            jdbcTemplate.update(sql, id, record.getTransactionId(), record.getTransactionType(), record.getTransactionDate(), record.getTransactionAmount(), record.getNumberOfTransactions());
        }

        jdbcTemplate.execute("COMMIT");
    }

}
