package org.csun;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.csun.database.Database;
import org.csun.database.impl.sqlite.SqliteDatabase;
import org.csun.dto.AudioDetailsDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Server {

    private static final int PORT = 7000;

    private final Javalin app;

    private final Database database;

    public Server(Javalin app, Database database) {
        this.app = app;
        this.database = database;
    }

    public static void main(String[] args) {
        var app = Javalin.create(config -> config.plugins.enableCors(corsContainer -> corsContainer.add(CorsPluginConfig::anyHost)));

        new Server(app, new SqliteDatabase())
                .addRoutes()
                .start();
    }

    private Server addRoutes() {
        app.post("api/insert", ctx -> {
            AudioDetailsDto saved = database.save(ctx.bodyAsClass(AudioDetailsDto.class));
            ctx.json(saved);
        });

        app.get("api/get-audio-list", ctx -> {
            List<AudioDetailsDto> fetchedList = database.fetchAllAudioDetails();
            ctx.json(fetchedList);
        });

        return this;
    }

    private void start() {
        CompletableFuture.runAsync(database::createTables)
                .thenRun(() -> app.start(PORT));
    }
}