package com.redstoned.pizton.module;

import com.redstoned.pizton.machinery.ToggleableModule;

public class NeverReduceDebugInfo extends ToggleableModule {
    @Override
    public String description() {
        return "Prevent server from disabling f3 infolines";
    }
}
