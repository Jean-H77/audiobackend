package org.csun.database;

import org.csun.dto.AudioDetailsDto;

import java.util.List;

public interface Database {

    AudioDetailsDto save(AudioDetailsDto audioDetailsDto);

    List<AudioDetailsDto> fetchAllAudioDetails();

    void createTables();
}
