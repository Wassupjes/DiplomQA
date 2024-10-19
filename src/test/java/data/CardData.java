package data;

public class CardData {

    String number;
    String mouth;
    String year;
    String holder;
    String cvc;

    public CardData(String number, String mouth, String year, String holder, String cvc) {
        this.number = number;
        this.mouth = mouth;
        this.year = year;
        this.holder = holder;
        this.cvc = cvc;
    }

    public String getNumber() {
        return number;
    }

    public String getMouth() {
        return mouth;
    }

    public String getYear() {
        return year;
    }

    public String getHolder() {
        return holder;
    }

    public String getCvc() {
        return cvc;
    }
}