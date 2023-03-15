package Crypto.Tracker;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;


public class CryptoTrackerApp extends JFrame {
    private JList<String> cryptoList;
    private JTextField searchField;
    private JLabel nameLabel;
    private JLabel symbolLabel;
    private JList<String> selectedCryptoList;

    private HashMap<String, String[]> cryptocurrencies;

    public CryptoTrackerApp() {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("Crypto Tracker");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Get cryptocurrency data from API
        CoingeckoAPI api = new CoingeckoAPI();
        try {
            api.makeApiRequest();
            cryptocurrencies = api.getCryptoList();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }


        cryptoList = new JList<>(cryptocurrencies.keySet().toArray(new String[cryptocurrencies.size()]));
        JScrollPane cryptoScrollPane = new JScrollPane(cryptoList);

        selectedCryptoList = new JList<>();
        JScrollPane selectedCryptoScrollPane = new JScrollPane(selectedCryptoList);

        nameLabel = new JLabel();
        symbolLabel = new JLabel();
        searchField = new JTextField();
        searchField = new JTextField("Type in here");
        searchField.setForeground(Color.GRAY);
        searchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (searchField.getText().equals("Type in here")) {
                    searchField.setText("");
                    searchField.setForeground(Color.WHITE);
                }
            }
        });
        searchField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCryptocurrencies();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCryptocurrencies();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCryptocurrencies();
            }
        });

        cryptoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedId = cryptoList.getSelectedValue();
                    DefaultListModel<String> selectedModel = (DefaultListModel<String>) selectedCryptoList.getModel();
                    if (!selectedModel.contains(selectedId)) {
                        if (selectedModel.size() < 4) {
                            selectedModel.addElement(selectedId);
                        } else {
                            JOptionPane.showMessageDialog(CryptoTrackerApp.this,
                                    "You can only select up to 4 cryptocurrencies.",
                                    "Selection Limit Reached!",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });


        // Add components to the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(searchField, BorderLayout.NORTH);
        JPanel cryptoPanel = new JPanel(new BorderLayout());
        cryptoPanel.add(cryptoScrollPane, BorderLayout.CENTER);
        contentPane.add(cryptoPanel, BorderLayout.WEST);
        contentPane.add(selectedCryptoScrollPane, BorderLayout.CENTER);
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(nameLabel);
        infoPanel.add(symbolLabel);
        contentPane.add(infoPanel, BorderLayout.SOUTH);

        //focus on start
        cryptoList.requestFocus();

        cryptoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedId = cryptoList.getSelectedValue();
                String[] selectedData = cryptocurrencies.get(selectedId);
                nameLabel.setText("Name: " + selectedData[0]);
                symbolLabel.setText("Symbol: " + selectedData[1]);
            }
        });

        selectedCryptoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = selectedCryptoList.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        DefaultListModel<String> model = (DefaultListModel<String>) selectedCryptoList.getModel();
                        model.removeElementAt(selectedIndex);
                    }
                }
            }
        });

        selectedCryptoList.setModel(new DefaultListModel<>());

        UIManager.put("Button.arc", 0);

        setVisible(true);
    }

    private void filterCryptocurrencies() {
        String searchQuery = searchField.getText().toLowerCase();
        DefaultListModel<String> model = new DefaultListModel<>();

        // Find exact match and add it to the model first
        if (cryptocurrencies.containsKey(searchQuery)) {
            model.addElement(searchQuery);
        }

        // Add other matching cryptocurrencies to the model
        for (String id : cryptocurrencies.keySet()) {
            String[] data = cryptocurrencies.get(id);
            if (!id.equals(searchQuery) && (id.toLowerCase().contains(searchQuery) || data[0].toLowerCase().contains(searchQuery) || data[1].toLowerCase().contains(searchQuery))) {
                model.addElement(id);
            }
        }
        cryptoList.setModel(model);
    }
}



