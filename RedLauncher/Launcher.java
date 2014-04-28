package RedLauncher;

import RedLauncher.Config.Config;
import static RedLauncher.Config.Messages.*;
import static RedLauncher.Downloader.getText;
import static RedLauncher.Downloader.getUrl;
import static RedLauncher.UnZip.getListFile;
import static RedLauncher.UnZip.unZip;
import static RedLauncher.Utils.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.json.json.getJsonFromPath;

/**
 * Главный класс приложения
 *
 * @author RedCreepster
 */
public class Launcher extends Thread {

    private static final Logger LOG = Logger.getLogger(Launcher.class.getName());
    private WebView webView;
    private String pathToGameDir;
    private String pathToConfig;
    String version;
    Collection<String> listUrl = new LinkedList<>();
    Collection<String> listFiles = new LinkedList<>();
    boolean versionJsonObtained = false;

    /**
     * Экземпляр класса загрузчика
     */
    public Downloader downloader;
    /**
     * Настройки приложения
     */
    public Config config;

    private void init() {
        LOG.log(Level.INFO, "{0}{1}", new Object[]{messages("GAME_DIR_SET_TO").getTextContent(), pathToGameDir = getPathToGameDir()});

        buildDirectory(new File(pathToGameDir));

        Date d = new Date();
        String logsFile = pathToGameDir + "/" + config("LOGS_DIR").getTextContent() + "/" + new SimpleDateFormat("dd.MM.yy/hh.mm.ss").format(d) + "_red.log";
        buildDirectory(extractDir(logsFile));
        setGlobalLogger(logsFile, 1048576);

        pathToConfig = pathToGameDir + "/" + config("LAUNCHER_JSON").getTextContent();
        config = new Config(pathToConfig);
        update();
    }

    /**
     * Проверка соединения с и интернетом
     *
     * @return Статус соединения с интернетом
     */
    public boolean checkInternetConnection() {
        try {
            //connect to moldercraft.com
            HttpURLConnection conMC = (HttpURLConnection) new URL("http://moldercraft.com").openConnection();
            conMC.setRequestMethod("HEAD");

            //connect to ya.ru
            HttpURLConnection conYa = (HttpURLConnection) new URL("http://ya.ru").openConnection();
            conYa.setRequestMethod("HEAD");

            //connect to google.com
            HttpURLConnection conGo = (HttpURLConnection) new URL("http://google.com").openConnection();
            conGo.setRequestMethod("HEAD");

            return conMC.getResponseCode() == HttpURLConnection.HTTP_OK || conYa.getResponseCode() == HttpURLConnection.HTTP_OK || conGo.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            LOG.severe(e.toString());
            return false;
        }
    }

    private String getPathToGameDir() {
        if (isWindows()) {
            return System.getenv("APPDATA") + "/" + config("GAME_DIR").getTextContent();
        }
        if (isUnix() || isMac()) {
            return System.getProperty("user.home") + "/" + config("GAME_DIR").getTextContent();
        }
        return null;
    }

    /**
     * Получение номера версии лаунчера из интернета
     *
     * @return Номер версии лаунчера из интернета
     */
    public static String getVersion() {
        return getText(config("UPDATE_URL").getTextContent() + "/" + config("LAUNCHER_VERSION_URL").getTextContent());
    }

