package net.glasslauncher.glassbrigadier.api.argument.tileid;

import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public class TileId {
    public final int numericId;
    public final Identifier id;

    TileId(int numericId) {
        this.numericId = numericId;
        Block block = BlockRegistry.INSTANCE.get(numericId);
        this.id = BlockRegistry.INSTANCE.getId(block);
    }

    TileId(String idString) {
        this.id = Identifier.of(idString);
        Block block = BlockRegistry.INSTANCE.get(id);
        if (block != null) {
            this.numericId = block.id;
            return;
        }
        try {
            this.numericId = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Tile ID invalid!");
        }
    }
}
