package RedLauncher.Config;

import static RedLauncher.Config.Messages.messages;
import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import static org.json.json.getJsonFromPath;

/**
 * Класс для работы с настройками лаунчера
 *
 * @author RedCreepster
 */
public class Config {

    private static final Logger log = Logger.getLogger(Config.class.getName());
    private final String pathToConfig;
    private JSONObject json;
    private JSONObject oldJson;

    /**
     * Конструктор класса
     *
     * @param pathToConfig Путь к json файлу настроек
     */
    public Config(String pathToConfig) {
        this.pathToConfig = pathToConfig;
        load();
    }

    private void load() {
        if (!new File(pathToConfig).exists()) {
            json = new JSONObject();
            json.put("server", "");
            json.put("login", "");
            json.put("ram", "");
            return;
        }
        json = getJsonFromPath(pathToConfig);
        oldJson = new JSONObject(json.toString());
        log.log(Level.INFO, "{0}{1}", new Object[]{messages("LOADING_CONFIG").getTextContent(), json.toString()});
    }

    /**
     * Сохранение настроек в json файл
     */
    public void save() {
        if (oldJson.toString().equals(json.toString())) {
            return;
        }
        try {
            try (PrintWriter jsonFile = new PrintWriter(new File(pathToConfig))) {
                jsonFile.print(json.toString());
                log.log(Level.INFO, "{0}{1}", new Object[]{messages("SAVING_CONFIG").getTextContent(), json.toString()});
                jsonFile.close();
                oldJson = new JSONObject(json.toString());
            }
        } catch (IOException e) {
            log.severe((Supplier<String>) e);
        }
    }

    /**
     * Получение логина пользователя
     *
     * @return Логин пользователя
     */
    public String getLogin() {
        return json.get("login").toString();
    }

    /**
     * Получение сервера
     *
     * @return Сервер
     */
    public String getServer() {
        return json.get("server").toString();
    }

    /**
     * Получеине количесва ОЗУ машины
     *
     * @return Количесво ОЗУ машины
     */
    public String getMaxRam() {
        long maxRam = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        return String.valueOf(maxRam / 1024 / 1024);
    }

    /**
     * Получение количества выделяемого ОЗУ
     *
     * @return Количество выделяемого ОЗУ
     */
    public String getRam() {
        return json.get("ram").toString();
    }

    /**
     * Установка логина
     *
     * @param login Логин
     */
    public void setLogin(String login) {
        json.put("login", login);
    }

    /**
     * Установка сервера
     *
     * @param server Сервер
     */
    public void setServer(String server) {
        json.put("server", server);
    }

    /**
     * Установка размера выделяемого ОЗУ
     *
     * @param ram ОЗУ
     */
    public void setRam(String ram) {
        json.put("ram", ram);
    }
}
