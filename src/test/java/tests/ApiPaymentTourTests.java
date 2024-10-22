package tests;

import data.CardData;
import org.junit.jupiter.api.*;

import static data.APIHelper.*;
import static data.DataHelper.*;
import static data.SQLRequestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApiPaymentTourTests {
    private final String approvedStatus = "APPROVED";
    private final String declinedStatus = "DECLINED";

    private final String endpointDebitCard = "/api/v1/pay";
    private final String endpointCreditCard = "/api/v1/credit";

    @BeforeAll
    static void setupAll() {
        deleteBaseRec();
    }

    @AfterEach
    public void tearDown() {
        deleteBaseRec();
    }

    @AfterAll
    public static void tearDownAll() {
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “APPROVED”")
    public void successfulPayFromApprovedDebitCard() {
        CardData card = getApprovedCardData();

        String requestStatus = requestToCard(card, endpointDebitCard);
        String dbStatus = getValuePaymentEntity();

        assertAll(() -> assertEquals(approvedStatus, requestStatus),
                () -> assertEquals(approvedStatus, dbStatus));
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по кредитной карте со статусом “APPROVED”")
    public void successfulPayFromApprovedCreditCard() {
        CardData card = getApprovedCardData();

        String requestStatus = requestToCard(card, endpointCreditCard);
        String dbStatus = getValueCreditRequestEntity();

        assertAll(() -> assertEquals(approvedStatus, requestStatus),
                () -> assertEquals(approvedStatus, dbStatus));
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “DECLINED”")
    public void failedPayFromApprovedDebitCard() {
        CardData card = getDeclinedCardData();

        String requestStatus = requestToCard(card, endpointDebitCard);
        String dbStatus = getValuePaymentEntity();

        assertAll(() -> assertEquals(declinedStatus, requestStatus),
                () -> assertEquals(declinedStatus, dbStatus));
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по кредитной карте со статусом “DECLINED”")
    public void failedPayFromApprovedCreditCard() {
        CardData card = getDeclinedCardData();

        String requestStatus = requestToCard(card, endpointCreditCard);
        String dbStatus = getValueCreditRequestEntity();

        assertAll(() -> assertEquals(declinedStatus, requestStatus),
                () -> assertEquals(declinedStatus, dbStatus));
    }
}