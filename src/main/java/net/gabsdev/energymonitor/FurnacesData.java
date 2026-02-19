package net.gabsdev.energymonitor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

public class FurnacesData extends SavedData {

    private final Map<BlockPos, Integer> trackedOutputCount = new HashMap<>();

    public FurnacesData() {}

    public FurnacesData(CompoundTag nbt) {
        // Ler do NBT
        ListTag list = nbt.getList("Furnaces", Tag.TAG_COMPOUND);
        for (Tag tag : list) {
            CompoundTag entry = (CompoundTag) tag;
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("Pos"));
            int count = entry.getInt("Count");
            trackedOutputCount.put(pos, count);
        }
    }

    @SuppressWarnings("null")
    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, Integer> entry : trackedOutputCount.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("Pos", NbtUtils.writeBlockPos(entry.getKey()));
            tag.putInt("Count", entry.getValue());
            list.add(tag);
        }
        nbt.put("Furnaces", list);
        return nbt;
    }

    public void setCount(BlockPos pos, int count) {
        trackedOutputCount.put(pos, count);
        setDirty(); // Marca para salvar
    }

    public int getCount(BlockPos pos) {
        return trackedOutputCount.getOrDefault(pos, 0);
    }

    public Map<BlockPos, Integer> getTracked() {
        return trackedOutputCount;
    }
}