package project.baonq.util;

import java.text.DecimalFormat;
import project.baonq.enumeration.Currency;
public class ConvertUtil {

    public static String convertCashFormat(double number) {
        //format number
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");
        return decimalFormat.format(number);
    }

    public static String convertCurrency(String currency) {
        switch (currency) {
            case "VND":
                return "đ";
            case "Dollar":
                return "$";
            case "Euro":
                return "€";
            default:
                return "";
        }
    }
}
