package net.swill.autooakfarm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class AutoOakFarm implements ModInitializer {
    private static int tickCounter = 0;

    @Override
    public void onInitialize() {
        System.out.println("[AutoOakFarm] Мод активирован. Автоферма дуба запущена.");

        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < 1) return;
        tickCounter = 0;

        for (ServerWorld world : server.getWorlds()) {
            List<ServerPlayerEntity> players = world.getPlayers();
            for (ServerPlayerEntity player : players) {
                FarmLogic.tick(world, player);
            }
        }
    }
}