    private void cmd(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            LOG.severe(ex.toString());
        }
    }

    private void update() {
        if (!checkInternetConnection()) {
            return;
        }

        LOG.log(Level.INFO, "{0}{1}{2}{3}", new Object[]{messages("CURENT_VERSOIN").getTextContent(), launcher("version").getTextContent(), messages("LAST_VERSION").getTextContent(), getVersion()});

        if (!launcher("version").getTextContent().equals(getVersion())) {
            LOG.info(messages("UPDATING_LAUNCHER").getTextContent());
            String thisPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
            try {
                File file = File.createTempFile("launcher", ".tmp");
                getUrl(config("UPDATE_URL").getTextContent() + "/" + config("LAUNCHER_URL").getTextContent(), file.getAbsolutePath());

                cmd("java -jar " + file.getAbsolutePath() + " " + thisPath);
                System.exit(0);
            } catch (IOException ex) {
                LOG.severe(ex.toString());
            }
        }
    }

    /**
     * Пост обновление лаунчера
     *
     * @param path Путь к оригиналу лаунчера
     */
    public static void postUpdate(String path) {
        try {
            File sourse = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            File f = new File(path);

            f.delete();
            Files.copy(sourse.toPath().normalize(), f.toPath());
        } catch (IOException ex) {
            LOG.severe(ex.toString());
        } finally {
            try {
                Runtime.getRuntime().exec("java -jar " + path);
                System.exit(0);
            } catch (IOException ex) {
                LOG.severe(ex.toString());
            }
        }
    }

    /**
     * Вход в игру
     */
    public void joinToGame() {
        config.save();
        runClient();
    }

    private void runClient() {
        listUrl.clear();
        listFiles.clear();

        version = getText(config("UPDATE_URL").getTextContent() + "/get-version.php?server=" + config.getServer());
        String url = "http://s3.amazonaws.com/Minecraft.Download/versions/" + version + "/" + version + ".json";
        String jsonPath = pathToGameDir + "/" + config.getServer() + "/versions/" + version + "/" + version + ".json";

        if (!versionJsonObtained) {
            getUrl(url, jsonPath);
            versionJsonObtained = true;
        }

        if (!checkLibs() || !checkClientHash()) {
            updateClient();
            return;
        }

        extractNatives();
        //cmd("cmd /c " + getLaunchString());
        //System.exit(0);
    }

    private boolean checkClientHash() {
        String pathToFile = pathToGameDir + "/" + config.getServer() + "/versions/" + version + "/" + version + ".jar";
        String clientHash = config("UPDATE_URL").getTextContent() + "/get-hash.php?f=" + config.getServer() + "/" + version + ".jar";

        LOG.log(Level.INFO, "{0}{1}", new Object[]{messages("MINECRAFT_VERSION").getTextContent(), version});

        File jar = new File(pathToFile);
        if (!jar.exists() || !getText(clientHash).equals(getMD5File(pathToFile))) {
            String url = config("UPDATE_URL").getTextContent() + "/" + config.getServer() + "/" + version + ".jar";

            listUrl.add(url);
            listFiles.add(pathToFile);

            return false;
        }

        return true;
    }

    private boolean checkLibs() {
        String jsonPath = pathToGameDir + "/" + config.getServer() + "/versions/" + version + "/" + version + ".json";
        JSONArray libs = getJsonFromPath(jsonPath).getJSONArray("libraries");
        Collection<String> result = getList(libs, "/", ".jar", System.getProperty("sun.arch.data.model"), '/');

        int count = 0;
        for (int i = 0; i < result.size(); i++) {
            if (!getSHA1File(pathToGameDir + "/" + config.getServer() + "/libraries" + result.toArray()[i]).equals(getText(config("LIBS_URL").getTextContent() + result.toArray()[i] + ".sha1"))) {
                listUrl.add(config("LIBS_URL").getTextContent() + result.toArray()[i]);
                listFiles.add(pathToGameDir + "/" + config.getServer() + "/libraries" + result.toArray()[i]);
                count++;
            }
        }

        return count <= 0;
    }

    private Collection<String> getList(JSONArray libs, String prefix, String sufix, String arch, char separator) {
        Collection<String> result = new LinkedList<>();

        for (int i = 0; i < libs.length(); i++) {
            String buf = parseString(libs.getJSONObject(i).getString("name"), separator);
            if (libs.getJSONObject(i).has("natives")) {
                buf += "-" + libs.getJSONObject(i).getJSONObject("natives").getString(getOSName()).replace("${arch}", arch);
            }
            result.add(prefix + buf + sufix);
        }
        return result;
    }

    private void updateClient() {
        LOG.info(messages("UPDATING_CLIENT").getTextContent());

        downloader = new Downloader(listUrl, listFiles);
        downloader.start();
        webView.getEngine().executeScript("updateLoading();");
    }

    private void extractNatives() {
        String jsonPath = pathToGameDir + "/" + config.getServer() + "/versions/" + version + "/" + version + ".json";
        String libsPath = pathToGameDir + "/" + config.getServer() + "/libraries/";
        String natinesPath = pathToGameDir + "/" + config.getServer() + "/versions/" + version + "/natives/";

        JSONArray libs = getJsonFromPath(jsonPath).getJSONArray("libraries");

        buildDirectory(extractDir(natinesPath));

        for (int i = 0; i < libs.length(); i++) {
            if (libs.getJSONObject(i).has("natives")) {
                String buf = libsPath + parseString(libs.getJSONObject(i).getString("name"), '/');
                if (libs.getJSONObject(i).getJSONObject("natives").has(getOSName())) {
                    buf += "-" + libs.getJSONObject(i).getJSONObject("natives").getString(getOSName()).replace("${arch}", System.getProperty("sun.arch.data.model"));

                    buf += ".jar";

                    Collection<String> natives = getListFile(buf);

                    for (int j = 0; j < natives.size(); j++) {
                        if (!new File(natinesPath + natives.toArray()[j].toString()).exists()) {
                            unZip(buf, natinesPath);
                        }
                    }
                }
            }
        }

        deletePath(natinesPath + "META-INF");
    }

    private String getLaunchString() {
        String jsonPath = pathToGameDir + "/" + config.getServer() + "/versions/" + version + "/" + version + ".json";
        JSONObject json = getJsonFromPath(jsonPath);

        String prefix;
        String arch = System.getProperty("sun.arch.data.model");

        if (isWindows()) {
            prefix = "%appdata%\\" + config("GAME_DIR").getTextContent() + "\\" + config.getServer() + "\\libraries\\";
        } else {
            prefix = "";
        }
        Collection<String> libs = getList(json.getJSONArray("libraries"), prefix, ".jar", arch, '\\');
        libs.add("%appdata%\\" + config("GAME_DIR").getTextContent() + "\\" + config.getServer() + "\\versions\\" + version + "\\" + version + ".jar;");

        String result = "javaw -Xmx" + config.getRam() + "M -Xms512M -cp \"";

        for (int i = 0; i < libs.size(); i++) {
            result += libs.toArray()[i] + ";";
        }

        result += "\" -Djava.library.path=\"%appdata%\\" + config("GAME_DIR").getTextContent() + "\\" + config.getServer() + "\\versions\\" + version + "\\natives" + "\" ";

        String minecraftArguments = " " + json.getString("minecraftArguments") + " --server ${server}";

        String[] search = {
            "${auth_player_name}",
            "${version_name}",
            "${game_directory}",
            "${assets_root}",
            "${assets_index_name}",
            "${auth_uuid}",
            "${auth_access_token}",
            "${user_properties}",
            "${user_type}",
            "${server}"
        };

        String[] replace = {
            config.getLogin(),
            version,
            "%appdata%\\" + config("GAME_DIR").getTextContent() + "\\" + config.getServer(),
            "%appdata%\\" + config("GAME_DIR").getTextContent() + "\\" + config.getServer() + "\\assets",
            "{}",
            "{}",
            "random access token",
            "{}",
            "{}",
            config.getServer() + ".moldercraft.com"
        };

        minecraftArguments = replace(minecraftArguments, search, replace);

        result += json.getString("mainClass") + minecraftArguments;
        return result;
    }

    /**
     * Установка браузера
     *
     * @param webView Браузер
     */
    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void run() {
        init();
    }
}
