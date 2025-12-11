package financeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

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
            }
        });

        izračunajButton.addActionListener(e ->{
            try{
                String type = (String) comboBox1.getSelectedItem();
                double amount = Double.parseDouble(textField1.getText());
                String description = textField2.getText();
                if(description.isEmpty()){
                    JOptionPane.showMessageDialog(null,
                            "Opis ne može biti prazan!");
                    return;
                }
                Transaction t = new Transaction(type, amount, description);
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

                if(description.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Opis ne može biti prazan!");
                    return;
                }

                Transaction original = currentTransactions.get(selectedRow);

                Transaction updated = new Transaction(
                        original.getId(),
                        type,
                        amount,
                        description
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

    }


    private void loadDataIntoTable(){
        currentTransactions = manager.getAllTransactions();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Vrsta");
        model.addColumn("Iznos");
        model.addColumn("Opis");

        for(Transaction t : currentTransactions){
            model.addRow(new Object[]{
                    t.getType(),
                    t.getAmount(),
                    t.getDescription()
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
}
