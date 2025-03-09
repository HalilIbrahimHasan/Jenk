package stepdefinitions;

import java.time.Duration;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AmazonSteps {
    private WebDriver driver;
    private WebDriverWait wait;
    private Scenario scenario;

    @Before
    public void setup(Scenario scenario) {
        this.scenario = scenario;
        
        try {
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        } catch (Exception e) {
            scenario.log("Failed to initialize WebDriver: " + e.getMessage());
            throw e;
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && driver != null) {
                byte[] screenshot = ((ChromeDriver) driver).getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot on Failure");
            }
        } catch (Exception e) {
            scenario.log("Failed to capture screenshot: " + e.getMessage());
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    scenario.log("Failed to quit WebDriver: " + e.getMessage());
                }
                driver = null;
            }
        }
    }

    @Given("I am on the Amazon homepage")
    public void iAmOnTheAmazonHomepage() {
        try {
            driver.get("https://www.amazon.com");
            // Wait for page load
            wait.until(driver -> ((ChromeDriver) driver)
                .executeScript("return document.readyState").equals("complete"));
            // Additional wait for dynamic content
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                scenario.log("Sleep interrupted while waiting for page load: " + e.getMessage());
            }
        } catch (Exception e) {
            scenario.log("Failed to load Amazon homepage: " + e.getMessage());
            throw e;
        }
    }

    @Then("I should see the Amazon homepage is displayed")
    public void iShouldSeeTheAmazonHomepageIsDisplayed() {
        try {
            // Wait for the page to load and try multiple locators for the Amazon logo
            boolean logoFound = false;
            try {
                wait.until(driver -> driver.findElement(By.id("nav-logo-sprites")).isDisplayed());
                logoFound = true;
            } catch (Exception e1) {
                try {
                    wait.until(driver -> driver.findElement(By.cssSelector("[aria-label='Amazon']")).isDisplayed());
                    logoFound = true;
                } catch (Exception e2) {
                    try {
                        wait.until(driver -> driver.findElement(By.cssSelector("#nav-logo")).isDisplayed());
                        logoFound = true;
                    } catch (Exception e3) {
                        scenario.log("Failed to find Amazon logo using multiple locators");
                        throw e3;
                    }
                }
            }

            Assert.assertTrue("Amazon logo is not displayed", logoFound);
            Assert.assertTrue("Page title does not contain Amazon", 
                driver.getTitle().toLowerCase().contains("amazon"));

            // Additional verification that we're on Amazon
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue("URL does not contain amazon.com", 
                currentUrl.contains("amazon.com"));

        } catch (Exception e) {
            scenario.log("Failed to verify Amazon homepage: " + e.getMessage());
            // Take screenshot on failure
            byte[] screenshot = ((ChromeDriver) driver).getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot on Failure");
            throw e;
        }
    }
} 