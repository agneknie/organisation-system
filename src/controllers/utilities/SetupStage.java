package controllers.utilities;

import javafx.scene.control.Label;

import java.time.LocalTime;

/**
 * Class which has helper methods used to setup stages. Methods here
 * are used in more than one controller/scene.
 */
public class SetupStage {

    /**
     * Sets up the welcome messages in login and register scenes.
     * Uses user's local time to display the according messages.
     *
     * @param greeting main greeting field (e.g. displays: Good Morning!)
     * @param greetingMessage accompanying greeting field (e.g. displays: Start your day.)
     */
    public static void setupWelcomePanel(Label greeting, Label greetingMessage){
        // Gets current user's time
        LocalTime currentTime = LocalTime.now();

        // Times slicing the day into sequences
        LocalTime morningStart = LocalTime.of(5, 0);
        LocalTime afternoonStart = LocalTime.of(12, 0);
        LocalTime eveningStart = LocalTime.of(18, 0);
        LocalTime nightStart = LocalTime.of(22, 0);

        // Morning
        if(currentTime.isAfter(morningStart) && currentTime.isBefore(afternoonStart) || currentTime.equals(morningStart)){
            greeting.setText("Good Morning!");
            greetingMessage.setText("Start your productive day.");
        }
        // Afternoon
        else if(currentTime.isAfter(afternoonStart) && currentTime.isBefore(eveningStart) || currentTime.equals(afternoonStart)){
            greeting.setText("Good Afternoon!");
            greetingMessage.setText("Prime productivity time.");
        }
        // Evening
        else if(currentTime.isAfter(eveningStart) && currentTime.isBefore(nightStart) || currentTime.equals(eveningStart)){
            greeting.setText("Good Evening!");
            greetingMessage.setText("End your day productively.");
        }
        // Night
        else if(currentTime.isAfter(nightStart) || currentTime.isBefore(morningStart) || currentTime.equals(nightStart)){
            greeting.setText("Good Evening!");
            greetingMessage.setText("Late night work rush?");
        }
        // Something is seriously wrong
        else{
            greeting.setText("Hello!");
            greetingMessage.setText("You are literally out of the comprehension of time.");
        }
    }
}