package locale;

import panels.MainPanel;
import util.ContextManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.*;

public class JCommander {

    public static JFrame frame;

    private static JMenuBar createMenuBar() {
        Context context = ContextManager.getContext();
        JMenuBar menuBar = new JMenuBar();
        String languages[] = { "EN", "PL" };

        JMenu langs = new JMenu(context.getBundle().getString("languages"));

        for(String lang : languages) {
            JMenuItem langItem = new JMenuItem(context.getBundle().getString(lang));
            langs.add(langItem);
            langItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Setting locale: " + lang);
                    context.setLocale(new Locale(lang));
                }
            });
        }

        context.addContextChangeListener(new ContextChangeListener() {
            @Override
            public void contextChanged() {
                langs.setText(context.getBundle().getString("languages"));
                for(int i = 0; i < languages.length; i++) {
                    langs.getItem(i).setText(context.getBundle().getString(languages[i]));
                }
            }
        });




        menuBar.add(langs);

        return menuBar;
    }


    private static void createAndShowGui() {
      //  Context context = new Context("Languages");
      //  context.setLocale(new Locale("EN"));
        frame = new JFrame("JCommander");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setDefaultLookAndFeelDecorated(true);

        frame.setJMenuBar(createMenuBar());

        MainPanel mainPanel = new MainPanel();

        frame.getContentPane().add(mainPanel.getPanel());

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });

        //JFrame.setDefaultLookAndFeelDecorated(true);
        /*final JLabel label1 = new JLabel(context.getBundle().getString("userName"));
        final JLabel label2 = new JLabel(context.getBundle().getString("password"));
        final JLabel label3 = new JLabel(context.getBundle().getString("login"));
        final JButton button = new JButton(context.getBundle().getString("lang"));
        context.addContextChangeListener(new ContextChangeListener() {
            @Override
            public void contextChanged() {
                label1.setText(context.getBundle().getString("userName"));
                label2.setText(context.getBundle().getString("password"));
                label3.setText(context.getBundle().getString("login"));
                button.setText(context.getBundle().getString("lang"));
            }
        });
        button.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (context.getLocale().getLanguage().equalsIgnoreCase("EN")) {
                    context.setLocale(new Locale("PL"));
                }
                else {
                    context.setLocale(new Locale("EN"));
                }
            }
        });

        frame.add(label1);
        frame.add(new JTextField());
        frame.add(label2);
        frame.add(new JPasswordField());
        frame.add(label3);
        frame.add(button); */
    }
}
