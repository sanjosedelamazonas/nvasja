package org.sanjose.helper;

import org.sanjose.model.VsjCajabanco;

/**
 * SORCER class
 * User: prubach
 * Date: 21.09.16
 */
public class EnviarException extends Exception {

    private VsjCajabanco cajabanco;

    public EnviarException(String message, VsjCajabanco cajabanco) {
        super(message);
        this.cajabanco = cajabanco;
    }

    public VsjCajabanco getCajabanco() {
        return cajabanco;
    }
}
