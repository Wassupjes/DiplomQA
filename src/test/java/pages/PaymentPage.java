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

    public int getCountErrorsOfValidation() {
        return $$(".input__sub").size();
    }

    public void getNotificationMsg(String msg) {
        $(".notification__title").shouldBe(visible, Duration.ofSeconds(20)).shouldHave(text(msg));
    }

    public String getInputStr(int index) {
        return formInputs.get(index).$(".input__control").getValue();
    }

    public String getErrorOfNumberInput() {
        return numberField.$(".input__sub").shouldBe(visible).getText();
    }

    public String getErrorOfMouthInput() {
        return mouthField.$(".input__sub").shouldBe(visible).getText();
    }

    public String getErrorOfYearInput() {
        return yearField.$(".input__sub").shouldBe(visible).getText();
    }

    public String getErrorOfHolderInput() {
        return holderField.$(".input__sub").shouldBe(visible).getText();
    }

    public String getErrorOfCvcInput() {
        return cvcField.$(".input__sub").shouldBe(visible).getText();
    }
}