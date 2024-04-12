package client.commands;


public interface ICommand {

    /**
     * Executes the command
     */
    public void execute();

    /**
     * Undoes the command
     */
    public void undo();


}
