package net.gabsdev.energymonitor.dto;

import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.NetworkStatistics;

import java.time.Instant;
import java.util.UUID;

import it.unimi.dsi.fastutil.longs.LongList;

public class EnergyNetworkSnapshot {

    public final int networkId;
    public final UUID owner;
    public final LongList energy;
    public final long input;
    public final long output;
    public final long timestamp;

    private EnergyNetworkSnapshot(
            int networkId,
            UUID owner,
            LongList energyChange,
            long input,
            long output
    ) {
        this.networkId = networkId;
        this.owner = owner;
        this.energy = energyChange;
        this.input = input;
        this.output = output;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public static EnergyNetworkSnapshot from(
            FluxNetwork network,
            NetworkStatistics stats
    ) {
        return new EnergyNetworkSnapshot(
                network.getNetworkID(),
                network.getOwnerUUID(),
                stats.energyChange,
                stats.energyInput,
                stats.energyOutput
        );
    }
}
