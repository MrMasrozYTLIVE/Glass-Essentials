package net.glasslauncher.glassbrigadier.api.argument.itemid;

import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public class ItemId {
    public final int numericId;
    public final Identifier id;

    ItemId(int numericId) {
        this.numericId = numericId;
        Item item = ItemRegistry.INSTANCE.get(numericId);
        this.id = ItemRegistry.INSTANCE.getId(item);
    }

    ItemId(String idString) {
        this.id = Identifier.of(idString);
        Item itemType = ItemRegistry.INSTANCE.get(id);
        if (itemType != null) {
            this.numericId = itemType.id;
            return;
        }
        try {
            this.numericId = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Item ID invalid!");
        }
    }
}
