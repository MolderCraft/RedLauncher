package RedLauncher;

import static RedLauncher.Config.Messages.launcher;

/**
 * Класс, связующий лаунчер и браузер
 *
 * @author RedCreepster
 */
public class Bridge {

    private final Launcher launcher;

    /**
     * Конструктор класса
     *
     * @param launcher Ссылка на экземпляр лаунчера
     */
    public Bridge(Launcher launcher) {
        this.launcher = launcher;
        this.launcher.start();
    }

    /**
     * Вход в игру
     */
    public void joinToGame() {
        launcher.joinToGame();
    }

    /**
     * Получение логина пользователя
     *
     * @return Логин польpователя
     */
    public String getLogin() {
        return launcher.config.getLogin();
    }

    /**
     * Получение сервера
     *
     * @return Сервер
     */
    public String getServer() {
        return launcher.config.getServer();
    }

    /**
     * Получеине количесва ОЗУ машины
     *
     * @return Количесво ОЗУ машины
     */
    public String getMaxRam() {
        return launcher.config.getMaxRam();
    }

    /**
     * Получение количества выделяемого ОЗУ
     *
     * @return Количество выделяемого ОЗУ
     */
    public String getRam() {
        return launcher.config.getRam();
    }

    /**
     * Получение версии лаунчера
     *
     * @return Версия лаунчера
     */
    public String getVersion() {
        return launcher("version").getTextContent();
    }

    /**
     * Проверка статуса потока загрузчика
     *
     * @return Статус работы загрузчика
     */
    public boolean isDownload() {
        return launcher.downloader.isAlive();
    }

    /**
     * Получение позиции загрузки файла
     *
     * @return Позиция загрузки файла
     */
    public int getDownloadPosition() {
        return launcher.downloader.getPosition();
    }

    /**
     * Получение длинны загружаемого файла
     *
     * @return Длинна загружаемого файла
     */
    public int getMaxDownloadPosition() {
        return launcher.downloader.getMaxPosition();
    }

    /**
     * Получение имени загружаемого фалйа
     *
     * @return Имя загружаемого файла
     */
    public String getCurentFile() {
        return launcher.downloader.getCurentFile();
    }

    /**
     * Установка логина
     *
     * @param login Логин
     */
    public void setLogin(String login) {
        launcher.config.setLogin(login);
    }

    /**
     * Установка сервера
     *
     * @param server Сервер
     */
    public void setServer(String server) {
        launcher.config.setServer(server);
    }

    /**
     * Установка размера выделяемого ОЗУ
     *
     * @param ram ОЗУ
     */
    public void setRam(String ram) {
        launcher.config.setRam(ram);
    }

    /**
     * Вывод obj
     *
     * @param obj Объект для вывода
     */
    public void sout(Object obj) {
        System.out.println(obj);
    }
}
