package pages;

import com.codeborne.selenide.*;
import data.CardData;

import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PaymentPage {

    private final ElementsCollection formInputs = $$(".input__inner");

    private final SelenideElement numberField = formInputs.get(0);
    private final SelenideElement mouthField = formInputs.get(1);
    private final SelenideElement yearField = formInputs.get(2);
    private final SelenideElement holderField = formInputs.get(3);
    private final SelenideElement cvcField = formInputs.get(4);


    private final String sub = ".input__sub";
    private final SelenideElement sendButton = $("form button");

    public PaymentPage(String text) {
        $(By.xpath("//h3[contains(text(), 'карт')]")).shouldBe(visible);
        formInputs.shouldHave(size(5));
        formInputs.get(0).$(".input__top").shouldBe(visible).shouldHave(text("Номер карты"));
        sendButton.shouldBe(visible).shouldHave(text("Продолжить"));
    }

    public void insertCardData(CardData cardData) {
        numberField.$(".input__control").setValue(cardData.getNumber());
        mouthField.$(".input__control").setValue(cardData.getMouth());
        yearField.$(".input__control").setValue(cardData.getYear());
        holderField.$(".input__control").setValue(cardData.getHolder());
        cvcField.$(".input__control").setValue(cardData.getCvc());
    }

    public void sendForm() {
        sendButton.click();
    }

    public void isCountErrorsOfValidation(int size) {
        $$(".input__sub").shouldHave(size(size));
    }

    public void getNotificationMsg(String msg) {
        $(".notification__title").shouldBe(visible, Duration.ofSeconds(20)).shouldHave(text(msg));
    }

    public void checkInputValue(int index, String text) {
        formInputs.get(index).$(".input__control").shouldHave(value(text));
    }

    public void getErrorOfNumberInput(String text) {
        numberField.$(".input__sub").shouldBe(visible).shouldHave(text(text));
    }

    public void getErrorOfMouthInput(String text) {
        mouthField.$(".input__sub").shouldBe(visible).shouldHave(text(text));
    }

    public void getErrorOfYearInput(String text) {
        yearField.$(".input__sub").shouldBe(visible).shouldHave(text(text));
    }

    public void getErrorOfHolderInput(String text) {
        holderField.$(".input__sub").shouldBe(visible).shouldHave(text(text));
    }

    public void getErrorOfCvcInput(String text) {
        cvcField.$(".input__sub").shouldBe(visible).shouldHave(text(text));
    }
}