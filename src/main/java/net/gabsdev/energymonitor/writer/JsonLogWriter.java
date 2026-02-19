package net.gabsdev.energymonitor.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.gabsdev.energymonitor.dto.EnergyNetworkSnapshot;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class JsonLogWriter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void write(Path file, List<EnergyNetworkSnapshot> snapshots) {
        try (FileWriter writer = new FileWriter(file.toFile(), true)) {
            for (EnergyNetworkSnapshot snapshot : snapshots) {
                writer.write(GSON.toJson(snapshot));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
