package runners;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "stepdefinitions",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json"
    },
    publish = false
)
public class TestRunner {
    private static WebDriver driver;

    @BeforeClass
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterClass
    public static void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
            WebDriverManager.chromedriver().quit();
            
            // Use ProcessBuilder for better process control
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                processBuilder.command("pkill", "-f", "chromedriver");
                Process process = processBuilder.start();
                process.waitFor();
                
                processBuilder.command("pkill", "-f", "chrome");
                process = processBuilder.start();
                process.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 