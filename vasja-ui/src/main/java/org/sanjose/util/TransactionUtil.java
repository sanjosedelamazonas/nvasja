package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.repo.VsjCajabancoRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
@Service
@Repository
@Transactional
public class TransactionUtil implements ITransactionUtil {

    private static final Logger log = LoggerFactory.getLogger(TransactionUtil.class);

    private final VsjCajabancoRep cajabancoRep;

    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public TransactionUtil(VsjCajabancoRep cajabancoRep, EntityManager em) {
        this.cajabancoRep = cajabancoRep;
        this.em = em;
    }

    @Transactional(readOnly = false)
    public List<VsjCajabanco> saveVsjCajabancos(List<VsjCajabanco> cajabancos) {
        assert TransactionSynchronizationManager.isActualTransactionActive();
        List<VsjCajabanco> savedOperaciones = new ArrayList<>();

        String transCorrelativo = null;
        // Find at least one operation with transCorrelativo set
        for (VsjCajabanco oper : cajabancos) {
            if (!GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo())) {
                transCorrelativo = oper.getCodTranscorrelativo();
                break;
            }
        }
        if (transCorrelativo==null) transCorrelativo = GenUtil.getUuid();
        for (VsjCajabanco oper : cajabancos) {
            if (GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo()))
                oper.setCodTranscorrelativo(transCorrelativo);
        }
        for (VsjCajabanco oper : cajabancoRep.save(cajabancos)) {
            // Tested saving each element using entityManager directly but then an Exception is raised:
            // javax.persistence.TransactionRequiredException: No EntityManager with actual transaction available for
            // current thread - cannot reliably process 'merge' call
            //
//            VsjCajabanco savedCajabanco = em.merge(oper);
            if (GenUtil.strNullOrEmpty(oper.getTxtCorrelativo())) {
                oper.setTxtCorrelativo(GenUtil.getTxtCorrelativo(oper.getCodCajabanco()));
                // TEST transactionality - causes org.springframework.dao.DataIntegrityViolationException
                // because codMes is NOT NULL in the database
///                if (oper.getTxtGlosaitem().equals("abc"))
//                    oper.setCodMes(null);
                oper = cajabancoRep.save(oper);
                log.info("Saved cajabanco from transferencia: " + oper);
//                oper = em.merge(oper);
            }
            savedOperaciones.add(oper);
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