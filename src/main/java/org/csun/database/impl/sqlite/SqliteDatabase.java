package org.csun.database.impl.sqlite;

import org.csun.database.Database;
import org.csun.dto.AudioDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteDatabase implements Database {

    private static final Logger logger = LoggerFactory.getLogger(SqliteDatabase.class);
    private static final SQLiteDataSource dataSource = new SQLiteDataSource();

    static {
        dataSource.setUrl("jdbc:sqlite:C://sqlite/db/audio.db");
        File directory = new File("C:/sqlite/db/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public AudioDetailsDto save(AudioDetailsDto audioDetailsDto) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO audio_metadata (filename, overall_tempo, peak_1, peak_2) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, audioDetailsDto.fileName());
                statement.setDouble(2, audioDetailsDto.overallTempo());
                statement.setDouble(3, audioDetailsDto.peakOne());
                statement.setDouble(4, audioDetailsDto.peakTwo());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("Unable to insert into table", e);
        }
        return audioDetailsDto;
    }

    @Override
    public List<AudioDetailsDto> fetchAllAudioDetails() {
        List<AudioDetailsDto> audioList = new ArrayList<>();
        String sql = "SELECT * FROM audio_metadata";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                AudioDetailsDto audioDetailsDto = new AudioDetailsDto(
                        resultSet.getString("filename"),
                        resultSet.getDouble("overall_tempo"),
                        resultSet.getDouble("peak_1"),
                        resultSet.getDouble("peak_2")
                );
                audioList.add(audioDetailsDto);
            }
        } catch (Exception e) {
            logger.error("Unable to fetch audio details", e);
        }
        return audioList;
    }

    @Override
    public void createTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS audio_metadata ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "filename TEXT NOT NULL,"
                + "overall_tempo REAL NOT NULL,"
                + "peak_1 REAL NOT NULL,"
                + "peak_2 REAL NOT NULL);";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            logger.error("Unable to create database table", e);
        }
    }
}
