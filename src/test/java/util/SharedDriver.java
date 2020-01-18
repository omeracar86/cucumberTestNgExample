package util;

import cucumber.api.Scenario;
import cucumber.api.java8.En;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.net.URL;

/**
 * <p>
 * Example of a WebDriver implementation that has delegates all methods to a static instance (REAL_DRIVER) that is only
 * created once for the duration of the JVM. The REAL_DRIVER is automatically closed when the JVM exits. This makes * scenarios a lot faster since opening and closing a browser for each scenario is pretty slow. * To prevent browser state from leaking between scenarios, cookies are automatically deleted before every scenario. * </p>
 * <p>
 * A new instance of SharedDriver is created for each Scenario and passed to yor Stepdef classes via Dependency Injection
 * </p>
 * <p>
 * As a bonus, screenshots are embedded into the report for each scenario. (This only works
 * if you're also using the HTML formatter). * </p>
 * <p>
 * A new instance of the SharedDriver is created for each Scenario and then passed to the Step Definition classes'
 * constructor. They all receive a reference to the same instance. However, the REAL_DRIVER is the same instance throughout * the life of the JVM. * </p>
 */
public class SharedDriver extends EventFiringWebDriver implements En {
    private static final WebDriver REAL_DRIVER;
    private static final Thread CLOSE_THREAD = new Thread() {
        @Override
        public void run() {
            REAL_DRIVER.close();
        }
    };

    static {
        Runtime.getRuntime().addShutdownHook(CLOSE_THREAD);
        try {
            REAL_DRIVER = getBrowser();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new Error(throwable);
        }
    }

    public SharedDriver() {
        super(REAL_DRIVER);

        Before(manage()::deleteAllCookies);

        After((Scenario scenario)->{
            try {
                byte[] screenshot = getScreenshotAs(OutputType.BYTES);
                scenario.embed(screenshot, "image/png");
            } catch (WebDriverException somePlatformsDontSupportScreenshots) {
                System.err.println(somePlatformsDontSupportScreenshots.getMessage());
            }
        });
    }

    @Override
    public void close() {
        if (Thread.currentThread() != CLOSE_THREAD) {
            throw new UnsupportedOperationException("You shouldn't close this WebDriver. It's shared and will close when the JVM exits.");
        }
        super.close();
    }

    private static WebDriver getBrowser() throws Throwable {
        String desiredBrowserName = System.getProperty("browser", "chrome");
        WebDriver desiredBrowser = null;

        switch(desiredBrowserName) {
            case "ie":
                InternetExplorerDriverManager.getInstance().setup();
                desiredBrowser = new InternetExplorerDriver();
                break;
            case "chrome":
                ChromeDriverManager.getInstance().setup();
                desiredBrowser = new ChromeDriver();
                break;
            case "firefox":
                FirefoxDriverManager.getInstance().setup();
                desiredBrowser = new FirefoxDriver();
                break;
            default:
                //work out what to do when a browser isn't needed
                break;
        }
        return desiredBrowser;
    }
}