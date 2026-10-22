package util;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern MOBILE = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PAN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern IFSC = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private static final Pattern ACCOUNT_NO = Pattern.compile("^AC[0-9]{4,}$");
    private static final Pattern AMOUNT = Pattern.compile("^[1-9][0-9]*$");

    public static boolean isValidMobile(String s) { return s != null && MOBILE.matcher(s).matches(); }
    public static boolean isValidEmail(String s) { return s != null && EMAIL.matcher(s).matches(); }
    public static boolean isValidPan(String s) { return s != null && PAN.matcher(s).matches(); }
    public static boolean isValidIfsc(String s) { return s != null && IFSC.matcher(s).matches(); }
    /** Accepts only positive whole numbers. */
    public static boolean isValidAmount(String s) { return s != null && AMOUNT.matcher(s).matches(); }

    public static boolean isValidAccountNumber(String s) { return s != null && ACCOUNT_NO.matcher(s).matches(); }
}
