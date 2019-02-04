package panels;

import component.FilesTable;
import component.FilesTableModel;
import filter.HiddenFilesFilter;
import util.ActionsManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by macie on 25/04/2017.
 */
public class ViewerPanel {
    private List<File> files;
    private File currentDir;
    private JTable filesTable;
    private JComboBox rootsComboBox;
    private JTextField locationTextField;
    private JButton goUpButton;


    JPanel createViewerPanel() {
        final JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());

        final JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BorderLayout(10, 0));

        ActionsManager.register(this);

        rootsComboBox = new JComboBox<>(getRoots());
        currentDir = (File)rootsComboBox.getSelectedItem();
        locationTextField = new JTextField();
        locationTextField.setEditable(false);
        goUpButton = new BasicArrowButton(BasicArrowButton.NORTH);
        goUpButton.setEnabled(false);
        locationPanel.add(rootsComboBox, BorderLayout.LINE_START);
        locationPanel.add(locationTextField, BorderLayout.CENTER);
        locationPanel.add(goUpButton, BorderLayout.LINE_END);

        viewerPanel.add(locationPanel, BorderLayout.PAGE_START);
        viewerPanel.add(new JScrollPane(createTable()), BorderLayout.CENTER);

        setupEvents();
        onCurrentDirChange();
       // viewerPanel.add(new JScrollPane(createTable()), BorderLayout.CENTER);

        //filesTable.getModel().setValueAt("test", 6, 2);

        return viewerPanel;
    }


    private JTable createTable() {
        //files = Arrays.asList(currentDir.listFiles(new HiddenFilesFilter()));
        filesTable = new FilesTable(new ArrayList<File>());
        return filesTable;
    }

    private void setupEvents() {
        rootsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    File f = (File) e.getItem();
                    currentDir = f;
                    onCurrentDirChange();
                }
            }
        });
        goUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDir = currentDir.getParentFile();
                onCurrentDirChange();
            }
        });
        filesTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if(me.getClickCount() == 2) {
                    System.out.println("Double click fired at row: " + row);
                    File f = (File) table.getValueAt(row, 0);
                    if(f.isDirectory()) {
                        System.out.println("Changing dir");
                        currentDir = f;
                        onCurrentDirChange();
                    }
                }
            }
        });
        ViewerPanel thisPanel = this;
        filesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ActionsManager.handleFocusChanged(thisPanel, e);
            }
        });

    }

    private File[] getRoots() {
        java.util.List<File> roots = new LinkedList<>(Arrays.asList(File.listRoots()));
        for(int i = roots.size()-1; i >= 0; i--) {
            if(roots.get(i).toString().startsWith("A:")) {
                roots.remove(0);
                break;
            }
        }
        return roots.stream().toArray(File[]::new);
    }

    public void onCurrentDirChange() {
        locationTextField.setText(currentDir.toString());
        FilesTableModel ftm = (FilesTableModel) filesTable.getModel();
        ftm.changeList(Arrays.asList(currentDir.listFiles(new HiddenFilesFilter())));
        ftm.fireTableDataChanged();
        goUpButton.setEnabled(currentDir.getParent() != null);
    }

    public void clearFocus() {
        filesTable.getSelectionModel().clearSelection();
    }

    public File getCurrentDirectory() {
        return currentDir;
    }

    public List<File> getSelectedFiles() {
        List<File> result = new LinkedList<>();
        int[] indices = filesTable.getSelectedRows();
        for(int index : indices) {
            result.add((File) filesTable.getValueAt(index,0));
        }
        return result;
    }



}
