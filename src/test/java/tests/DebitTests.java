package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.*;
import pages.*;

import static com.codeborne.selenide.Selenide.open;
import static data.DataHelper.*;
import static data.SQLRequestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebitTests {

    private final String noticeSuccess = "Успешно";
    private final String noticeWrongFormat = "Неверный формат";
    private final String noticeCardExpired = "Истёк срок действия карты";
    private final String noticeRequiredField = "Поле обязательно для заполнения";
    private final String noticeInvalidExpirationDate = "Неверно указан срок действия карты";
    private final int countCardNumber = 16;

    private final int inputNumberIndex = 0;
    private final int inputMouthIndex = 1;
    private final int inputYearIndex = 2;
    private final int inputCvcIndex = 4;

    @BeforeAll
    static void setupAllureReports() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        open(System.getProperty("sut.url"));
    }

    @AfterEach
    public void tearDown() {
        deleteBaseRec();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    //Позитивные сценарии(Валидные значения)
    @Test
    @DisplayName("Успешная оплата тура по активной карте")
    void approvedCard() {
        CardData cardInfo = DataHelper.getApprovedCardData();

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();
        paymentPage.getNotificationMsg(noticeSuccess);

        String dbStatus = getValuePaymentEntity();
        Assertions.assertEquals("APPROVED", dbStatus);
    }

    @Test
    @DisplayName("Отклонение оплаты тура по заблокированной карте")
    void declinedCard() {
        CardData cardInfo = DataHelper.getDeclinedCardData();

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();
        paymentPage.getNotificationMsg("Ошибка");

        String dbStatus = getValuePaymentEntity();
        Assertions.assertEquals("DECLINED", dbStatus);
        //Баг - отображается уведомление об успехе операции
    }

//"Негативные сценарии(Невалидные значения, граничные значения)"

    @Test
    @DisplayName("Ввод номера карты состоящим из нулей")
    void number00Digits() {
        CardData cardInfo = new CardData(getNullCard(), randomMouth(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfNumberInput(noticeWrongFormat);
        //Баг - нет валидации нулевой карты
    }

    @Test
    @DisplayName("Поле номера карты пустое")
    void numberEmpty() {
        CardData cardInfo = new CardData("", randomMouth(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputNumberIndex, "");
        paymentPage.getErrorOfNumberInput(noticeRequiredField);
        // Баг - отображается подсказка неверного формата
    }

    @Test
    @DisplayName("Ввод номера карты на латинице")
    void numberLatin() {
        CardData cardInfo = new CardData(randomHolder(), randomMouth(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputNumberIndex, "");
        paymentPage.getErrorOfNumberInput(noticeWrongFormat);
    }

    @Test
    @DisplayName("Ввод номера карты на кириллице")
    void numberCyrillic() {
        CardData cardInfo = new CardData(randomHolderCyrillicName(), randomMouth(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputNumberIndex, "");
        paymentPage.getErrorOfNumberInput(noticeWrongFormat);
    }

    @Test
    @DisplayName("Ввод номера карты специальными символами")
    void numberSymbols() {
        CardData cardInfo = new CardData(getSymbols(), randomMouth(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputNumberIndex, "");
        paymentPage.getErrorOfNumberInput(noticeWrongFormat);
    }

    @Test
    @DisplayName("Поле номера карты состоящее больше чем 16 символов")
    void number17Digits() {
        CardData cardInfo = new CardData(getApprovedCard() + "1", randomMouth(), randomYear(), randomHolder(), getCardCvc());
        String cardNumber = cardInfo.getNumber().substring(0, cardInfo.getNumber().length() - 1);

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(0);
        paymentPage.checkInputValue(inputNumberIndex, cardNumber);
    }

    @Test
    @DisplayName("Поле номера карты состоящее меньше чем 16 символов")
    void number15Digits() {
        CardData cardInfo = new CardData(randomNumber(countCardNumber - 1), randomMouth(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfNumberInput(noticeWrongFormat);
    }

    @Test
    @DisplayName("Поле \"Месяц\" пустое")
    void mouthEmpty() {
        CardData cardInfo = new CardData(getApprovedCard(), "", randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputMouthIndex, "");
        paymentPage.getErrorOfMouthInput(noticeRequiredField);
        // Баг - отображается подсказка неверного формата
    }

    @Test
    @DisplayName("Ввод данных в поле \"Месяц\" нулевое значение")
    void mouthValue00() {
        CardData cardInfo = new CardData(getApprovedCard(), "00", randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfMouthInput(noticeInvalidExpirationDate);
        // Баг - нет валидации поля на ввод 00
    }

    @Test
    @DisplayName("Поле \"Месяц\" с истёкшим сроком действия карты")
    void mouthExpired() {
        CardData cardInfo = new CardData(getApprovedCard(), expiredMouth(), currentYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfMouthInput(noticeInvalidExpirationDate);
    }

    @Test
    @DisplayName("Ввод данных в поле \"Месяц\" 13 месяца")
    void mouthField13() {
        CardData cardInfo = new CardData(getApprovedCard(), String.valueOf(12 + 1), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfMouthInput(noticeInvalidExpirationDate);
    }

    @Test
    @DisplayName("Ввод данных в поле \"Месяц\" спец символами")
    void mouthSymbols() {
        CardData cardInfo = new CardData(getApprovedCard(), getSymbols(), randomYear(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputMouthIndex, "");
        paymentPage.getErrorOfMouthInput(noticeWrongFormat);
    }

    @Test
    @DisplayName("Ввод данных в поле \"Год\" c нулевым значением")
    void yearField00Year() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), "00", randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfYearInput(noticeCardExpired);
    }

    @Test
    @DisplayName("Ввод данных в поле \"Год\" спец символами")
    void yearSymbols() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), getSymbols(), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputYearIndex, "");
        paymentPage.getErrorOfYearInput(noticeWrongFormat);
    }

    @Test
    @DisplayName("Поле \"Год\" оставить поле пустым")
    void yearEmpty() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), "", randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.checkInputValue(inputYearIndex, "");
        paymentPage.getErrorOfYearInput(noticeRequiredField);
        // Баг - отображается подсказка неверного формата
    }

    @Test
    @DisplayName("Поле \"Год\" с истёкшим сроком действия карты")
    void yearFieldPastYear() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(-1), randomHolder(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfYearInput(noticeCardExpired);
    }

    @Test
    @DisplayName("Поле \"Владелец\" поле пустое")
    void holderEmpty() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), "", getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfHolderInput(noticeRequiredField);
    }

    @Test
    @DisplayName("Ввод данных в поле \"Владелец\" на латинице, без фамилии")
    void holderEmptyLastName() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomFirstName(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfHolderInput(noticeRequiredField);
        // Баг - нет валидации на отсутствие фамилии
    }

    @Test
    @DisplayName("Ввод данных в поле \"Владелец\" на кириллице")
    void holderCyrillic() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomHolderCyrillicName(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfHolderInput(noticeWrongFormat);
        // Баг - нет валидации поля на ввод кириллицы
    }

    @Test
    @DisplayName("Ввод данных в поле \"Владелец\", фамилию на латинице,а имя на кириллице")
    void holderCyrillicLatin() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomFirstCyrillicName() + " " + randomLastName(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfHolderInput(noticeWrongFormat);
        // Баг - нет валидации поля на ввод кириллицы
    }

    @Test
    @DisplayName("Ввод данных в поле \"Владелец\" спец символами")
    void holderSymbols() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), getSymbols(), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfHolderInput(noticeWrongFormat);
        // Баг - нет валидации поля на ввод спец. символов
    }

    @Test
    @DisplayName("Ввод данных в поле \"Владелец\" цифрами")
    void holderDigits() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomNumber(10), getCardCvc());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfHolderInput(noticeWrongFormat);
        // Баг - нет валидации поля на ввод цифр
    }

    @Test
    @DisplayName("Поле \"CVC/CVV\" оставить поле пустым")
    void cvcEmpty() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomHolder(), "");

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfCvcInput(noticeRequiredField);
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }

    @Test
    @DisplayName("Ввод данных в поле \"CVC/CVV\" на кириллице")
    void cvcLettersCyrillic() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomFirstCyrillicName(), randomFirstName());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfCvcInput(noticeRequiredField);
        paymentPage.checkInputValue(inputCvcIndex, "");
    }

    @Test
    @DisplayName("Ввод данных в поле \"CVC/CVV\" на латинице")
    void cvcLettersLatin() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomLastName(), randomFirstName());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfCvcInput(noticeRequiredField);
        paymentPage.checkInputValue(inputCvcIndex, "");
    }

    @Test
    @DisplayName("Ввод данных в поле \"CVC/CVV\" спец символами")
    void cvcSymbols() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomHolder(), getSymbols());

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfCvcInput(noticeRequiredField);
        paymentPage.checkInputValue(inputCvcIndex, "");
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Ввод 4-х цифр (дебетовая)")
    void cvc4Digits() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomHolder(), randomNumber(4));

        String cvc = cardInfo.getCvc().substring(0, cardInfo.getCvc().length() - 1);

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.checkInputValue(inputCvcIndex, cvc);
        paymentPage.getNotificationMsg(noticeSuccess);
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Ввод 2-х цифр (дебетовая)")
    void cvc2Digits() {
        CardData cardInfo = new CardData(getApprovedCard(), randomMouth(), randomYear(), randomHolder(), randomNumber(2));

        BasicPage mainPage = new BasicPage();
        PaymentPage paymentPage = mainPage.payOnDebit();
        paymentPage.insertCardData(cardInfo);
        paymentPage.sendForm();

        paymentPage.isCountErrorsOfValidation(1);
        paymentPage.getErrorOfCvcInput(noticeWrongFormat);
    }
}