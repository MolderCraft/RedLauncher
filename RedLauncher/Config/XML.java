package RedLauncher.Config;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Класс для чтения простых xml документов
 *
 * @author RedCreepster
 */
public class XML {

    /**
     * Получение Node по ключу
     *
     * @param path Путь к xml документу
     * @param nodeName Имя возвращаемого эллемента
     * @return Node по ключу nodeName
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Node getNodeByName(String path, String nodeName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(Class.class.getResourceAsStream(path));
        doc.getDocumentElement().normalize();
        return getNodeByName(doc, nodeName);
    }

    /**
     * Получение Node по ключу
     *
     * @param doc Документ для парсинга
     * @param nodeName Имя возвращаемого эллемента
     * @return Node по ключу nodeName
     */
    public static Node getNodeByName(Document doc, String nodeName) {
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        return getNodeByName(nodes, nodeName);
    }

    /**
     * Получение Node по ключу
     *
     * @param node Node для парсинга
     * @param nodeName Имя возвращаемого эллемента
     * @return Node по ключу nodeName
     */
    public static Node getNodeByName(Node node, String nodeName) {
        return getNodeByName(node.getChildNodes(), nodeName);
    }

    /**
     * Получение Node по ключу
     *
     * @param nodes NodeList для парсинга
     * @param nodeName Имя возвращаемого эллемента
     * @return Node по ключу nodeName
     */
    public static Node getNodeByName(NodeList nodes, String nodeName) {
        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);
            if (node.getNodeName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }
}
