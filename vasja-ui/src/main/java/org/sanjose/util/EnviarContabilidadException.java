package org.sanjose.util;

import org.sanjose.model.ScpCajabanco;

public class EnviarContabilidadException extends Exception {
    private ScpCajabanco vcb;

    public EnviarContabilidadException(String message) {
        super(message);
    }

    public EnviarContabilidadException(String message, ScpCajabanco vcb) {
        super(message);
        this.vcb = vcb;
    }

    public ScpCajabanco getVcb() {
        return vcb;
    }
}
