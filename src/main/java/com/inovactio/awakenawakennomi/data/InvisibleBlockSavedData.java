package com.inovactio.awakenawakennomi.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;
import java.util.UUID;

public class InvisibleBlockSavedData extends WorldSavedData {

    private final Map<Long, UUID> map = new HashMap<>();
    public static final String NAME = "invisible_blocks";

    public InvisibleBlockSavedData() {
        super(NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        map.clear();
        if (nbt == null) return;
        ListNBT list = nbt.getList("entries", 8);
        for (int i = 0; i < list.size(); i++) {
            String s = list.getString(i);
            String[] parts = s.split(":");
            if (parts.length == 2) {
                try {
                    long posLong = Long.parseLong(parts[0]);
                    UUID uuid = UUID.fromString(parts[1]);
                    map.put(posLong, uuid);
                } catch (Exception ignored) { }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        for (Map.Entry<Long, UUID> e : map.entrySet()) {
            list.add(StringNBT.valueOf(e.getKey() + ":" + e.getValue().toString()));
        }
        nbt.put("entries", list);
        return nbt;
    }

    public Map<Long, UUID> getMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Définit l'état invisible d'une position pour un propriétaire donné.
     * Si invisible == true, ajoute ou met à jour l'entrée; sinon supprime l'entrée.
     * Appelle setDirty() si un changement a eu lieu afin que les données soient persistées.
     */
    public void setInvisible(BlockPos pos, boolean invisible, UUID owner) {
        if (pos == null) return;
        long key = pos.asLong();
        if (invisible) {
            UUID previous = map.put(key, owner);
            if (!Objects.equals(previous, owner)) this.setDirty();
        } else {
            if (map.remove(key) != null) this.setDirty();
        }
    }

    /**
     * Retourne true si la position est marquée invisible (quel que soit le propriétaire).
     */
    public boolean isInvisible(BlockPos pos) {
        if (pos == null) return false;
        return map.containsKey(pos.asLong());
    }

    /**
     * Optionnel : vérifie si la position est invisible pour un joueur spécifique.
     */
    public boolean isInvisibleForOwner(BlockPos pos, UUID owner) {
        if (pos == null || owner == null) return false;
        UUID u = map.get(pos.asLong());
        return owner.equals(u);
    }

    public List<Long> removeEntriesForPlayer(UUID owner) {
        List<Long> removed = new ArrayList<>();
        Iterator<Map.Entry<Long, UUID>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, UUID> e = it.next();
            if (owner.equals(e.getValue())) {
                removed.add(e.getKey());
                it.remove();
            }
        }
        if (!removed.isEmpty()) {
            this.setDirty();
        }
        return removed;
    }

    public void put(long posLong, UUID owner) {
        map.put(posLong, owner);
        this.setDirty();
    }

    public void remove(long posLong) {
        if (map.remove(posLong) != null) this.setDirty();
    }
}
