package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.config.ConfigHelper;
import net.minecraft.client.gui.screens.Screen;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * @author LatvianModder
 */
public class EMCFormat extends DecimalFormat {
    public static final EMCFormat INSTANCE = new EMCFormat(false);
    public static final EMCFormat INSTANCE_IGNORE_SHIFT = new EMCFormat(true);

    private final boolean ignoreShift;

    private EMCFormat(boolean is) {
        super("#,###");
        setRoundingMode(RoundingMode.DOWN);
        ignoreShift = is;
    }

    public static String formatBigDecimal(BigDecimal d) {
        String s = d.toString();
        if (s.length() >= 25) {
            s = s.substring(0, s.length() - 24) + "Y";
        } else if (s.length() >= 22) {
            s = s.substring(0, s.length() - 21) + "Z";
        } else if (s.length() >= 19) {
            s = s.substring(0, s.length() - 18) + "E";
        } else if (s.length() >= 16) {
            s = s.substring(0, s.length() - 15) + "P";
        } else if (s.length() >= 13) {
            s = s.substring(0, s.length() - 12) + "T";
        } else if (s.length() >= 10) {
            s = s.substring(0, s.length() - 9) + "G";
        } else if (s.length() >= 7) {
            s = s.substring(0, s.length() - 6) + "M";
        }
        return s;
    }

    @Override
    public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
        if (ConfigHelper.client().general.overrideEMCFormatter.get() && number >= 1_000_000D && (ignoreShift || !Screen.hasShiftDown())) {
            double num;
            char c;

            if (number >= 1_000_000_000_000_000_000_000_000D) {
                num = number / 1_000_000_000_000_000_000_000_000D;
                c = 'Y';
            } else if (number >= 1_000_000_000_000_000_000_000D) {
                num = number / 1_000_000_000_000_000_000_000D;
                c = 'Z';
            } else if (number >= 1_000_000_000_000_000_000D) {
                num = number / 1_000_000_000_000_000_000D;
                c = 'E';
            } else if (number >= 1_000_000_000_000_000D) {
                num = number / 1_000_000_000_000_000D;
                c = 'P';
            } else if (number >= 1_000_000_000_000D) {
                num = number / 1_000_000_000_000D;
                c = 'T';
            } else if (number >= 1_000_000_000D) {
                num = number / 1_000_000_000D;
                c = 'G';
            } else {
                num = number / 1_000_000D;
                c = 'M';
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append(String.format("%.02f", num));
            buffer.append(c);
            return buffer;
        }

        return super.format(number, result, fieldPosition);
    }

    @Override
    public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
        if (ConfigHelper.client().general.overrideEMCFormatter.get() && number >= 1_000_000L && (ignoreShift || !Screen.hasShiftDown())) {
            double num;
            char c;

            if (number >= 1_000_000_000_000_000_000L) {
                num = number / 1_000_000_000_000_000_000D;
                c = 'E';
            } else if (number >= 1_000_000_000_000_000L) {
                num = number / 1_000_000_000_000_000D;
                c = 'P';
            } else if (number >= 1_000_000_000_000L) {
                num = number / 1_000_000_000_000D;
                c = 'T';
            } else if (number >= 1_000_000_000L) {
                num = number / 1_000_000_000D;
                c = 'G';
            } else {
                num = number / 1_000_000D;
                c = 'M';
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append(String.format("%.02f", num));
            buffer.append(c);
            return buffer;
        }

        return super.format(number, result, fieldPosition);
    }
}