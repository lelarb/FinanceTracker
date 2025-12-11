package financeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FinanceTrackerForm {
    private ArrayList<Transaction> currentTransactions;

    private JTextField textField1;
    private JTextField textField2;
    private JTable transactonTable;
    private JComboBox<String> comboBox1;
    private JButton izračunajButton;
    private JPanel mainPanel;
    private JLabel naslov;
    private JLabel opis;
    private JLabel unos;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;
    private JButton ažurirajButton;
    private JButton brišiButton;
    private JComboBox categoryComboBox;
    private JButton exportButton;

    private TransactionManager manager;

    public FinanceTrackerForm(){
        manager = new TransactionManager();
        loadDataIntoTable();
        updateSummary();

        transactonTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = transactonTable.getSelectedRow();
            if(selectedRow >= 0 && currentTransactions != null){
                Transaction t = currentTransactions.get(selectedRow);
                comboBox1.setSelectedItem(t.getType());
                textField1.setText(String.valueOf(t.getAmount()));
                textField2.setText(t.getDescription());
                categoryComboBox.setSelectedItem(t.getCategory());
            }
        });

        izračunajButton.addActionListener(e ->{
            try{
                String type = (String) comboBox1.getSelectedItem();
                double amount = Double.parseDouble(textField1.getText());
                String description = textField2.getText();
                String category = (String) categoryComboBox.getSelectedItem();

                if(description.isEmpty()){
                    JOptionPane.showMessageDialog(null,
                            "Opis ne može biti prazan!");
                    return;
                }
                Transaction t = new Transaction(type, amount, description, category);
                manager.addTransaction((t));
                loadDataIntoTable();
                updateSummary();
                textField1.setText("");
                textField2.setText("");
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(null,
                        "Iznos mora biti broj!");
            }
        });

        ažurirajButton.addActionListener(e -> {
            int selectedRow = transactonTable.getSelectedRow();
            if(selectedRow == -1){
                JOptionPane.showMessageDialog(null, "Molimo odaberite transakciju iz tabele.");
                return;
            }

            try{
                String type = (String) comboBox1.getSelectedItem();
                double amount = Double.parseDouble(textField1.getText());
                String description = textField2.getText();
                String category = (String) categoryComboBox.getSelectedItem();

                if(description.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Opis ne može biti prazan!");
                    return;
                }

                Transaction original = currentTransactions.get(selectedRow);

                Transaction updated = new Transaction(
                        original.getId(),
                        type,
                        amount,
                        description,
                        category
                );

                manager.updateTransaction(updated);
                loadDataIntoTable();
                updateSummary();

                JOptionPane.showMessageDialog(null,
                        "Transakcija je uspješno ažurirana!");
            }
            catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(null,
                        "Iznos mora biti broj!");
            }
            });

        brišiButton.addActionListener(e -> {
            int selectedRow = transactonTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null,
                        "Molimo odaberite transakciju koju želite obrisati.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Jeste li sigurni da želite izbrisati ovu transakciju?",
                    "Potvrda brisanja",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                Transaction t = currentTransactions.get(selectedRow);
                manager.deleteTransaction(t.getId());
                loadDataIntoTable();
                updateSummary();
                JOptionPane.showMessageDialog(null,
                        "Transakcija je obrisana.");
            }
        });

        exportButton.addActionListener(e -> exportToFile());
    }


    private void loadDataIntoTable(){
        currentTransactions = manager.getAllTransactions();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Vrsta");
        model.addColumn("Iznos");
        model.addColumn("Opis");
        model.addColumn("Kategorija");

        for(Transaction t : currentTransactions){
            model.addRow(new Object[]{
                    t.getType(),
                    t.getAmount(),
                    t.getDescription(),
                    t.getCategory()
            });
        }
        transactonTable.setModel(model);
    }

    private void updateSummary(){
        double income = manager.getTotalIncome();
        double expense = manager.getTotalExpense();
        double balance = income - expense;

        incomeLabel.setText("Prihod: " + income);
        expenseLabel.setText("Rashod:" + expense);
        balanceLabel.setText("Saldo:" + balance);
    }
    public JPanel getMainPanel(){
        return mainPanel;
    }

    private void exportToFile() {
        // 1) Uzmemo sve transakcije iz baze
        ArrayList<Transaction> list = manager.getAllTransactions();

        double income = 0;
        double expense = 0;

        // Rashodi po kategorijama
        Map<String, Double> expenseByCategory = new HashMap<>();
        String[] categories = {"Plata", "Hrana", "Racuni", "Zabava", "Prijevoz", "Ostalo"};
        for (String c : categories) {
            expenseByCategory.put(c, 0.0);
        }

        // 2) Prolazimo kroz sve transakcije i sabiramo
        for (Transaction t : list) {
            if ("Prihod".equals(t.getType())) {
                income += t.getAmount();
            } else if ("Rashod".equals(t.getType())) {
                expense += t.getAmount();

                String cat = t.getCategory();
                if (cat == null || cat.isEmpty()) {
                    cat = "Ostalo";
                }

                if (!expenseByCategory.containsKey(cat)) {
                    expenseByCategory.put(cat, 0.0);
                }
                expenseByCategory.put(cat, expenseByCategory.get(cat) + t.getAmount());
            }
        }

        double balance = income - expense;

        // 3) Pišemo u TXT datoteku
        try (FileWriter writer = new FileWriter("finansije_izvjestaj.txt")) {

            writer.write("Ukupni prihod: " + income + "\n");
            writer.write("Ukupni rashod: " + expense + "\n");
            writer.write("Stanje: " + balance + "\n");
            writer.write("\n");
            writer.write("Rashodi po kategorijama:\n");

            for (String c : categories) {
                double value = expenseByCategory.getOrDefault(c, 0.0);
                writer.write(c + ": " + value + "\n");
            }

            JOptionPane.showMessageDialog(null,
                    "Podaci su exportovani u datoteku 'finansije_izvjestaj.txt' u folderu projekta.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Greška pri upisu u datoteku: " + ex.getMessage());
        }
    }

}
