package dev.ratas.aggressiveanimals.aggressive.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;

public enum MobType {
    axolotl(EntityType.AXOLOTL),
    // bat(EntityType.BAT),
    bee(EntityType.BEE),
    // blaze(EntityType.BLAZE),
    cat(EntityType.CAT),
    cave_spider(EntityType.CAVE_SPIDER),
    chicken(EntityType.CHICKEN),
    cod(EntityType.COD),
    cow(EntityType.COW),
    // creeper(EntityType.CREEPER),
    dolphin(EntityType.DOLPHIN),
    donkey(EntityType.DONKEY),
    // drowned(EntityType.DROWNED),
    // elder_guardian(EntityType.ELDER_GUARDIAN),
    // ender_dragon(EntityType.ENDER_DRAGON),
    enderman(EntityType.ENDERMAN),
    // endermite(EntityType.ENDERMITE),
    // evoker(EntityType.EVOKER),
    fox(EntityType.FOX),
    // ghast(EntityType.GHAST),
    // giant(EntityType.GIANT),
    goat(EntityType.GOAT),
    // guardian(EntityType.GUARDIAN),
    glow_squid(EntityType.GLOW_SQUID),
    // hogline(EntityType.HOGLIN),
    horse(EntityType.HORSE),
    // husk(EntityType.HUSK),
    // illusioner(EntityType.ILLUSIONER),
    iron_golem(EntityType.IRON_GOLEM),
    llama(EntityType.LLAMA),
    // magma_cube(EntityType.MAGMA_CUBE),
    mule(EntityType.MULE),
    mooshroom(EntityType.MUSHROOM_COW),
    ocelot(EntityType.OCELOT),
    panda(EntityType.PANDA),
    parrot(EntityType.PARROT),
    // phantom(EntityType.PHANTOM),
    pig(EntityType.PIG),
    piglin(EntityType.PIGLIN),
    // piglin_brute(EntityType.PIGLIN_BRUTE),
    // pillager(EntityType.PILLAGER),
    polar_bear(EntityType.POLAR_BEAR),
    pufferfish(EntityType.PUFFERFISH),
    rabbit(EntityType.RABBIT),
    // ravager(EntityType.RAVAGER),
    salmon(EntityType.SALMON),
    sheep(EntityType.SHEEP),
    // shulker(EntityType.SHULKER),
    // silverfish(EntityType.SILVERFISH),
    // skeleton(EntityType.SKELETON),
    skeleton_horse(EntityType.SKELETON_HORSE),
    // slime(EntityType.SLIME),
    snow_golem(EntityType.SNOWMAN),
    spider(EntityType.SPIDER),
    squid(EntityType.SQUID),
    // stray(EntityType.STRAY),
    strider(EntityType.STRIDER),
    trader_llama(EntityType.TRADER_LLAMA),
    tropical_fish(EntityType.TROPICAL_FISH),
    turtle(EntityType.TURTLE),
    // vex(EntityType.VEX),
    villager(EntityType.VILLAGER),
    // vindicator(EntityType.VINDICATOR),
    wandering_trader(EntityType.WANDERING_TRADER),
    // witch(EntityType.WITCH),
    // wither(EntityType.WITHER),
    // wither_skeleton(EntityType.WITHER_SKELETON),
    wolf(EntityType.WOLF),
    // zoglin(EntityType.ZOGLIN),
    // zombie(EntityType.ZOMBIE),
    zombie_horse(EntityType.ZOMBIE_HORSE),
    // zombie_villager(EntityType.ZOMBIE_VILLAGER),
    zombified_piglin(EntityType.ZOMBIFIED_PIGLIN),
    // New in next version(s):
    frog("FROG"),
    tadpole("TADPOLE"),
    __INVALID("_INVALD");

    private static final Map<EntityType, MobType> REVERSE_MAP = new EnumMap<>(EntityType.class);
    private static final Map<String, MobType> NAME_MAP = new HashMap<>();
    private static final List<String> NAMES = new ArrayList<>();
    static {
        MobType.fillMaps();
    }
    private final EntityType delegate;
    private final Set<String> alternateNames;

    MobType(String enumName, String... alternates) {
        EntityType delegate;
        try {
            delegate = EntityType.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            delegate = null;
        }
        this.delegate = delegate;
        this.alternateNames = new HashSet<>(Arrays.asList(alternates));
    }

    MobType(EntityType delegate, String... alternates) {
        this.delegate = delegate;
        this.alternateNames = new HashSet<>(Arrays.asList(alternates));
    }

    /**
     * Checks whether or not the mob type is tameable. Foxes are not considered
     * tameable and will thus need to be addressed separately.
     * 
     * @return whether or not the mob represented by this type is tameable
     */
    public boolean isTameable() {
        if (this == __INVALID) {
            return false;
        }
        return Tameable.class.isAssignableFrom(delegate.getEntityClass()) || this == fox || this == ocelot;
    }

    public EntityType getBukkitType() {
        return delegate;
    }

    public static MobType from(String name) throws IllegalArgumentException {
        MobType type;
        try {
            type = Enum.valueOf(MobType.class, name);
        } catch (IllegalArgumentException e) {
            // try again with upper case
            try {
                type = Enum.valueOf(MobType.class, name.toUpperCase());
            } catch (IllegalArgumentException e2) {
                throw e; // original
            }
        }
        if (type == __INVALID) {
            // exception for the __INVALID type which does not have a bukkit type
            // (obviously)
            return type;
        } else if (type.getBukkitType() == null) {
            throw new IllegalArgumentException("Disabled/unavailable mob type: " + name);
        }
        return type;
    }

    public static MobType matchType(String name) {
        try {
            return MobType.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return NAME_MAP.get(name.toLowerCase());
        }
    }

    public static MobType fromBukkit(EntityType type) {
        return REVERSE_MAP.get(type);
    }

    private static void fillMaps() {
        for (MobType type : values()) {
            if (type.getBukkitType() == null) {
                continue; // ignore
            }
            if (type == __INVALID) {
                continue; // ingored
            }
            NAME_MAP.put(type.name(), type);
            NAMES.add(type.name());
            for (String name : type.alternateNames) {
                NAME_MAP.put(name, type);
            }
            REVERSE_MAP.put(type.getBukkitType(), type);
        }
    }

    public static List<String> names() {
        return Collections.unmodifiableList(NAMES);
    }

}
