package net.exaltedzoro.cutscenelib.command;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RunCutsceneCommand.register(event.getDispatcher());
    }
}
