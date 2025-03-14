package rangiffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
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


  @Step("Открыть страницу People")
  public PeoplePage open() {
    Selenide.open("/people");
    return this;
  }

  @Step("Открыть таб Friends")
  public PeoplePage openFriendsTab() {
    friendsTab.click();
    return this;
  }

  @Step("Открыть таб IncomeInvitations")
  public PeoplePage openIncomeInvitationsTab() {
    incomeInvitationsTab.click();
    return this;
  }

  @Step("Проверить видимость кнопки Добавить в друзья")
  public PeoplePage shouldAddFriendButton() {
    addFriendButton.shouldBe(visible);
    return this;
  }

  @Step("Проверить видимость кнопки Удалить из друзей")
  public PeoplePage shouldRemoveFriendButton() {
    removeFriendButton.shouldBe(visible);
    return this;
  }

  @Step("Проверить отображения статуса Waiting")
  public PeoplePage shouldWaitingStatus() {
    waitingStatus.shouldBe(visible);
    return this;
  }

  @Step("Открыть таб OutcomeInvitations")
  public PeoplePage openOutcomeInvitationsTab() {
    outcomeInvitationsTab.click();
    return this;
  }

  @Step("Открыть таб AllPeople")
  public PeoplePage openAllPeopleTab() {
    allPeopleTab.click();
    return this;
  }

  @Step("Проверить видимость сообщения заглушки")
  public void noUserYetMessageShouldBePresented() {
    noUserYetMessage.shouldBe(visible);
  }

  @Step("Проверить представление пользователей на странице")
  public PeoplePage exactlyUsersShouldBePresentedInTable(TestUser... users) {
    table.exactlyUsersShouldBePresentedInTable(users);
    return this;
  }


  @Step("Проверить представление пользователей на странице")
  public PeoplePage usersShouldBePresentedInTable(TestUser... users) {
    table.usersShouldBePresentedInTable(users);
    return this;
  }

  @Step("Проверить представление пользователей на странице")
  public PeoplePage usersCountShouldBeEqualTo(int expectedCount) {
    table.usersCountShouldBeEqualTo(expectedCount);
    return this;
  }

  @Step("Ввести в поиск значение: {0}")
  public PeoplePage search(String searchQuery) {
    searchInput.setValue(searchQuery).pressEnter();
    return this;
  }

  @Step("Добавить друга")
  public PeoplePage addFriend(String username) {
    table.addFriend(username);
    return this;
  }

  @Step("Принять приглашение")
  public PeoplePage acceptInvitation(String username) {
    table.acceptInvitation(username);
    return this;
  }

  @Step("Отклонить приглашение")
  public PeoplePage rejectInvitation(String username) {
    table.rejectInvitation(username);
    return this;
  }

  @Step("Удалить друга")
  public PeoplePage deleteFriend(String username) {
    table.deleteFriend(username);
    return this;
  }

  @Override
  @Step("Проверить загрузку страницы")
  public PeoplePage checkThatPageLoaded() {
    return null;
  }
}
