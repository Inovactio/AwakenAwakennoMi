package com.inovactio.awakenawakennomi.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class InvisibleBlockSavedData extends WorldSavedData {
    private static final String NAME = "invisible_blocks";
    private final Set<Long> invisibleBlocks = new HashSet<>();

    public InvisibleBlockSavedData() {
        super(NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        long[] arr = nbt.getLongArray("Blocks");
        invisibleBlocks.clear();
        for (long l : arr) {
            invisibleBlocks.add(l);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        long[] arr = invisibleBlocks.stream().mapToLong(Long::longValue).toArray();
        nbt.putLongArray("Blocks", arr);
        return nbt;
    }

    public void setInvisible(BlockPos pos, boolean invisible) {
        long key = pos.asLong();
        if (invisible) {
            invisibleBlocks.add(key);
        } else {
            invisibleBlocks.remove(key);
        }
        this.setDirty(); // important pour que Forge sache qu’il faut sauvegarder
    }

    public boolean isInvisible(BlockPos pos) {
        return invisibleBlocks.contains(pos.asLong());
    }

    public Set<Long> getInvisibleBlocks() {
        return invisibleBlocks;
    }

}
