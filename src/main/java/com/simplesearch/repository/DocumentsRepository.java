package com.simplesearch.repository;

import com.simplesearch.entity.document.Document;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Log4j2
@Repository
public class DocumentsRepository implements MetadataRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final String CREATE_NEW_ENTRY_QUERY = "INSERT INTO data(data, idx) VALUES(?, ?)";
    private static final String SELECT_METADATA_QUERY = "SELECT data from data where id = ?";


    @Autowired
    public DocumentsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer serialize(String idx, Document metadata) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_NEW_ENTRY_QUERY, Statement.RETURN_GENERATED_KEYS);
            try {
                write(idx, metadata, ps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Document deserialize(int id) {
        return jdbcTemplate.queryForObject(SELECT_METADATA_QUERY, (rs, rowNum) -> read(rs), id);
    }

    private void write(String idx, Document obj, PreparedStatement ps) throws SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        ps.setBytes(1, byteArrayOutputStream.toByteArray());
        ps.setString(2, idx);
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
