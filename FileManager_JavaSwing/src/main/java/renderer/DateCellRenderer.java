package renderer;

import util.ContextManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by macie on 26/04/2017.
 */
public class DateCellRenderer  extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable jtab, Object v, boolean selected, boolean focus, int r, int c){
        JLabel rendComp = (JLabel) super.getTableCellRendererComponent(jtab, v, selected, focus, r, c);

        String pattern;
        Locale locale = ContextManager.getContext().getLocale();
        if(locale.equals(new Locale("EN"))) {
            pattern = "yyyy-MM-dd HH:mm";
        } else {
            pattern = "dd/MM/yyyy HH:mm";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        rendComp.setText(formatter.format(v));
        setBorder(noFocusBorder);
        return rendComp;
    }
}
