package client.commands;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;

import java.util.Date;
import java.util.List;

public class EditExpenseCommand implements ICommand{

    double amount;
    double priorAmount;
    String currency;
    String priorCurrency;
    String title;
    String priorTitle;
    Date date;
    Date priorDate;
    List<ParticipantPayment> split;
    List<ParticipantPayment> priorSplit;
    Tag tag;
    Tag priorTag;
    Participant payee;
    Participant priorPayee;
    Expense expense;
    ServerUtils serverUtils;
    MainCtrl mainCtrl;

    /**
     * Sets all values for the expense being edited
     * @param amount new expense amount
     * @param currency new expense currency
     * @param title new expense title
     * @param date new expense date
     * @param split new expense split
     * @param tag new expense tag
     * @param payee new expense payee
     * @param expense expense edited
     * @param serverUtils serverutils
     * @param mainCtrl main controller
     */
    public EditExpenseCommand(double amount,
                              String currency,
                              String title,
                              Date date,
                              List<ParticipantPayment> split,
                              Tag tag,
                              Participant payee,
                              Expense expense,
                              ServerUtils serverUtils,
                              MainCtrl mainCtrl) {
        this.amount = amount;
        this.currency = currency;
        this.title = title;
        this.date = date;
        this.split = split;
        this.tag = tag;
        this.expense = expense;
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.payee = payee;
        priorAmount = expense.getAmount();
        priorCurrency = expense.getCurrency();
        priorTitle = expense.getTitle();
        priorDate = expense.getDate();
        priorSplit = expense.getSplit();
        priorTag = expense.getTag();
        priorPayee = expense.getPayee();
    }

    /**
     * Applies edits to expense
     * @throws WebApplicationException possible exceptions when updating server
     */
    @Override
    public void execute() throws WebApplicationException{
        expense.setDate(date);
        expense.setSplit(split);
        expense.setTag(tag);
        expense.setPayee(payee);
        expense.setTitle(title);
        expense.setCurrency(currency);
        expense.setAmount(amount);

        serverUtils.updateExpense(mainCtrl.getEvent().getInviteCode(), expense);

    }

    /**
     * Undoes the edits
     * @throws WebApplicationException possible exception when adding to server
     */
    @Override
    public void undo() throws WebApplicationException{
        expense.setDate(priorDate);
        expense.setSplit(priorSplit);
        expense.setTag(priorTag);
        expense.setPayee(priorPayee);
        expense.setTitle(priorTitle);
        expense.setCurrency(priorCurrency);
        expense.setAmount(priorAmount);

        serverUtils.updateExpense(mainCtrl.getEvent().getInviteCode(), expense);


    }

}
