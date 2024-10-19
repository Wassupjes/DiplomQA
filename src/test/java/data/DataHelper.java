package data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataHelper {

    private static final String approvedCard = "4444 4444 4444 4441";
    private static final String declinedCard = "4444 4444 4444 4442";
    private static final String nullCard = "0000 0000 0000 0000";
    private static final Faker fakerRU = new Faker(new Locale("ru"));
    private static final Faker fakerEN = new Faker(new Locale("en"));
    private static final String symbols = " *?/\\|<>,.()[]{};:'\"!@#$%^&";
    private static final int yearShift = 3;

    private DataHelper() {
    }

    public static String getSymbols() {
        return symbols;
    }

    public static String randomNumber(int num) {
        return fakerEN.numerify("#".repeat(num));
    }

    public static String randomMouth() {
        int shift = new Random().nextInt(12) + 1;
        return LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String expiredMouth() {
        return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String currentYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String randomYear(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String randomYear() {
        return LocalDate.now().plusYears(yearShift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String randomHolder() {
        return randomFirstName() + " " + randomLastName();
    }

    public static String randomFirstName() {
        return fakerEN.name().firstName();
    }

    public static String randomLastName() {
        return fakerEN.name().lastName();
    }

    public static String randomFirstCyrillicName() {
        return fakerRU.name().firstName();
    }

    public static String randomHolderCyrillicName() {
        return fakerRU.name().firstName() + " " + fakerRU.name().lastName();
    }

    public static String getCardCvc() {
        return randomNumber(3);
    }

    public static data.CardData getApprovedCardData() {
        return new data.CardData(approvedCard, randomMouth(), randomYear(yearShift), randomHolder(), getCardCvc());
    }

    public static data.CardData getDeclinedCardData() {
        return new data.CardData(declinedCard, randomMouth(), randomYear(yearShift), randomHolder(), getCardCvc());
    }

    public static String getNullCard() {
        return nullCard;
    }

    public static String getApprovedCard() {
        return approvedCard;
    }

    public static String removeSpace(String str) {
        return str.replace(" ", "");
    }
}