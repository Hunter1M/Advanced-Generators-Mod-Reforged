package net.bdew.generators;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * Java entry point for the mod.
 *
 * <p>The mod itself is written in Scala and its main class is a Scala object
 * ({@link Generators}). Forge's Scala loader used to be able to instantiate Scala
 * objects directly, but that loader does not exist for NeoForge. Using a small
 * Java class avoids any reflection/module weirdness with Scala's {@code MODULE$}
 * singleton.</p>
 */
@Mod("advgenerators")
public class GeneratorsMod {
    public GeneratorsMod(IEventBus modBus, ModContainer container) {
        Generators.init(modBus, container);
    }
}
