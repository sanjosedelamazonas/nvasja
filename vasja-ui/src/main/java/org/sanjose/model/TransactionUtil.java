package org.sanjose.model;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.helper.GenUtil;
import org.sanjose.views.TransferenciaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * SORCER class
 * User: prubach
 * Date: 20.09.16
 */
@Service
@Repository
@Transactional
public class TransactionUtil implements ITransactionUtil {

    private static final Logger log = LoggerFactory.getLogger(TransactionUtil.class);

    private VsjCajabancoRep cajabancoRep;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public TransactionUtil(VsjCajabancoRep cajabancoRep, EntityManager em) {
        this.cajabancoRep = cajabancoRep;
        this.em = em;
    }

    @Transactional(readOnly = false)
    public List<VsjCajabanco> saveVsjCajabancos(List<VsjCajabanco> cajabancos) {
        assert TransactionSynchronizationManager.isActualTransactionActive();
        List<VsjCajabanco> savedOperaciones = new ArrayList<VsjCajabanco>();
        for (VsjCajabanco oper : cajabancoRep.save(cajabancos)) {
            //VsjCajabanco savedCajabanco = em.merge(oper);
            if (GenUtil.strNullOrEmpty(oper.getTxtCorrelativo())) {
                oper.setTxtCorrelativo(GenUtil.getTxtCorrelativo(oper.getCodCajabanco()));
                // TEST transactionality
///                if (oper.getTxtGlosaitem().equals("abc"))
//                    oper.setCodMes(null);
                oper = cajabancoRep.save(oper);
                log.info("Saved cajabanco from transferencia: " + oper);
//                oper = em.merge(oper);
                savedOperaciones.add(oper);
            }
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
