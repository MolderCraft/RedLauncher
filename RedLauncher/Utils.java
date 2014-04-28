package RedLauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Вспомогательные утилиты
 *
 * @author RedCreepster
 */
public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    /**
     * Получение md5 хэша файла
     *
     * @param filePath Путь к файлу
     * @return Хэш файла
     */
    public static String getMD5File(String filePath) {
        return getMD5File(new File(filePath));
    }

    /**
     * Получение md5 хэша файла
     *
     * @param forHash File для получения хэша
     * @return Хэш файла
     */
    public static String getMD5File(File forHash) {
        if (!forHash.exists()) {
            return "";
        }
        try (InputStream is = new FileInputStream(forHash)) {
            MessageDigest complete;
            byte[] buffer = new byte[1024];

            complete = MessageDigest.getInstance("MD5");

            int numRead;
            while ((numRead = is.read(buffer)) > 0) {
                complete.update(buffer, 0, numRead);
            }

            return getHash(complete.digest());
        } catch (NoSuchAlgorithmException | IOException ex) {
            LOG.severe(ex.toString());
            return null;
        }
    }

    /**
     * Получение хэша массива байт
     *
     * @param forHash Массив байт
     * @return Хэш массива байт
     */
    public static String getHash(byte[] forHash) {
        String result = "";
        for (int i = 0; i < forHash.length; i++) {
            result += Integer.toString((forHash[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    /**
     * Получение sha1 хэша файла
     *
     * @param filePath Путь к файлу
     * @return Хэш файла
     */
    public static String getSHA1File(String filePath) {
        return getSHA1File(new File(filePath));
    }

    /**
     * Получение sha1 хэша файла
     *
     * @param forHash File для получения хэша
     * @return Хэш файла
     */
    public static String getSHA1File(File forHash) {
        if (!forHash.exists()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            InputStream fis = new FileInputStream(forHash);
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }
            return getHash(digest.digest());
        } catch (NoSuchAlgorithmException | FileNotFoundException ex) {
            LOG.severe(ex.toString());
        } catch (IOException ex) {
            LOG.severe(ex.toString());
        }
        return null;
    }

    /**
     * Проверка или создание директории
     *
     * @param file File директории
     * @return Статус проверки или создания директории
     */
    public static boolean buildDirectory(File file) {
        return file.exists() || file.mkdirs();
    }

    /**
     * Проверка или создание директории
     *
     * @param file Путь к директории
     * @return Статус проверки или создания директории
     */
    public static boolean buildDirectory(String file) {
        File f = new File(file);
        return f.exists() || f.mkdirs();
    }

    /**
     *
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("win"));
    }

    /**
     *
     * @return
     */
    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("nix") || os.contains("nux"));
    }

    /**
     *
     * @return
     */
    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("mac"));
    }

    /**
     * Получение имени ОС
     *
     * @return Имя ОС
     */
    public static String getOSName() {
        if (isWindows()) {
            return "windows";
        }
        if (isUnix()) {
            return "linux";
        }
        if (isMac()) {
            return "osx";
        }
        return "unknow os";
    }

    /**
     * Парсинг строки из version.json
     *
     * @param data Строка для парсинга
     * @param separator Разделитель
     * @return Распарсенная строка
     */
    public static String parseString(String data, char separator) {
        int pack = 0;
        int name = 1;
        int version = 2;

        String[] params = data.split(":");
        params[pack] = params[pack].replace('.', separator);

        return params[pack] + separator + params[name] + separator + params[version] + separator + params[name] + "-" + params[version];
    }

    /**
     * Получение директории из пути к фалу
     *
     * @param path Путь к файлу
     * @return Путь к директории, содержащей файл
     */
    public static String extractDir(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    /**
     * Замена значений в строках
     *
     * @param forReplace Строка для замены
     * @param search Массив строк для поиска
     * @param replace Массив строк для замены
     * @return Строка с произведённой заменой
     */
    public static String replace(String forReplace, String[] search, String[] replace) {
        for (int i = 0; i < search.length; i++) {
            forReplace = forReplace.replace(search[i], replace[i]);
        }
        return forReplace;
    }

    /**
     * Рекурсивное удаление директории
     *
     * @param path Путь к директории
     */
    public static void deletePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                deletePath(listFile.toString());
            }
        }
        file.delete();
    }

    /**
     * Получение имени файл из пути
     *
     * @param path Путь к файлу
     * @return Имя файла
     */
    public static String extractFileName(String path) {
        return path.substring(path.lastIndexOf('/') + 1, path.length());
    }

    /**
     * Установка глобального логгера
     *
     * @param path Путь к файлу для сохранения
     * @param size Размер файла
     */
    public static void setGlobalLogger(String path, int size) {
        try {
            Logger logger = Logger.getLogger("");
            FileHandler handler = new FileHandler(path, size, 1, true);
            handler.setLevel(Level.ALL);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException | SecurityException ex) {
            LOG.severe(ex.toString());
        }
    }
}
