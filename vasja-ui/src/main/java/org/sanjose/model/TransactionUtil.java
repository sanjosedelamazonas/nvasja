package org.sanjose.model;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.helper.GenUtil;
import org.sanjose.views.TransferenciaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * SORCER class
 * User: prubach
 * Date: 20.09.16
 */
@Service
public class TransactionUtil implements ITransactionUtil {

    private static final Logger log = LoggerFactory.getLogger(TransactionUtil.class);

    @Resource
    private VsjCajabancoRep cajabancoRep;

    @Transactional(rollbackFor = TestTransactionException.class)
    public List<VsjCajabanco> saveVsjCajabancos(List<VsjCajabanco> cajabancos) {
        assert TransactionSynchronizationManager.isActualTransactionActive();
        List<VsjCajabanco> savedOperaciones = new ArrayList<VsjCajabanco>();
        for (VsjCajabanco oper : cajabancos) {
            VsjCajabanco savedCajabanco = cajabancoRep.save(oper);
            log.info("Saved cajabanco: " + savedCajabanco);
            if (GenUtil.strNullOrEmpty(savedCajabanco.getTxtCorrelativo())) {
                savedCajabanco.setTxtCorrelativo(GenUtil.getTxtCorrelativo(savedCajabanco.getCodCajabanco()));
                savedCajabanco = cajabancoRep.save(savedCajabanco);
                savedOperaciones.add(savedCajabanco);
            }
            if (savedCajabanco.getTxtGlosaitem().equals("abc")) throw new TestTransactionException("test");
        }
        return savedOperaciones;
    }

    public class TestTransactionException extends TransactionException {

        public TestTransactionException(String msg) {
            super(msg);
        }

        public TestTransactionException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

}
