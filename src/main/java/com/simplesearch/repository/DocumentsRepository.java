package com.simplesearch.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.simplesearch.model.internal.document.Document;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DocumentsRepository implements MetadataRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final String CREATE_NEW_ENTRY_QUERY = "INSERT IGNORE INTO data(id, data, idx) VALUES(?, ?, ?) ";
    private static final String SELECT_METADATA_QUERY = "SELECT data from data where id = ?";

    public Integer serialize(String idx, Document metadata) {
        int hash = metadata.hashCode();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_NEW_ENTRY_QUERY);
            try {
                write(hash, idx, metadata, ps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ps;
        });
        return hash;
    }

    public Document deserialize(int id) {
        return jdbcTemplate.queryForObject(SELECT_METADATA_QUERY, (rs, rowNum) -> read(rs), id);
    }

    private void write(int id, String idx, Document obj, PreparedStatement ps) throws SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        ps.setLong(1, id);
        ps.setBytes(2, byteArrayOutputStream.toByteArray());
        ps.setString(3, idx);
    }

    private Document read(ResultSet rs) {
        try {
            byte[] buf = rs.getBytes("data");
            if (buf != null) {
                ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                return (Document) objectIn.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
        }
        return null;
    }

}
