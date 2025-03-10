package net.glasslauncher.glassbrigadier;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class GlassBrigadierConfig {
    @ConfigEntry(
            name = "Old Help Display",
            description = "This is server-sided, specify the command for extended help text.",
            nameKey = "config.glassbrigadier.oldDisplay.name",
            descriptionKey= "config.glassbrigadier.oldDisplay.desc",
            multiplayerSynced = true
    )
    public Boolean oldDisplay = true;
}
