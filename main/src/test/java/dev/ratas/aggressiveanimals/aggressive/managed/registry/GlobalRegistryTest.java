package dev.ratas.aggressiveanimals.aggressive.managed.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for SlimeDog/AggressiveAnimals#128: lookups and
 * unregistration must not create per-world entries that pin unloaded worlds.
 */
public class GlobalRegistryTest {
    private GlobalRegistry registry;

    @BeforeEach
    public void setup() {
        registry = new GlobalRegistry(null);
    }

    private static Object defaultObjectMethod(Object proxy, String name, Object[] args) {
        switch (name) {
            case "hashCode":
                return System.identityHashCode(proxy);
            case "equals":
                return proxy == args[0];
            case "toString":
                return "proxy@" + System.identityHashCode(proxy);
            default:
                throw new UnsupportedOperationException(name);
        }
    }

    private static World mockWorld(UUID uid) {
        return (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[] { World.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("getUID")) {
                        return uid;
                    }
                    if (method.getName().equals("getName")) {
                        return "world-" + uid;
                    }
                    return defaultObjectMethod(proxy, method.getName(), args);
                });
    }

    private static Mob mockMob(World world) {
        return (Mob) Proxy.newProxyInstance(Mob.class.getClassLoader(), new Class<?>[] { Mob.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("getWorld")) {
                        return world;
                    }
                    return defaultObjectMethod(proxy, method.getName(), args);
                });
    }

    private int worldEntries() throws ReflectiveOperationException {
        Field field = GlobalRegistry.class.getDeclaredField("perWorldManager");
        field.setAccessible(true);
        return ((Map<?, ?>) field.get(registry)).size();
    }

    @Test
    public void getTrackedMobDoesNotCreateWorldEntry() throws ReflectiveOperationException {
        Mob mob = mockMob(mockWorld(UUID.randomUUID()));
        Assertions.assertNull(registry.getTrackedMob(mob));
        Assertions.assertEquals(0, worldEntries());
    }

    @Test
    public void unregisterDoesNotCreateWorldEntry() throws ReflectiveOperationException {
        Mob mob = mockMob(mockWorld(UUID.randomUUID()));
        registry.unregister(mob, null);
        Assertions.assertEquals(0, worldEntries());
    }

    @Test
    public void getWorldManagerDoesNotCreateWorldEntry() throws ReflectiveOperationException {
        Assertions.assertNull(registry.getWorldManager(mockWorld(UUID.randomUUID())));
        Assertions.assertEquals(0, worldEntries());
    }

    @Test
    public void removeUnknownWorldIsEmpty() throws ReflectiveOperationException {
        Assertions.assertTrue(registry.removeWorld(mockWorld(UUID.randomUUID())).isEmpty());
        Assertions.assertEquals(0, worldEntries());
        Assertions.assertTrue(registry.getAllTrackedMobs().isEmpty());
    }

}
