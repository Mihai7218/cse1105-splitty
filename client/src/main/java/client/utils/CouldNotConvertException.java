package client.utils;

public class CouldNotConvertException extends RuntimeException {
    private int status;

    /**
     * Constructor for the exception.
     * @param status - the status code returned by the server.
     */
    public CouldNotConvertException(int status) {
        super();
        this.status = status;
    }

    /**
     * Getter for the status code.
     * @return - the status code.
     */
    public int getStatus() {
        return status;
    }
}
