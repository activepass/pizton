package com.redstoned.pizton.module;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.redstoned.pizton.machinery.StaticModule;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class TabCopy extends StaticModule {
    @Override
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                literal("tabcopy")
                .then(argument("player", StringArgumentType.string())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggest(context.getSource().getOnlinePlayerNames(), builder))
                    .executes(context -> {
                        String player = StringArgumentType.getString(context, "player");
                        PlayerInfo pe = Minecraft.getInstance().getConnection().getPlayerInfo(player);
                        context.getSource().sendFeedback(pe.getTabListDisplayName());
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
        });
    }

    @Override
    public String description() {
        return "Get tab formatted player text";
    }
}
