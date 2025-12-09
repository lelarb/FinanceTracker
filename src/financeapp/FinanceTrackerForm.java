package financeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class FinanceTrackerForm {
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

    private TransactionManager manager;

    public FinanceTrackerForm(){
        manager = new TransactionManager();
        loadDataIntoTable();
        updateSummary();

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
    }

    private void loadDataIntoTable(){
        ArrayList<Transaction> list = manager.getAllTransactions();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Vrsta");
        model.addColumn("Iznos");
        model.addColumn("Opis");

        for(Transaction t : list){
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
