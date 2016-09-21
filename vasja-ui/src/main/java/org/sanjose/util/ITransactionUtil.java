package org.sanjose.util;

import org.sanjose.model.VsjCajabanco;

import java.util.List;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public interface ITransactionUtil {

    List<VsjCajabanco> saveVsjCajabancos(List<VsjCajabanco> cajabancos);
}
