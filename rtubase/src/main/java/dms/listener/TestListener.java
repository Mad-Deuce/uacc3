package dms.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {

    @EventListener(String.class)
    public void reportUserCreation(String event) {
        // e.g. increment a counter to report the total amount of new users
        System.out.println("Increment counter as new user was created: " + event);
    }
}
