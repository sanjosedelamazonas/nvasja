package org.sanjose.helper;

import org.sanjose.model.VsjCajabanco;

/**
 * VASJA class
 * User: prubach
 * Date: 21.09.16
 */
public class EnviarException extends Exception {

    private final VsjCajabanco cajabanco;

    public EnviarException(String message, VsjCajabanco cajabanco) {
        super(message);
        this.cajabanco = cajabanco;
    }

    public VsjCajabanco getCajabanco() {
        return cajabanco;
    }
}
