package stepdefinitions;

import java.time.Duration;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import utils.TestUtils;

public class AmazonSteps {
    private WebDriver driver;
    private WebDriverWait wait;
    private Scenario scenario;

    @Before
    public void setup(Scenario scenario) {
        this.scenario = scenario;
        
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // Initialize ChromeDriver with options
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            // Take screenshot if scenario fails
            String screenshotName = scenario.getName().replaceAll(" ", "_");
            String screenshotPath = TestUtils.captureScreenshot(driver, screenshotName);
            scenario.attach(screenshotPath, "image/png", "Screenshot on Failure");
        }
        
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am on the Amazon homepage")
    public void iAmOnTheAmazonHomepage() {
        driver.get("https://www.amazon.com");
        // Wait for the search box to be present to confirm page load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("twotabsearchtextbox")));
    }

    @When("I search for {string}")
    public void iSearchFor(String searchTerm) {
        try {
            // Wait for search box and ensure it's clickable
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("twotabsearchtextbox")));
            searchBox.clear();
            searchBox.sendKeys(searchTerm);
            
            // Wait for and click the search button
            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-search-submit-button")));
            searchButton.click();
            
            // Wait for results to start loading
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-component-type='s-search-result']")));
        } catch (Exception e) {
            scenario.log("Failed to perform search: " + e.getMessage());
            throw e;
        }
    }

    @Then("I should see search results")
    public void iShouldSeeSearchResults() {
        // Wait for search results with a longer timeout
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
            By.cssSelector("[data-component-type='s-search-result']"), 0));
        
        Assert.assertTrue("No search results found", 
            driver.findElements(By.cssSelector("[data-component-type='s-search-result']")).size() > 0);
    }

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String text) {
        // Wait for title to update
        wait.until(driver -> driver.getTitle().toLowerCase().contains(text.toLowerCase()));
        Assert.assertTrue("Title does not contain expected text: " + text,
            driver.getTitle().toLowerCase().contains(text.toLowerCase()));
    }

    @When("I click on the first product")
    public void iClickOnTheFirstProduct() {
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-component-type='s-search-result'] h2 a")));
        firstProduct.click();
    }

    @When("I add the product to cart")
    public void iAddTheProductToCart() {
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
        addToCartButton.click();
    }

    @Then("the cart count should be {string}")
    public void theCartCountShouldBe(String count) {
        WebElement cartCount = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nav-cart-count")));
        Assert.assertEquals("Cart count does not match", count, cartCount.getText());
    }

    @When("I hover over the {string} menu")
    public void iHoverOverTheMenu(String menuText) {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu")));
        Actions actions = new Actions(driver);
        actions.moveToElement(menu).perform();
    }

    @Then("I should see the main navigation categories")
    public void iShouldSeeTheMainNavigationCategories() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu"))).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("hmenu-item")));
        Assert.assertTrue("Navigation categories not displayed",
            driver.findElements(By.className("hmenu-item")).size() > 0);
    }
} 