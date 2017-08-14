package me.zambie.game.statistic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Statistics {

    private static final HashMap<UUID, Double> balanceMap = new HashMap<>();
    private static final HashMap<UUID, Double> perksMap = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadStatistics(File dataFolder) throws IOException, ParseException {
        if (!dataFolder.exists()){
            dataFolder.mkdir();
        }

        File statFolder = new File(dataFolder, "statistic");

        if (!statFolder.exists()){
            statFolder.mkdir();
        }

        File[] listFiles = statFolder.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (!file.exists()){
                    continue;
                }
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));


            }
        }
    }
}
