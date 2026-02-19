# ⚡ EnergyMonitorMod (Minecraft 1.12.2)

EnergyMonitorMod is a Minecraft Forge mod for version 1.12.2 that reads Flux Networks data from a world or server and logs structured energy metrics into a JSONL file.

The mod was created as a sandbox environment to simulate real-world energy generation data for experimental Blockchain applications using the Polkadot SDK (Solochain).

---

## 📌 Purpose

This project bridges a simulated energy infrastructure (Minecraft modded environment) with experimental Blockchain systems.

It captures energy network metrics and exports them as structured data for use in:

- Blockchain validation experiments
- Tokenized energy models
- Decentralized infrastructure simulations
- Solochain development with Polkadot SDK

---

## 🔍 What It Does

The mod reads active Flux Networks in the current world/server and writes logs to:

- Network ID
- Internal server timestamp
- Network energy variation rate
- Total energy input
- Total energy output

Example structure:

```json
{
  "networkId": 1,
  "owner": "9669710f-6899-3a53-84c4-cf4ce9611817",
  "energy": [
    106,
    106,
    106,
    106,
    106,
    106
  ],
  "input": 106,
  "output": 106,
  "timestamp": 1768263839
}

