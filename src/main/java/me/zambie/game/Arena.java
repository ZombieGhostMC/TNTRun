package me.zambie.game;

import org.bukkit.Bukkit;
import org.bukkit.World;
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
import java.util.Set;

public class Arena {

    private static final Set<Arena> arenaSet = new HashSet<>();

    private final String name, world;
    private final Vector spawn;
    private boolean running;

    public Arena(String name, String world, Vector spawn) {
        this.name = name;
        this.world = world;
        this.spawn = spawn;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public Vector getSpawn() {
        return spawn;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", world='" + world + '\'' +
                ", spawn=" + spawn +
                ", running=" + running +
                '}';
    }

    public static Set<Arena> getSet() {
        return arenaSet;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void load(File dataFolder) throws IOException, ParseException {
        if (!dataFolder.exists()){
            dataFolder.mkdir();
        }

        File file = new File(dataFolder, "arena.json");

        if (!file.exists()){
            file.createNewFile();
        }else {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));

            JSONArray jsonArray = (JSONArray) jsonObject.get("arenas");

            for (Object arena : jsonArray){
                JSONObject jsonArena = (JSONObject) arena;

                String name = (String) jsonArena.get("name");
                String world = (String) jsonArena.get("world");

                Vector spawnVector;

                {
                    JSONObject jsonLobbyVector = (JSONObject) jsonArena.get("spawn");
                    double x = (double) jsonLobbyVector.get("x");
                    double y = (double) jsonLobbyVector.get("y");
                    double z = (double) jsonLobbyVector.get("z");
                    spawnVector = new Vector(x, y, z);
                }
                arenaSet.add(new Arena(name, world, spawnVector));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void save(File dataFolder){
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (Arena arena : arenaSet){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", arena.getName());
            jsonObject.put("world", arena.getWorld().getName());

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("x", arena.getSpawn().getX());
            jsonObject1.put("y", arena.getSpawn().getY());
            jsonObject1.put("z", arena.getSpawn().getZ());

            jsonObject.put("spawn", jsonObject1);
            jsonArray.add(jsonObject);
        }

        data.put("arenas", jsonArray);

        File file = new File(dataFolder, "arena.json");

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(data.toJSONString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<Arena> get(){
        return arenaSet.stream().filter(arena -> !arena.isRunning()).findFirst();
    }
}
