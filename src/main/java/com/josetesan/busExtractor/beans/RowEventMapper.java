package com.josetesan.busExtractor.beans;

import io.micrometer.core.annotation.Timed;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowEventMapper implements RowMapper<RowEvent> {

    @Override
    @Timed
    public RowEvent mapRow(ResultSet resultSet, int i) throws SQLException {
        return RowEvent.builder()
                .id(resultSet.getLong("id"))
                .create_date(resultSet.getTimestamp("create_date"))
                .payload(resultSet.getString("payload"))
                .build();
    }
}
