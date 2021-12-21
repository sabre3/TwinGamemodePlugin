package me.sabre.twingamemode;
import com.destroystokyo.paper.utils.PaperPluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* lazily made by sabre3
*
* the amount of boilerplate and useless overhead in java is insane lmao
* */

public final class TwinGamemode extends JavaPlugin {

    private ArrayList<TWGWorld> worlds;

    @Override
    public void onEnable() {
        // Plugin startup logic

        //init dirs
        initDirs();

        //init config
        initConfig();

        //init world config
        this.worlds = initWorlds(this.getConfig());

        //init logger
        Logger logger = PaperPluginLogger.getLogger("TwinGamemode");
        logger.setLevel(Level.FINER);

        //register command
        this.getCommand("twg_generate").setExecutor(new TWGenerateWall());

        //start new player listener
        getServer().getPluginManager().registerEvents(new NewPlayerListener(), this);

        //start location listener
        getServer().getPluginManager().registerEvents(new LocationListener2(worlds, this.getConfig()), this);
        
        logger.log(Level.INFO, "Has successfully started!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PaperPluginLogger.getLogger("TWG").log(Level.INFO, "Has successfully shutdown!");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new DividerGenerator(this.getConfig());
    }

    public static void initDirs() {

        //create playerdata dirs if they don't exist
        File pdC = new File(FileLocs.PDC);
        File pdS = new File(FileLocs.PDS);

        pdC.mkdirs();
        pdS.mkdirs();

    }

    public void initConfig() {
        this.saveDefaultConfig();
    }

    public static ArrayList<TWGWorld> initWorlds(FileConfiguration config) {
        Logger logger =  PaperPluginLogger.getLogger("TWG");

        //init worlds
        ArrayList<TWGWorld> tworlds = new ArrayList<>();
        List<Map<?,?>> worlds = config.getMapList("worlds");

        try {

            for (Map<?, ?> map : worlds) {
                Map<String, ?> world = (Map<String, ?>) map.get("level");
                String worldname = (String) world.get("name");

                Map<String, ?> divider = (Map<String, ?>) world.get("divider");
                double coord = (double) divider.get("coord");
                double offset = (double) divider.get("offset");
                boolean vert = divider.get("type").equals("vertical");
                boolean cpos = divider.get("creative").equals("positive");

                World worldObj = Bukkit.getWorld(worldname);

                TWGWorld worldW = new TWGWorld(worldObj, worldname, coord, offset, vert, cpos);
                tworlds.add(worldW);

            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load config!");
            logger.throwing("LocationListener2", "LocationListener2()", e);
        }
        return tworlds;
    }
}
