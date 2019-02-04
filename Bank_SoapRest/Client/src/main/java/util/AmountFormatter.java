package util;

import java.math.BigInteger;

/**
 * Created by macie on 19/01/2018.
 */
public class AmountFormatter {
    public static String formatValue(BigInteger amount) {
        return '$' + String.format("%.2f", amount.intValue() / 100.f);
    }
}
