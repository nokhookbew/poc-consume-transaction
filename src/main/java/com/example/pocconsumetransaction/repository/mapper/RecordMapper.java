package com.example.pocconsumetransaction.repository.mapper;


import com.example.pocconsumetransaction.model.Record;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RecordMapper implements RowMapper<Record> {
    @Override
    public Record mapRow(ResultSet resultSet, int i) throws SQLException {
        Record  record = new Record();
        record.setId((UUID) resultSet.getObject("id"));
        record.setTransactionId(resultSet.getString("transaction_id"));
        record.setTransactionType(resultSet.getString("transaction_type"));
        record.setTransactionDate(resultSet.getString("transaction_date"));
        record.setTransactionAmount(resultSet.getString("transaction_amount"));
        record.setNumberOfTransactions(resultSet.getInt("number_of_transactions"));
        return record;
    }
}
