package me.zambie.game.listener;

import me.zambie.game.Arena;
import me.zambie.game.GameCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

public class SignListener implements Listener {

    private static final HashSet<Sign> set = new HashSet<>();
    private static final String[] strings = new String[] {
            "▉▇▆▅▄▃▂▁",
            "▇▉▇▆▅▄▃▂",
            "▆▇▉▇▆▅▄▃",
            "▅▆▇▉▇▆▅▄",
            "▄▅▆▇▉▇▆▅",
            "▃▄▅▆▇▉▇▆",
            "▂▃▄▅▆▇▉▇",
            "▁▂▃▄▅▆▇▉",
            "▂▃▄▅▆▇▉▇",
            "▃▄▅▆▇▉▇▆",
            "▄▅▆▇▉▇▆▅",
            "▅▆▇▉▇▆▅▄",
            "▆▇▉▇▆▅▄▃",
            "▇▉▇▆▅▄▃▂",
    };
    private static int i = 0;

    private final GameCore core;

    public SignListener(GameCore core) {
        this.core = core;
        try {
            loadSigns(core.getDataFolder());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }finally {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Sign sign : set){
                        sign.setLine(0, "§a[JOIN]");
                        sign.setLine(1, "§lTNT RUN");

                        Optional<Arena> arena = Arena.get();
                        if (arena.isPresent()){
                            sign.setLine(2, String.format("%s in queue", arena.get().getWorld().getPlayers().size()));
                        }else {
                            sign.setLine(2, "§cNo Arena");
                        }

                        sign.setLine(3, ChatColor.BOLD + strings[i]);
                        sign.update();
                    }
                    i = i >= strings.length - 1 ? 0 : i + 1;
                }
            }.runTaskTimer(core, 0L, 1L);
        }
    }

    public GameCore getCore() {
        return core;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        String s = event.getLine(0);
        if (s.equalsIgnoreCase("[TNT]")){
            Sign sign = (Sign) event.getBlock().getState();
            sign.setLine(0, "§a[JOIN]");
            sign.setLine(1, "§lTNT RUN");
            sign.setLine(2, "%s in queue");
            sign.setLine(3, "loading");
            sign.update();

            set.add(sign);
            event.getPlayer().sendMessage("§eAdded sign");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!event.hasBlock()){
            return;
        }

        Block block = event.getClickedBlock();
        BlockState state = block.getState();

        if (!(state instanceof Sign)){
            return;
        }

        Sign sign = (Sign) state;
        if (set.contains(sign)){
            player.performCommand("game queue");
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        save(core.getDataFolder());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadSigns(File dataFolder) throws IOException, ParseException {
        if (!dataFolder.exists()){
            dataFolder.mkdir();
        }

        File file = new File(dataFolder, "sign.json");

        if (!file.exists()){
            file.createNewFile();
        }else {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));

            JSONArray jsonArray = (JSONArray) jsonObject.get("signs");

            for (Object sign : jsonArray){
                JSONObject jsonArena = (JSONObject) sign;

                String world = (String) jsonArena.get("world");
                Vector vector;

                {
                    JSONObject jsonLobbyVector = (JSONObject) jsonArena.get("vector");
                    double x = (double) jsonLobbyVector.get("x");
                    double y = (double) jsonLobbyVector.get("y");
                    double z = (double) jsonLobbyVector.get("z");
                    vector = new Vector(x, y, z);
                }

                Location location = new Location(Bukkit.getWorld(world), vector.getX(), vector.getY(), vector.getZ());
                Block block = location.getBlock();
                BlockState state = block.getState();
                if (state instanceof Sign){
                    Sign signState = (Sign) state;
                    set.add(signState);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void save(File dataFolder){
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (Sign sign : set){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("world", sign.getWorld().getName());

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("x", sign.getLocation().getX());
            jsonObject1.put("y", sign.getLocation().getY());
            jsonObject1.put("z", sign.getLocation().getZ());

            jsonObject.put("vector", jsonObject1);
            jsonArray.add(jsonObject);
        }

        data.put("signs", jsonArray);

        File file = new File(dataFolder, "sign.json");

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(data.toJSONString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
