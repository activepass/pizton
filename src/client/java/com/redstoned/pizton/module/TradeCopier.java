package com.redstoned.pizton.module;

import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.machinery.ToggleableModule;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class TradeCopier extends ToggleableModule {
    public static Logger LOGGER = Pizton.loggerFor(TradeCopier.class);

    @Override
    public String description() {
        return "Copy trades from wandering trader (Paper plugin)";
    }

    public record HeadSetMember(String set, Integer set_id, ItemStack stack, Boolean sold_out) {}

}
