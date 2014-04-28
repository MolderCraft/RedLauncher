package org.json;

import RedLauncher.Launcher;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс для работы с json фалами/ресурсами
 *
 * @author RedCreepster
 */
public class json {

    /**
     *
     * @param path Путь к json ресурсу
     * @return json объект
     */
    public static JSONObject getJsonFromResource(String path) {
        InputStream in = json.class.getResourceAsStream(path);
        return loadJson(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     *
     * @param path Путь к json файлу
     * @return json объект
     */
    public static JSONObject getJsonFromPath(String path) {
        try {
            return loadJson(new BufferedReader(new FileReader(path)));
        } catch (FileNotFoundException e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private static JSONObject loadJson(BufferedReader reader) {
        StringBuilder fileData = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                fileData.append(line);
            }
            reader.close();
        } catch (IOException | JSONException e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, e);
        }
        return new JSONObject(fileData.toString());
    }
}
