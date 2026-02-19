package net.gabsdev.energymonitor.collector;

import net.gabsdev.energymonitor.dto.EnergyNetworkSnapshot;

import sonar.fluxnetworks.common.connection.FluxNetworkData;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.connection.ServerFluxNetwork;

import java.util.ArrayList;
import java.util.List;

public class FluxNetworkCollector {

    public static List<EnergyNetworkSnapshot> collect() {
        List<EnergyNetworkSnapshot> snapshots = new ArrayList<>();

        for (FluxNetwork network : FluxNetworkData.getAllNetworks()) {
            if (network instanceof ServerFluxNetwork serverNetwork) {
                NetworkStatistics stats = serverNetwork.getStatistics();
                if (stats != null) {
                    snapshots.add(EnergyNetworkSnapshot.from(serverNetwork, stats));
                }
            }
        }
        return snapshots;
    }
}