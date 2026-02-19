package net.gabsdev.energymonitor.scheduler;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.gabsdev.energymonitor.collector.FluxNetworkCollector;
import net.gabsdev.energymonitor.writer.JsonLogWriter;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class EnergyLogScheduler {

    private static final int INTERVAL_TICKS = 20 * 300; // 5min
    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side.isClient()) return;


        counter++;
        if (counter < INTERVAL_TICKS) return;
        counter = 0;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (ServerLevel level : server.getAllLevels()) {
            var snapshots = FluxNetworkCollector.collect();
            Path logFile = level.getServer().getServerDirectory()
                    .toPath()
                    .resolve("energy_logs.jsonl");

            JsonLogWriter.write(logFile, snapshots);
        }
    }
}
