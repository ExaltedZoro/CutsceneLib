package net.exaltedzoro.cutscenelib.networking;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkingEvents {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                CutsceneTriggerPayload.TYPE,
                CutsceneTriggerPayload.STREAM_CODEC,
                new MainThreadPayloadHandler<>(
                    CutsceneTriggerPayload::handleData
                )
        );
    }
}
