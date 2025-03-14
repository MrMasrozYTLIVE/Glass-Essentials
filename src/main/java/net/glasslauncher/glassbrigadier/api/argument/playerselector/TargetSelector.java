package net.glasslauncher.glassbrigadier.api.argument.playerselector;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.argument.entityid.EntityType;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.utils.EntityUtils;
import net.glasslauncher.glassbrigadier.impl.argument.SelfSelector;
import net.glasslauncher.glassbrigadier.impl.utils.StringReaderUtils;
import net.glasslauncher.glassbrigadier.impl.utils.UncheckedCaster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TargetSelector<E extends Entity> implements Predicate<Entity> {
    private static final Random RANDOM = new Random();
    private static final SimpleCommandExceptionType INVALID_TARGET_SELECTOR = new SimpleCommandExceptionType(new LiteralMessage("Invalid Target Selector"));
    private static final SimpleCommandExceptionType INVALID_ENTITY_TYPE = new SimpleCommandExceptionType(new LiteralMessage("Invalid Entity Type"));
    private final Class<E> clazz;
    @Getter
    private final String name;
    private final int limit;
    private final SortingMethod sortingMethod;

    protected TargetSelector(Class<E> clazz, String name, int limit, SortingMethod sortingMethod) {
        this.clazz = clazz;
        this.name = name;
        this.limit = limit;
        this.sortingMethod = sortingMethod;
    }

    public static TargetSelector<?> literal(String name) throws CommandSyntaxException {
        return new TargetSelector<>(Entity.class, name, 1, SortingMethod.RANDOM);
    }

    public static TargetSelector<?> player(String name) throws CommandSyntaxException {
        return new TargetSelector<>(PlayerEntity.class, name, 1, SortingMethod.RANDOM);
    }

    public static TargetSelector<?> create(char selectorType, String optionsString) throws CommandSyntaxException {
        final Options options = new Options(StringReaderUtils.readTargetSelectorOptions(new StringReader(optionsString)));
        return switch (selectorType) {
            case 'a' -> new TargetSelector<>(PlayerEntity.class, options.name(), options.limit(), options.sort());
            case 'p' -> new TargetSelector<>(PlayerEntity.class, options.name(), 1, SortingMethod.NEAREST);
            case 'r' -> new TargetSelector<>(PlayerEntity.class, options.name(), 1, SortingMethod.RANDOM);
            case 'e' -> new TargetSelector<>(options.clazz(), options.name(), options.limit(), options.sort());
            case 's' -> new SelfSelector();
            default -> throw INVALID_TARGET_SELECTOR.create();
        };
    }

    public boolean isPlayerOnly() {
        return PlayerEntity.class.isAssignableFrom(this.clazz);
    }

    public boolean isSingleOnly() {
        return this.limit == 1;
    }

    @Override
    public boolean test(Entity entity) {
        return this.clazz.isAssignableFrom(entity.getClass()) && (this.name == null || this.name.equals(EntityUtils.getName(entity)));
    }

    public List<E> getEntities(GlassCommandSource sender) {
        return this.getMatchingEntities(sender);
    }

    public List<String> getNames(GlassCommandSource sender) {
        return this.getEntities(sender).stream().map(EntityUtils::getName).collect(Collectors.toList());
    }

    protected List<E> getMatchingEntities(GlassCommandSource sender) {
        return getAllEntities(sender).stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(this)
                .sorted(sortingMethod.getComparator(sender))
                .limit(limit)
                .collect(Collectors.toList());
    }

    protected static List<Entity> getAllEntities(GlassCommandSource sender) {
        World level = sender.getWorld();
        if (level != null) {
            return UncheckedCaster.list(level.entities);
        } else {
            //noinspection deprecation
            return UncheckedCaster.list(Arrays.stream(((MinecraftServer) FabricLoader.getInstance().getGameInstance()).worlds).flatMap(l -> UncheckedCaster.list(l.entities).stream()).collect(Collectors.toList()));
        }
    }

    public enum SortingMethod {
        NEAREST(sender ->
            Comparator.comparingDouble(e -> EntityUtils.distanceBetween(e, sender.getPosition()))
        ),
        FURTHEST(sender -> NEAREST.getComparator(sender).reversed()),
        RANDOM(sender -> {
            int randomValue = TargetSelector.RANDOM.nextInt();
            return Comparator.<Entity>comparingInt(e -> e.hashCode()^randomValue)
                    .thenComparing(NEAREST.getComparator(sender)); // if they have the same hash code, just resort to nearest
        });

        private final Function<GlassCommandSource, Comparator<Entity>> implementation;

        SortingMethod(Function<GlassCommandSource, Comparator<Entity>> implementation) {
            this.implementation = implementation;
        }

        public Comparator<Entity> getComparator(GlassCommandSource sender) {
            return implementation.apply(sender);
        }
    }

    private record Options(Map<String, String> optionStrings) {

        private String name() {
                return this.getString("name", null);
            }

        private int limit() {
            return this.getInt("limit", Integer.MAX_VALUE);
        }

        private SortingMethod sort() {
            return this.getEnum("sort", SortingMethod.class, SortingMethod.RANDOM);
        }

        private Class<? extends Entity> clazz() throws CommandSyntaxException {
            final String entityTypeName = this.getString("type", null);
            if (entityTypeName == null) {
                return Entity.class;
            } else {
                //noinspection unchecked
                final EntityType entityType = EntityType.of(entityTypeName, (Class<? extends Entity>) EntityRegistry.idToClass.get(entityTypeName));
                if (entityType == null) {
                    throw INVALID_ENTITY_TYPE.create();
                }

                return entityType.entity();
            }
        }

        private int getInt(final String key, final int defaultValue) {
            final String value = this.optionStrings.get(key);
            if (value != null) {
                return Integer.parseInt(value);
            } else {
                return defaultValue;
            }
        }

        private double getDouble(final String key, final double defaultValue) {
            final String value = this.optionStrings.get(key);
            if (value != null) {
                return Double.parseDouble(value);
            } else {
                return defaultValue;
            }
        }

        private <E extends Enum<E>> E getEnum(final String key, final Class<E> clazz, final E defaultValue) {
            final String value = this.optionStrings.get(key);
            final E[] enumValues = clazz.getEnumConstants();

            if (value != null) {
                for (E element : enumValues) {
                    if (element.name().equalsIgnoreCase(value)) {
                        return element;
                    }
                }
            }

            return defaultValue;
        }

        private String getString(final String key, final String defaultValue) {
            return this.optionStrings.getOrDefault(key, defaultValue);
        }
    }
}