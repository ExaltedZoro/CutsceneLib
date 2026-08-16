package net.exaltedzoro.cutscenelib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneData;
import net.exaltedzoro.cutscenelib.networking.CutsceneTriggerPayload;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;

public class RunCutsceneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        /*
        Run Cutscene command

        **Command**
        run_cutscene

        **Arguments**
        cutscene_id (Mandatory) - The key for the registered cutscene to be played
        targets (Optional) - What players this cutscene should play for. Defaults to the source player if there is one
        origin (Optional) - Coordinates that the cutscene should be played at. Defaults to the player position
        rotation (Optional) - How much the cutscene should be rotated by. Defaults to 0.
         */
        dispatcher.register(
                Commands.literal("run_cutscene")
                        .then(Commands.argument("cutscene_id", ResourceKeyArgument.key(ModRegistries.CUTSCENE_REGISTRY_KEY)).executes(RunCutsceneCommand::execute)
                                .then(Commands.literal("targets")
                                        .then(Commands.argument("targets", EntityArgument.players()).executes(RunCutsceneCommand::execute)
                                                .then(Commands.literal("origin")
                                                        .then(Commands.argument("origin", Vec3Argument.vec3()).executes(RunCutsceneCommand::execute)
                                                                .then(Commands.literal("rotation")
                                                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute)))
                                                                .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute))))
                                                .then(Commands.literal("rotation")
                                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute)))))
                                .then(Commands.literal("origin")
                                        .then(Commands.argument("origin", Vec3Argument.vec3()).executes(RunCutsceneCommand::execute)
                                                .then(Commands.literal("rotation")
                                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute)))))
                                .then(Commands.literal("rotation")
                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg()).executes(RunCutsceneCommand::execute)))
                        ));
    }

    public static int execute(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {

            CutsceneData data = null;
            List<ServerPlayer> targets = null;
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
                targets = (List<ServerPlayer>) EntityArgument.getPlayers(command, "targets");
            } catch (IllegalArgumentException | CommandSyntaxException ignored) {}

            try {
                origin = Vec3Argument.getVec3(command, "origin");
            } catch (IllegalArgumentException ignored) {}

            try {
                rotation =  command.getArgument("rotation", Float.class);
            } catch (IllegalArgumentException ignored) {}

            if (data == null) {
                CutsceneLib.LOGGER.error("Could not start cutscene due to invalid cutscene ID given in command.");
            } else {
                ResourceLocation cutsceneID = command.getSource().registryAccess().registryOrThrow(ModRegistries.CUTSCENE_REGISTRY_KEY).getKey(data);

                long startTime = command.getSource().getLevel().getGameTime() + CutsceneLib.CUTSCENE_START_DELAY;

                if (rotation == null) {
                    rotation = 0f;
                }

                if (targets != null) {
                    for (ServerPlayer target : targets) {
                        PacketDistributor.sendToPlayer(target, new CutsceneTriggerPayload(cutsceneID, Objects.requireNonNullElseGet(origin, () -> target.blockPosition().getCenter()), rotation, startTime));
                    }
                } else {
                    CutsceneLib.LOGGER.info("No valid targets given in run_cutscene command. Attempting command source fallback.");
                    ServerPlayer targetFallback = command.getSource().getPlayer();
                    if (targetFallback == null) {
                        CutsceneLib.LOGGER.error("Command source fallback failed. Could not start cutscene.");
                        CutsceneLib.LOGGER.info("If command is being run from a Command Block or similar, a target parameter must be given.");
                    } else {
                        CutsceneLib.LOGGER.info("Command source fallback successful, sending packet.");
                        if (origin == null) {
                            PacketDistributor.sendToPlayer(targetFallback, new CutsceneTriggerPayload(cutsceneID, targetFallback.blockPosition().getCenter(), rotation, startTime));
                        } else {
                            PacketDistributor.sendToPlayer(targetFallback, new CutsceneTriggerPayload(cutsceneID, origin, rotation, startTime));
                        }
                    }
                }

            }

            /*StringBuilder constructedMessage = new StringBuilder("Playing ");
            if (data != null) {
                constructedMessage.append(data.getDisplayName()).append(" ");
            } else {
                constructedMessage.append("null");
            }

            if (targets != null) {
                constructedMessage.append("to ");
                for (int i = 0; i < targets.size(); i++) {
                    constructedMessage.append(targets.get(i).getDisplayName().getString());
                    if (i + 2 == targets.size()) {
                        constructedMessage.append(" and ");
                    } else if (i + 1 == targets.size()) {
                        constructedMessage.append(" ");
                    } else {
                        constructedMessage.append(", ");
                    }
                }
            } else {
                constructedMessage.append("to source player ");
            }

            if (origin != null) {
                constructedMessage.append("at ").append(origin).append(" ");
            } else {
                constructedMessage.append("at player cutscene_id ");
            }

            if (rotation != null) {
                constructedMessage.append("with ").append(rotation).append(" degrees of rotation");
            } else {
                constructedMessage.append("without rotation");
            }

            player.sendSystemMessage(Component.literal(constructedMessage.toString())); */

        }
        return Command.SINGLE_SUCCESS;
    }
}
