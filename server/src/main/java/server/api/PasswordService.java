package server.api;

public class PasswordService {
    private static String password;

    public PasswordService() {
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        PasswordService.password = password;
    }
}
