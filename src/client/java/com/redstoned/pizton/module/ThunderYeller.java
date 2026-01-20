package com.redstoned.pizton.module;

import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.machinery.ToggleableModule;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.sounds.SoundEvents;

public class ThunderYeller extends ToggleableModule {
    public boolean foundThunder = false;
    private static final Logger LOGGER = Pizton.loggerFor(ThunderYeller.class);

    @Override
    public String description() {
        return "Make it known when there's thunder";
    }

    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!this.ENABLED) return;

            if (client.level != null) {
                assert client.player != null;
                if (client.level.isThundering() && !foundThunder) {
                    LOGGER.debug("Thunder detected!");
                    client.player.displayClientMessage(Component.literal("IT'S THUNDERING"), false);
                    client.level.playSound(client.player, client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.MASTER, 1, 1);
                    client.gui.setTitle(Component.literal("THUNDER!!!!"));
                    foundThunder = true;
                }
                if (!client.level.isThundering() && foundThunder) {
                    LOGGER.debug("Thunder stopped.");
                    foundThunder = false;
                }
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!this.ENABLED) return;

            LOGGER.debug("resetting tracker");
            foundThunder = false;
        });
    }

    @Override
    public void enable() {
        super.enable();
        this.foundThunder = false;
    }
}
