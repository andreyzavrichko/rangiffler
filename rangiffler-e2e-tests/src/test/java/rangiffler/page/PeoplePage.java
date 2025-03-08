package rangiffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.component.PeopleTable;


import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class PeoplePage extends BasePage<PeoplePage> {

  private final SelenideElement friendsTab = $x("//button[text()='Friends']");
  private final SelenideElement incomeInvitationsTab = $x("//button[text()='Income invitations']");
  private final SelenideElement outcomeInvitationsTab = $x("//button[text()='Outcome invitations']");
  private final SelenideElement allPeopleTab = $x("//button[text()='All People']");
  private final SelenideElement searchInput = $x("//input[@placeholder='Search people']");
  private final SelenideElement noUserYetMessage = $x("//p[text()='There are no users yet']");
  private final SelenideElement removeFriendButton = $x("//button[text()='Remove']");
  private final SelenideElement addFriendButton = $x("//button[text()='Add']");
  private final SelenideElement waitingStatus = $x("//span[text()='Waiting...']");
  private final PeopleTable table = new PeopleTable($("//table"));

  public PeoplePage open() {
    Selenide.open("/people");
    return this;
  }

  public PeoplePage openFriendsTab() {
    friendsTab.click();
    return this;
  }

  public PeoplePage openIncomeInvitationsTab() {
    incomeInvitationsTab.click();
    return this;
  }

  public PeoplePage clickRemoveFriendButton() {
    removeFriendButton.click();
    return this;
  }

  public PeoplePage clickAddFriendButton() {
    addFriendButton.click();
    return this;
  }


  public PeoplePage shouldAddFriendButton() {
    addFriendButton.shouldBe(visible);
    return this;
  }

  public PeoplePage shouldRemoveFriendButton() {
    removeFriendButton.shouldBe(visible);
    return this;
  }

  public PeoplePage shouldWaitingStatus() {
    waitingStatus.shouldBe(visible);
    return this;
  }



  public PeoplePage openOutcomeInvitationsTab() {
    outcomeInvitationsTab.click();
    return this;
  }

  public PeoplePage openAllPeopleTab() {
    allPeopleTab.click();
    return this;
  }

  public void noUserYetMessageShouldBePresented() {
    noUserYetMessage.shouldBe(visible);
  }

  public PeoplePage exactlyUsersShouldBePresentedInTable(TestUser... users) {
    table.exactlyUsersShouldBePresentedInTable(users);
    return this;
  }


  public PeoplePage usersShouldBePresentedInTable(TestUser... users) {
    table.usersShouldBePresentedInTable(users);
    return this;
  }

  public PeoplePage usersCountShouldBeEqualTo(int expectedCount) {
    table.usersCountShouldBeEqualTo(expectedCount);
    return this;
  }

  public PeoplePage search(String searchQuery) {
    searchInput.setValue(searchQuery).pressEnter();
    return this;
  }

  public PeoplePage addFriend(String username) {
    table.addFriend(username);
    return this;
  }

  public PeoplePage acceptInvitation(String username) {
    table.acceptInvitation(username);
    return this;
  }

  public PeoplePage rejectInvitation(String username) {
    table.rejectInvitation(username);
    return this;
  }

  public PeoplePage deleteFriend(String username) {
    table.deleteFriend(username);
    return this;
  }

  @Override
  public PeoplePage checkThatPageLoaded() {
    return null;
  }
}
