package cn.fanziyun.doubledoor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class DoubledoorMod implements ModInitializer {

    /** Mod ID, must match the id in fabric.mod.json */
    public static final String MOD_ID = "doubledoor";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static DoubledoorConfig CONFIG = new DoubledoorConfig();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onInitialize() {
        loadConfig();
        LOGGER.info("Doubledoor mod initialized!");
    }

    private void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                CONFIG = GSON.fromJson(json, DoubledoorConfig.class);
                LOGGER.info("Config loaded");
            } catch (IOException e) {
                LOGGER.error("Failed to read config", e);
            }
        } else {
            saveConfig(configPath);
            LOGGER.info("Default config created");
        }
    }


    private void saveConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(CONFIG));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }
}
