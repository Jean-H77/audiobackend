package org.csun.database;

import org.csun.dto.AudioDetailsDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Database {

    CompletableFuture<AudioDetailsDto> save(AudioDetailsDto audioDetailsDto);

    CompletableFuture<List<AudioDetailsDto>> fetchAllAudioDetails();

    void createTables();
}
