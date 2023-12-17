package org.csun;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.csun.database.Database;
import org.csun.database.impl.sqlite.SqliteDatabase;
import org.csun.dto.AudioDetailsDto;

import java.util.Objects;
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
            String fileName = ctx.formParam("file_name");
            double overallTempo = Double.parseDouble(Objects.requireNonNull(ctx.formParam("overall_tempo")));
            double peakOne = Double.parseDouble(Objects.requireNonNull(ctx.formParam("peak_1")));
            double peakTwo = Double.parseDouble(Objects.requireNonNull(ctx.formParam("peak_2")));

            database.save(new AudioDetailsDto(fileName, overallTempo, peakOne, peakTwo))
                    .thenApply(dto -> ctx.result("Received: " + dto));
        });

        app.get("api/get-audio-list", ctx -> database.fetchAllAudioDetails()
                .thenApply(ctx::json));

        return this;
    }

    private void start() {
        CompletableFuture.runAsync(database::createTables)
                .thenRun(() -> app.start(PORT));
    }
}