package org.sanjose.helper;

import org.sanjose.model.ScpCajabanco;

/**
 * VASJA class
 * User: prubach
 * Date: 21.09.16
 */
public class EnviarException extends Exception {

    private final ScpCajabanco cajabanco;

    public EnviarException(String message, ScpCajabanco cajabanco) {
        super(message);
        this.cajabanco = cajabanco;
    }

    public ScpCajabanco getCajabanco() {
        return cajabanco;
    }
}
