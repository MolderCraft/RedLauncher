package RedLauncher.Config;

import static RedLauncher.Config.XML.getNodeByName;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Класс для получения Node объекта из внутренних файлов
 *
 * @author RedCreepster
 */
public class Messages {

    private static final Logger LOG = Logger.getLogger(Messages.class.getName());

    /**
     * Получение Node объекта из /res/xml/RedLauncher.xml по ключу
     *
     * @param key Ключ, по которому будет возврашён Node объект из файла
     * /res/xml/RedLauncher.xml
     * @return Node объект по ключу key
     */
    public static Node launcher(String key) {
        try {
            return getNodeByName("/res/xml/RedLauncher.xml", key);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            LOG.severe(ex.toString());
        }
        return null;
    }

    /**
     * Получение Node объекта из /res/xml/config.xml по ключу
     *
     * @param key Ключ, по которому будет возврашён Node объект из файла
     * /res/xml/config.xml
     * @return Node объект по ключу key
     */
    public static Node config(String key) {
        try {
            return getNodeByName("/res/xml/config.xml", key);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            LOG.severe(ex.toString());
        }
        return null;
    }

    /**
     * Получение Node объекта из /res/xml/messages.xml по ключу
     *
     * @param key Ключ, по которому будет возврашён Node объект из файла
     * /res/xml/messages.xml
     * @return Node объект по ключу key
     */
    public static Node messages(String key) {
        try {
            return getNodeByName("/res/xml/messages.xml", key);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            LOG.severe(ex.toString());
        }
        return null;
    }
}
