package io.roach.trading.client.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.Availability;

public abstract class BaseCommand {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public Availability connectedCheck() {
        return Connect.isConnected()
                ? Availability.available()
                : Availability.unavailable("You are not connected");
    }
}
