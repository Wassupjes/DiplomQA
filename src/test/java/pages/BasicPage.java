package pages;

import com.codeborne.selenide.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class BasicPage {

    private final ElementsCollection buttons = $$("button");

    private final SelenideElement buttonPayOnDebit = buttons.get(0);
    private final SelenideElement buttonPayOnCredit = buttons.get(1);

    public BasicPage() {
        $("h2").shouldBe(visible);
        buttonPayOnDebit.shouldBe(visible).shouldHave(text("Купить"));
        buttonPayOnCredit.shouldBe(visible).shouldHave(text("Купить в кредит"));
    }

    public PaymentPage payOnDebit() {
        buttonPayOnDebit.click();
        return new PaymentPage("Оплата по карте");
    }

    public PaymentPage payOnCredit() {
        buttonPayOnCredit.click();
        return new PaymentPage("Кредит по данным карты");
    }
}