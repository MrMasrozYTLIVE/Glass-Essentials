package net.glasslauncher.glassbrigadier.impl;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class GlassBrigadierConfig {
    @ConfigEntry(
            name = "Display help the old way",
            description = "This is server-sided",
            nameKey = "config.glassbrigadier.oldDisplay.name",
            descriptionKey= "config.glassbrigadier.oldDisplay.desc",
            multiplayerSynced = true
    )
    public Boolean oldDisplay = true;
}
