package steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.testng.Assert;
import pageObjects.GooglePage;

public class GoogleStepDef {

    private GooglePage google;

    public GoogleStepDef(GooglePage google){
        this.google = google;
    }

    @Given("^I go to google$")
    public void iGoToGoogle() throws Throwable {
        google.goTo();
    }

    @When("^I query for \"([^\"]*)\"$")
    public void iQueryFor(String query) throws Throwable {
        google.enterSearchQuery(query);
    }

    @And("^click search$")
    public void clickSearch() throws Throwable {
        google.clickGoogleSearchBtn();
    }

    @Then("^google page title should become \"([^\"]*)\"$")
    public void googlePageTitleShouldBecome(String pageTitle) throws Throwable {
        Assert.assertEquals(google.getSearchQuery() +" - Google Search", pageTitle);
    }

    @Then("^i should see \"([^\"]*)\" as 1 of the suggested search$")
    public void iShouldSeeAsOfTheSuggestedSearch(String suggestion) throws Throwable {
        Assert.assertTrue(google.verifySuggestionExist(suggestion));
    }
}