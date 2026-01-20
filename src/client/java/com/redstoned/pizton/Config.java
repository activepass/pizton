package com.redstoned.pizton;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public record Config(List<String> enabled_modules) {
    public static final File config_file = FabricLoader.getInstance().getConfigDir().resolve("pizton.json").toFile();

    public static Config load() {
        try {
            JsonElement je = JsonParser.parseReader(new FileReader(config_file));
            DataResult<Config> result = Config.CODEC.parse(JsonOps.INSTANCE, je);
            return result.resultOrPartial(Pizton.LOGGER::error).orElseThrow();
        } catch (Exception e) {
            e.printStackTrace();
            return new Config(new ArrayList<>());
        }
    }

    public void save() {
        DataResult<JsonElement> result = Config.CODEC.encodeStart(JsonOps.INSTANCE, this);
        JsonElement e = result.resultOrPartial(Pizton.LOGGER::error).orElseThrow();
        try {
            var w = new FileWriter(config_file);
            new Gson().toJson(e, w);
            w.close();
        } catch (IOException ex) {
            Pizton.LOGGER.error("failed to save config");
            ex.printStackTrace();
        }
    }

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().fieldOf("enabled_modules").forGetter(Config::enabled_modules)
    ).apply(instance, Config::new));
}
