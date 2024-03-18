package server.api;

public class PasswordService {
    private static String password;

    /**
     * Empty Constructor for the passwordService
     */
    public PasswordService() {
    }

    /**
     * Getter for the password
     * @return Returns the current password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * setter for the password
     * @param password The value to set the password to
     */
    public static void setPassword(String password) {
        PasswordService.password = password;
    }
}
