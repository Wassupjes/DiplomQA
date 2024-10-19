package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.CardData;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static data.APIHelper.*;
import static data.DataHelper.*;
import static data.SQLRequestHelper.*;

public class ApiPaymentTourTests {
    private final String approvedStatus = "APPROVED";
    private final String declinedStatus = "DECLINED";

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        deleteBaseRec();
    }

    @AfterEach
    public void tearDown() {
        deleteBaseRec();
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “APPROVED”")
    public void successfulPayFromApprovedDebitCard() {
        CardData card = getApprovedCardData();

        String requestStatus = requestToDebitCard(card);
        String dbStatus = getValuePaymentEntity();

        Assertions.assertEquals(approvedStatus, requestStatus);
        Assertions.assertEquals(approvedStatus, dbStatus);
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по кредитной карте со статусом “APPROVED”")
    public void successfulPayFromApprovedCreditCard() {
        CardData card = getApprovedCardData();

        String requestStatus = requestToCreditCard(card);
        String dbStatus = getValueCreditRequestEntity();

        Assertions.assertEquals(approvedStatus, requestStatus);
        Assertions.assertEquals(approvedStatus, dbStatus);
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “DECLINED”")
    public void failedPayFromApprovedDebitCard() {
        CardData card = getDeclinedCardData();

        String requestStatus = requestToDebitCard(card);
        String dbStatus = getValuePaymentEntity();

        Assertions.assertEquals(declinedStatus, requestStatus);
        Assertions.assertEquals(declinedStatus, dbStatus);
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по кредитной карте со статусом “DECLINED”")
    public void failedPayFromApprovedCreditCard() {
        CardData card = getDeclinedCardData();

        String requestStatus = requestToCreditCard(card);
        String dbStatus = getValueCreditRequestEntity();

        Assertions.assertEquals(declinedStatus, requestStatus);
        Assertions.assertEquals(declinedStatus, dbStatus);
    }
}