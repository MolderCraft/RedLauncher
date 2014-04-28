package RedLauncher;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * Браузер
 *
 * @author RedCreepster
 */
public class Browser extends Region {

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();
    private final Bridge bridge;

    /**
     *
     * @param launcher Ссылка на экземпляр лаунчера
     * @param prefWidth Ширина окна
     * @param prefHeight Высота окна
     */
    public Browser(Launcher launcher, double prefWidth, double prefHeight) {
        launcher.setWebView(webView);
        bridge = new Bridge(launcher);
        webView.setPrefSize(prefWidth, prefHeight);
        webView.getEngine().setJavaScriptEnabled(true);
        webView.setContextMenuEnabled(false);
        webEngine.load(this.getClass().getResource("/html/index.html").toExternalForm());
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", bridge);
        getChildren().add(webView);
    }
}
