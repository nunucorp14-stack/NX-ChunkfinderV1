package com.donutsmp;

import com.donutsmp.addon.modules.ChunkFinder;
import com.donutsmp.addon.modules.PlayerESP;
import com.donutsmp.addon.modules.HomeSetter;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class DonutSMPAddon extends MeteorAddon {
    @Override
    public void onInitialize() {
        Modules.get().add(new ChunkFinder());
        Modules.get().add(new PlayerESP());
        Modules.get().add(new HomeSetter());
    }

    @Override
    public String getPackage() {
        return "com.donutsmp";
    }
}
