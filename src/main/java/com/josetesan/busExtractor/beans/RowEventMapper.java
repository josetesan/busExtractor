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
                .sec(resultSet.getLong("sec"))
                .eventDate(resultSet.getTimestamp("event_date"))
                .origen(resultSet.getString("origen"))
                .eventType(resultSet.getString("tipo_evento"))
                .campoModif(resultSet.getString("campo_modif"))
                .pks(resultSet.getString("pks"))
                .vaVn(resultSet.getString("va_vn"))
                .payload(resultSet.getString("payload"))
                .processed(resultSet.getInt("processed"))
                .build();
    }
}

