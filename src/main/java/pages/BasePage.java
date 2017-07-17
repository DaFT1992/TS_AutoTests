package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import utils.FileIO;
import utils.ReporterManager;

/**
 * Created by odiachuk on 07.07.17.
 */
public class BasePage {


    static ReporterManager reporter = ReporterManager.Instance;

    public ThreadLocal<String> URL = new ThreadLocal<String>();
    protected String pageTitle;
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    static final int DEFAULT_TIMEOUT = getTimeout();

    private static int getTimeout() {
        String timeout = FileIO.getConfigProperty("DefaultTimeoutInSeconds");
        if (timeout == null ) {
            reporter.fatalFail("DefaultTimeoutInSeconds parameter was not found");
            timeout = "15";
        };

        return Integer.parseInt(timeout);
    }

    public BasePage() {
       // waitForPageToLoad();
    }

    public static WebDriver driver(){
        return driver.get();
    }


    public BasePage(String pageTitle) {
        //this();
        this.pageTitle = pageTitle;
    }

    public boolean isPageLoaded() {
        reporter.info("Page title is: " + driver().getTitle());
        return (driver().getTitle().contains(pageTitle));
    }

    public void reloadPage() {
        driver().navigate().refresh();
    }

    public void open() {
        //TODO add multiple URLs support
        /*if ( URL.get() == null)
            URL.set(FileIO.getConfigProperty("Environment"));
        else
            URL.set(FileIO.getConfigProperty("Environment") + URL.get() );
            */
        URL.set(FileIO.getConfigProperty("Environment"));
        reporter.info("Opening the page: " + "\"" + URL.get() + "\"");
        driver().get(URL.get());
        driver().manage().window().maximize();
    }

    public void close() {
        reporter.info("Closing the browser");
        driver().close();
    }

    public String getTitle() {
        reporter.info("The page title is: " + "\"" + pageTitle + "\"");
        return pageTitle;
    }

    public String getURL() {
        reporter.info("The requested URL is: " + URL.get());
        return URL.get();
    }

    protected void sendText(String cssSelector, String text) {
        findElement(By.cssSelector(cssSelector)).sendKeys(text);
    }

    public boolean isTextPresent(String text) {
        return driver().getPageSource().contains(text);
    }

    public boolean isElementPresent(By by) {
        try {
            WebElement element = findElement(by);
            return element.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isElementPresent(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isElementPresent(String _cssSelector) {
        try {
            findElement(By.cssSelector(_cssSelector));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isElementPresentAndDisplay(By by) {
        try {
            return findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getWebElement(By by) {
        return findElement(by);
    }


    public static void selectFromDropdown(By element, String value){
        Select dropdown = new Select(findElement(element));
        dropdown.selectByVisibleText(value);
    }


    public static void clickOnElementIgnoreException(By element, int... timeout) {
        waitForPageToLoad();
        try {
            clickOnElement(element, timeout[0]);
        } catch (RuntimeException e) {
            reporter.info("Got exception. Exception is expected and ignored.");
        }
    }

    public static WebElement findElementIgnoreException(By element, int... timeout) {
        waitForPageToLoad();
        try {
            return findElement(element, timeout[0]);
        } catch (RuntimeException e) {
            reporter.info("Got exception. Exception is expected and ignored.");
        }
        return null;
    }

    public static List<WebElement> findElementsIgnoreException(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElements(element);
        } catch (Exception e) {
            reporter.info("Got exception. Exception is expected and ignored.");
            return new ArrayList<WebElement>();
        }
    }

    public static void clickOnElement(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            driver().findElement(element).click();
        } catch (Exception e) {
            throw new RuntimeException("Failure clicking on element");
        }
        waitForPageToLoad();
    }

    public static WebElement findElement(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            return driver().findElement(element);
        } catch (Exception e) {
            throw new RuntimeException("Failure finding element");
        }
    }

    public static List<WebElement> findElements(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElements(element);
        } catch (Exception e) {
            throw new RuntimeException("Failure finding elements");
        }
    }

    public static String getAttributeIDIgnoreExecption(By element, int... timeout) {
        waitForPageToLoad();
        try {
            return getAttributeID(element, timeout[0]);
        } catch (RuntimeException e) {
            reporter.info("Got exception. Exception is expected and ignored.");
        }
        return null;
    }

    public static String getAttributeID(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            String id = findElement(element).getAttribute("id");
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Failure getting attribute id of an element");
        }
    }

    public static void setDriverContextToPage(WebDriver driver) {
        reporter.info("Setting the context mode to Page");
        driver.switchTo().defaultContent();
    }

    public static void scrollToElement(WebElement element) {
        waitForPageToLoad();
        ((JavascriptExecutor) driver()).executeScript("arguments[0].focus(); window.scroll(0, window.scrollY+=200)",element);

    }

    public static void waitForPageToLoad(){

        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver driver)
            {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState")
                        .equals("complete");
            }

        };

        Wait<WebDriver> wait = new WebDriverWait(driver(), DEFAULT_TIMEOUT);

        try
        {
            wait.until(expectation);
        } catch (Exception error)
        {
            reporter.fail("JavaScript readyState query timeout - The page has not finished loading");
        }
    }

    public static void sleepFor(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    static void waitForAlert(WebDriver driver, int timeout) {
        int i = 0;
        while (i++ < timeout) {
            try {
                Alert alert = driver.switchTo().alert();
                break;
            } catch (NoAlertPresentException e)  // чекаємо секунду
            {
                sleepFor(1);
                continue;
            }
        }
    }

    /*
    static void waitForElement(WebDriver driver, By by, int timeout){
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
    } */
}
