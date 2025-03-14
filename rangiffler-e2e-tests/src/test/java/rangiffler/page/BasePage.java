package rangiffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import io.qameta.allure.Step;
import rangiffler.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    private final SelenideElement alert = $(".MuiSnackbar-root");
    private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

    public abstract T checkThatPageLoaded();


}
