package RedLauncher;

import static RedLauncher.Config.Messages.messages;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Класс для распаковки ZIP файлов
 *
 * @author RedCreepster
 */
public class UnZip extends Thread {

    private static final Logger LOG = Logger.getLogger(UnZip.class.getName());
    private Collection<String> targetFiles = new LinkedList<>();
    private Collection<String> targetDirs = new LinkedList<>();
    private final LinkedList<ZipEntry> zfiles = new LinkedList<>();

    /**
     * Конструктор для распаковки одного файла
     *
     * @param targetFile Путь к zip файлу
     * @param targetDir Путь к директории для распаковки
     */
    public UnZip(String targetFile, String targetDir) {
        this.targetFiles.add(targetFile);
        this.targetDirs.add(targetDir);
    }

    /**
     * Конструктор для распаковки коллекции файлов
     *
     * @param targetFiles Коллекция строк - путей к файлам для распаковки
     * @param targetDirs Коллекция строк - путей к директориям для распаковки
     */
    public UnZip(Collection<String> targetFiles, Collection<String> targetDirs) {
        this.targetFiles = targetFiles;
        this.targetDirs = targetDirs;
    }

    private void unZip() {
        unZip(0);
    }

    private void unZip(int i) {
        if (!new File(targetFiles.toArray()[i].toString()).exists()) {
            return;
        }
        try {
            try (ZipFile zip = new ZipFile((String) targetFiles.toArray()[i])) {
                Enumeration entries = zip.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    if (entry.isDirectory()) {
                        new File(targetDirs.toArray()[i] + "/" + entry.getName()).mkdir();
                    } else {
                        zfiles.add(entry);
                    }
                }
                LOG.log(Level.INFO, "{0}{1}{2}{3}{4}{5}", new Object[]{
                    messages("EXTRACKTING_FILE").getTextContent(),
                    zfiles.size(), messages("FILES_FROM").getTextContent(),
                    targetFiles.toArray()[i], messages("IN_DIRECTORY").getTextContent(),
                    targetDirs.toArray()[i]});

                for (ZipEntry entry : zfiles) {
                    OutputStream out;
                    try (InputStream in = zip.getInputStream(entry)) {
                        out = new FileOutputStream(targetDirs.toArray()[i] + "/" + entry.getName());

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) >= 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                    out.close();
                }
            }
            LOG.log(Level.INFO, "{0}{1}{2}{3}{4}", new Object[]{
                messages("EXTRACKTING_FILE").getTextContent(),
                targetFiles.toArray()[i], messages("IN_DIRECTORY").getTextContent(),
                targetDirs.toArray()[i],
                messages("ENDED").getTextContent()});

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception:\n", ex);
        }
    }

    /**
     * Получение списка файлов в архиве
     *
     * @param targetFiles Коллексия строк - путей к файлам
     * @return Коллекция строк - имён файлов
     */
    public static Collection<String> getListFile(Collection<String> targetFiles) {
        Collection<String> result = new LinkedList<>();
        for (int i = 0; i < targetFiles.size(); i++) {
            result.addAll(getListFile(targetFiles.toArray()[i].toString()));
        }
        return result;
    }

    /**
     * Получение списка файлов в архиве
     *
     * @param targetFile Путь к файлу
     * @return Коллекция строк - имён файлов
     */
    public static Collection<String> getListFile(String targetFile) {
        Collection<String> result = new LinkedList<>();
        try {
            Enumeration entries = new ZipFile(targetFile).entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (!entry.isDirectory()) {
                    result.add(entry.getName());
                }
            }
        } catch (IOException ex) {
            LOG.severe(ex.toString());
        }
        return result;
        //return UnZip.getListFile(targetFile);
    }

    /**
     * Распаковка файла без создания класса
     *
     * @param targetFile Путь к zip файлу
     * @param targetDir Путь к директории для распаковки
     */
    public static void unZip(String targetFile, String targetDir) {
        UnZip xz = new UnZip(targetFile, targetDir);
        xz.unZip();
    }

    @Override
    public void run() {
        for (int i = 0; i < targetFiles.size(); i++) {
            unZip(i);
        }
    }
}
