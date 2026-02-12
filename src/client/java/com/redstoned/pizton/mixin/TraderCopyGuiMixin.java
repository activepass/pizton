package com.redstoned.pizton.mixin;

import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.module.TradeCopier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(MerchantScreen.class)
public abstract class TraderCopyGuiMixin extends AbstractContainerScreen<MerchantMenu> {
    public TraderCopyGuiMixin(MerchantMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        //TODO Auto-generated constructor stub
    }

    @Unique
    private boolean isParsed = false;
    @Unique
    private boolean isTrader = false;
    @Unique
    private static final TradeCopier pizton$module = Pizton.fetchModule(TradeCopier.class);

    @Inject(at = @At("TAIL"), method = "<init>")
    private void checkTrader(MerchantMenu merchantMenu, Inventory inventory, Component component, CallbackInfo ci) {
        isTrader = this.title.getContents() instanceof TranslatableContents t
                && Objects.equals(t.getKey(), "entity.minecraft.wandering_trader");
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CallbackInfo ci) {
        if (pizton$module.enabled() && isTrader) addButtons();
    }

    @Inject(at = @At("HEAD"), method = "renderContents")
    private void checkParse(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!pizton$module.enabled() || !isTrader) return;
        if (!isParsed && !this.menu.getOffers().isEmpty() && this.sets.isEmpty()) {
            determineSets();
            addButtons();
            isParsed = true;
        }
    }

    @Unique
    private void addButtons() {
        int i = 0;
        for (String set : this.sets) {
            this.addRenderableWidget(new Button.Builder(Component.literal(String.format("Copy %s set", set)), b -> copySet(set)).pos(0, i * 20).build());
            i++;
        }
    }

    @Unique
    private final HashSet<String> sets = new HashSet<>();
    @Unique
    private final List<TradeCopier.HeadSetMember> set_members = new ArrayList<>();

    @Unique
    private void determineSets() {
        var offers = this.menu.getOffers();

        ArrayList<TradeCopier.HeadSetMember> heads = new ArrayList<>();

        for (MerchantOffer x : offers) {
            var z = x.getResult().get(DataComponents.LORE);
            var set_text = z.lines().stream().map(Component::getString).collect(Collectors.joining());
            if (set_text.isEmpty()) continue;

            int hash_index = set_text.indexOf("#");
            var set_name = remapSetName((hash_index == -1 ? set_text.substring(5) : set_text.substring(5, hash_index)).stripTrailing());
            var set_id = hash_index == -1 ? null : Integer.valueOf(set_text.substring(hash_index + 1));

            this.sets.add(set_name);
            this.set_members.add(new TradeCopier.HeadSetMember(set_name, set_id, x.getResult(), x.isOutOfStock()));
        }
    }

    @Unique
    private static String remapSetName(String set) {
        return switch (set) {
            case "Head" -> "Player";
            case "Microblock" -> "Vanilla";
            default -> set;
        };
    }

    @Unique
    private void copySet(String set) {
        String members = this.set_members
                .stream()
                .filter(headSetMember -> headSetMember.set().equals(set) && !headSetMember.sold_out())
                .map(x -> {
                    var p1 = set.equals("Player") ? x.stack().get(DataComponents.PROFILE).name().get() : x.stack().getCustomName().getString();
                    return p1 + (x.set_id() != null ? String.format(" (#%d)", x.set_id()) : "");
                })
                .collect(Collectors.joining(", "));
        String f = String.format("%s set: %s", set, members);
        TradeCopier.LOGGER.debug(f);
        Minecraft.getInstance().keyboardHandler.setClipboard(f);
    }
}