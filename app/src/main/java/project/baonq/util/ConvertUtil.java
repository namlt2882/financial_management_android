package project.baonq.util;

import java.text.DecimalFormat;

public class ConvertUtil {

    public static String convertCashFormat(double number) {
        //format number
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");
        return decimalFormat.format(number);
    }

    public static String convertCurrency(String currency) {
        switch (currency) {
            case "VNĐ":
                return "đ";
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
