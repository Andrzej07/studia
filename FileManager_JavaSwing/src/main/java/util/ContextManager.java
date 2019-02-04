package util;

import locale.Context;

import java.util.Locale;

/**
 * Created by macie on 26/04/2017.
 */
public class ContextManager {
    private static Context context;

    public static Context getContext() {
        if(context == null) {
            context = new Context("GUILabels");
            context.setLocale(new Locale("EN"));
        }
        return context;
    }

    public static void setLocale(Locale l) {
        context.setLocale(l);
    }
}
