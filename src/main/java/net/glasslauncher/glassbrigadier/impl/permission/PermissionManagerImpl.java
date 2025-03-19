package net.glasslauncher.glassbrigadier.impl.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PermissionManagerImpl {
    @Getter
    private static final Map<String, Set<PermissionNode>> permissionsMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();
    private static final File permissionsFile = GlassBrigadier.getConfigFile("permissions.json");
    private static final TypeToken<Map<String, Set<String>>> string2StringSetType = new TypeToken<>() {};

    // This should never throw a FileNotFound exception.
    @SneakyThrows
    public static void setupPermissionManager() {
        if (!permissionsFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            permissionsFile.getParentFile().mkdirs();
        }

        if (permissionsFile.exists()) {
            loadPermissions();
            return;
        }

        try {
            final FileWriter writer = new FileWriter(permissionsFile);
            writer.write("{}");
            writer.close();
            GlassBrigadier.LOGGER.info("Created perms file.");
        } catch (IOException ex) {
            GlassBrigadier.LOGGER.error("Couldn't create perms file!", ex);
        }
    }

    public static boolean tryUpdatePermissionsFile() {
        if (!permissionsFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            permissionsFile.getParentFile().mkdirs();
        }

        try {
            savePermissions();
            return true;
        } catch (IOException e) {
            GlassBrigadier.LOGGER.error("Couldn't save perms file!", e);
            return false;
        }
    }

    private static void loadPermissions() throws FileNotFoundException {
        final Map<String, Set<String>> stringMap = GSON.fromJson(new FileReader(permissionsFile), string2StringSetType.getType());
        permissionsMap.clear();
        stringMap.forEach((names, perms) ->
                permissionsMap.put(names, perms.stream().map(PermissionNode::of).collect(Collectors.toSet()))
        );
    }

    private static void savePermissions() throws IOException {
        final Map<String, Set<String>> stringMap = new HashMap<>();
        permissionsMap.forEach((name, perms) ->
                stringMap.put(name, perms.stream().map(PermissionNode::toString).collect(Collectors.toSet()))
        );
        final FileWriter writer = new FileWriter(permissionsFile);
        writer.write(GSON.toJson(stringMap, string2StringSetType.getType()));
        writer.close();
    }
}
