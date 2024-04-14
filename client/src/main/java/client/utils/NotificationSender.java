package client.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public interface NotificationSender {

    /**
     * method to display a confirmation message when the reminder is sent
     * this message disappears
     */
    default void showNotification(String binding) {
        getNotificationLabel().textProperty().bind(languageManagerProperty().bind(binding));
        getNotificationLabel().setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), getNotificationLabel());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(
                        Duration.seconds(1), getNotificationLabel());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> getNotificationLabel().setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }

    /**
     * Gets the notification label.
     * @return - the notification label.
     */
    Label getNotificationLabel();

    /**
     * Sets the notification label.
     * @param NotificationLabel - the notification label.
     */
    void setNotificationLabel(Label NotificationLabel);


    /**
     * Getter for the language manager property.
     *
     * @return - the language manager property.
     */
    LanguageManager languageManagerProperty();
}
