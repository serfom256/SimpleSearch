package com.opensearch.repository;

import com.opensearch.entity.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Profile("!test")
@Repository
public class ObjectMetadataRepository implements MetadataRepository{

    private final JdbcTemplate jdbcTemplate;
    private static final String CREATE_NEW_ENTRY_QUERY = "INSERT INTO serialized(data) VALUES(?)";
    private static final String SELECT_METADATA_QUERY = "SELECT data from serialized where id = ?";

    @Autowired
    public ObjectMetadataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer serialize(Document metadata) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_NEW_ENTRY_QUERY, Statement.RETURN_GENERATED_KEYS);
            try {
                write(metadata, ps);
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

    private void write(Document obj, PreparedStatement ps) throws SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        ps.setBytes(1, byteArrayOutputStream.toByteArray());
    }

    private static Document read(ResultSet rs) {
        try {
            byte[] buf = rs.getBytes("data");
            if (buf != null) {
                ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                return (Document) objectIn.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
