package pageObjects;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class GooglePage extends BasePage {

    private String url;

    private WebDriver driver;

    @FindBy(id = "lst-ib")
    private WebElement searchTextBox;
    @FindBy(name = "btnK")
    private WebElement googleSearchBtn;
    @FindBy(id = "resultStats")
    private WebElement resultCount;

    @FindBys(@FindBy(css = "div .sbqs_c"))
    private List<WebElement> suggestions;

    private String searchQuery = "";

    public String getSearchQuery() {
        return searchQuery;
    }

    private void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public GooglePage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.url = "https://www.google.com";
    }

    @Override
    public boolean isAt() {
        return false;
    }

    public void goTo() {
        this.driver.get(this.url);
    }

    public void enterSearchQuery(String text) {
        setSearchQuery(text);
        WebDriverWait wait = new WebDriverWait(this.driver, 30);
        wait.until(ExpectedConditions.elementToBeClickable(searchTextBox)).clear();
        searchTextBox.sendKeys(getSearchQuery());
    }

    public void clickGoogleSearchBtn() {
        searchTextBox.submit();
    }

    public boolean verifySuggestionExist(String suggestionText) {

        Wait wait = new FluentWait(this.driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);

        wait.until(ExpectedConditions.elementToBeClickable(suggestions.get(0)));

        suggestions.stream()
                .forEach(suggest -> System.out.println("suggestion is " + suggest.getText()));
        long suggestCount = suggestions.stream()
                .filter(suggest -> suggest.getText().contains(suggestionText))
                .count();

        if (suggestCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}