package RedLauncher;

import static RedLauncher.Config.Messages.messages;
import static RedLauncher.Utils.buildDirectory;
import static RedLauncher.Utils.extractDir;
import static RedLauncher.Utils.extractFileName;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс для загрузки файлов/получения текста из сети
 *
 * @author RedCreepster
 */
public class Downloader extends Thread {

    private static final Logger LOG = Logger.getLogger(Downloader.class.getName());
    private Collection<String> urlList = new LinkedList<>();
    private Collection<String> fileList = new LinkedList<>();
    private String curentFile;
    private int maxPosition;
    private int position;
    private int speed;
    private URL urlConnection;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    /**
     * Конструктор класса без доп. параметров
     *
     * @param url Ссылка для загрузки
     */
    public Downloader(String url) {
        this.urlList.add(url);
        this.fileList.add(extractFileName(url));
    }

    /**
     * Конструктор класса с доп. параметрами
     *
     * @param url Ссылка для загрузки
     * @param pathToSave Путь для сохранения
     */
    public Downloader(String url, String pathToSave) {
        this.urlList.add(url);
        this.fileList.add(pathToSave);
    }

    /**
     * Конструктор класса без доп. параметров
     *
     * @param urlList Коллекция строк - ссылок
     */
    public Downloader(Collection<String> urlList) {
        this.urlList = urlList;
        for (int i = 0; i < urlList.size(); i++) {
            this.fileList.add(extractFileName(urlList.toArray()[i].toString()));
        }
    }

    /**
     * Конструктор класса с доп. параметрами
     *
     * @param urlList Коллекция строк - ссылок
     * @param fileList Коллекция строк - путей сохранения файлов
     */
    public Downloader(Collection<String> urlList, Collection<String> fileList) {
        this.urlList = urlList;
        this.fileList = fileList;
    }

    private void getUrl() {
        getUrl(0);
    }

    private void getUrl(int i) {
        try {
            LOG.log(Level.INFO, "{0}{1}{2}{3}", new Object[]{messages("BEGINING").getTextContent(), urlList.toArray()[i], messages("IN_PATH").getTextContent(), fileList.toArray()[i]});
            urlConnection = new URL(urlList.toArray()[i].toString());
            buildDirectory(extractDir(fileList.toArray()[i].toString()));
            maxPosition = getUrlLength();

            bis = new BufferedInputStream(urlConnection.openStream());
            bos = new BufferedOutputStream(new FileOutputStream((String) fileList.toArray()[i]));

            byte[] data = new byte[1024];

            int x;
            int c = 0;
            long startTime = System.nanoTime();
            while ((x = bis.read(data, 0, 1024)) >= 0) {
                bos.write(data, 0, x);
                c += 1024;
                position = c;
                double estimatedTime = System.nanoTime() - startTime;
                speed = (int) ((c * (1000000000 / estimatedTime)) / 1024);
            }
        } catch (IOException ex) {
            LOG.severe(ex.toString());
        } finally {
            try {
                bos.close();
                bis.close();
                LOG.log(Level.INFO, "{0}{1}{2}{3}{4}", new Object[]{messages("BEGINING").getTextContent(), urlList.toArray()[i], messages("IN_PATH").getTextContent(), fileList.toArray()[i], messages("ENDED").getTextContent()});
            } catch (IOException ex) {
                LOG.severe(ex.toString());
            }
        }
        this.maxPosition = 0;
        this.position = 0;
        this.speed = 0;
    }

    /**
     * Загрузка файла из интернета в переменную
     *
     * @param url Ссылка для загрузки
     * @return Текст по ссылке
     */
    public static String getText(String url) {
        try {
            URL urlConnection = new URL(url);
            StringBuilder text;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.openConnection().getInputStream()))) {
                text = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    text.append(inputLine);
                }
            }

            return text.toString();
        } catch (IOException ex) {
            LOG.severe(ex.toString());
            return null;
        }
    }

    /**
     * Загрузка файла без создания класса
     *
     * @param url Ссылка для загрузки
     * @param targetFile Путь для сохранения
     */
    public static void getUrl(String url, String targetFile) {
        Downloader downloader = new Downloader(url, targetFile);
        downloader.getUrl();
    }

    /**
     * Загрузка фалй без создания класса, без указания пути сохранения
     *
     * @param url Ссылка для загрузки
     * @return Путь к сохранённому файлу
     */
    public static String getUrl(String url) {
        Downloader downloader = new Downloader(url);
        downloader.getUrl();
        File f = (File) downloader.fileList.toArray()[0];
        return f.getPath();
    }

    private int getUrlLength() {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) urlConnection.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception:\n", e);
            return -1;
        }
    }

    /**
     * Получение длинны файла
     *
     * @return Длинна файла
     */
    public int getMaxPosition() {
        return maxPosition;
    }

    /**
     * Получение позиции скачивания файла
     *
     * @return Позиция скачивания файла
     */
    public int getPosition() {
        return position;
    }

    /**
     * Получение скорости скачивания файла
     *
     * @return Скорость скачивания
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Получение имени скачиваемого файла
     *
     * @return Имя скачиваемого файла
     */
    public String getCurentFile() {
        return curentFile;
    }

    @Override
    public void run() {
        for (int i = 0; i < urlList.size(); i++) {
            curentFile = extractFileName((String) fileList.toArray()[i]);
            getUrl(i);
        }
    }
}
