package org.sanjose.test;

import org.sanjose.views.dict.ConsultaRucDni;
import org.sanjose.views.dict.ConsultaRucDniException;

import java.util.Map;

public class TestConsultaRucDni {

    // apis.net.pe
    public static void main(String[] args) throws ConsultaRucDniException {
        //String ruc = "20558890828";
        String ruc = "20601111781";
        String tipo = "ruc";
        Map<String, String> consulta = ConsultaRucDni.getInstance().get(tipo, ruc);
        for (Map.Entry<String, String> entry : consulta.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
