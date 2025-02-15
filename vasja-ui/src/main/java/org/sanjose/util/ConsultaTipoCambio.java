package org.sanjose.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.views.dict.ConsultaRucDniException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

public class ConsultaTipoCambio {

    private static final Logger log = LoggerFactory.getLogger(ConsultaTipoCambio.class);
    private static ConsultaTipoCambio instance = null;

    private RestTemplate restTemplate;
    private String baseUrl;
    private ConsultaTipoCambio() {
        baseUrl = ConfigurationUtil.get("TIPO_CAMBIO_URL");
        restTemplate = new RestTemplateBuilder(rt-> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ ConfigurationUtil.get("RUC_TOKEN"));
            request.getHeaders().add("Referer", "https://apis.net.pe/tipo-de-cambio-sunat-api");
            List<MediaType> acceptList = new ArrayList<>();
            acceptList.add(MediaType.APPLICATION_JSON);
            request.getHeaders().setAccept(acceptList);
            return execution.execute(request, body);
        })).setReadTimeout(8000).build();
    }


    public static ConsultaTipoCambio getInstance() {
        if (instance==null) {
            instance = new ConsultaTipoCambio();
        }
        return instance;
    }

    public Map<String, String> get(Date date, String monedaSimbolo) throws ConsultaRucDniException {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        Map<String, String> map = new HashMap<>();
        try {
            String stUrl = baseUrl.replace("{0}", sdf.format(date));
            stUrl = stUrl.replace("{1}", monedaSimbolo);
            String json = restTemplate.getForObject(stUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {});
        } catch (HttpClientErrorException he) {
            throw new ConsultaRucDniException("Could not find tipo cambio for: " + sdf.format(date));
        } catch (Exception e) {
            log.error("Problem reading TipoCambio response: " + e);
            throw new ConsultaRucDniException("Problema al consultar tipoCambio para: " + sdf.format(date) + "\n" + e.getMessage(), e);
        }
        return map;
    }
}
