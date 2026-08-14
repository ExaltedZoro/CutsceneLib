package net.exaltedzoro.cutscenelib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneData;
import net.exaltedzoro.cutscenelib.registry.ModRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RunCutsceneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        /*
        Run Cutscene command

        **Command**
        run_cutscene

        **Arguments**
        cutscene_id (Mandatory) - The key for the registered cutscene to be played
        origin (Optional) - Coordinates that the cutscene should be played at. Defaults to the player position
        rotation (Optional) - How much the cutscene should be rotated by. Defaults to 0.
         */
        dispatcher.register(
                Commands.literal("run_cutscene")
                        .then(Commands.argument("cutscene_id", ResourceKeyArgument.key(ModRegistries.CUTSCENE_REGISTRY_KEY))
                                .executes(RunCutsceneCommand::execute)
                                .then(Commands.argument("origin", Vec3Argument.vec3()).executes(RunCutsceneCommand::execute)
                                .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute)))
                                .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute))
                        ));
    }

    public static int execute(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {

            CutsceneData data = null;
            Vec3 origin = null;
            Float rotation = null;

            try {
                ResourceLocation dataArgument = command.getArgument("cutscene_id", ResourceKey.class).location();
                Registry<CutsceneData> registry = command.getSource().registryAccess().registryOrThrow(ModRegistries.CUTSCENE_REGISTRY_KEY);
                data = registry.get(dataArgument);
            } catch (IllegalArgumentException exception) {
                player.sendSystemMessage(Component.literal("Cutscene argument is invalid"));
            }

            try {
                origin = Vec3Argument.getVec3(command, "origin");
            } catch (IllegalArgumentException ignored) {}

            try {
                rotation =  command.getArgument("rotation", Float.class);
            } catch (IllegalArgumentException ignored) {}

            String constructedMessage = "Playing ";
            if (data != null) {
                constructedMessage += data.getDisplayName();
            } else {
                constructedMessage += "null";
            }

            if (origin != null) {
                constructedMessage += " at " + origin + " ";
            } else {
                constructedMessage += " at player location ";
            }

            if (rotation != null) {
                constructedMessage += "with " + rotation + " degrees of rotation";
            } else {
                constructedMessage += "without rotation";
            }

            player.sendSystemMessage(Component.literal(constructedMessage));
        }
        return Command.SINGLE_SUCCESS;
    }
}
