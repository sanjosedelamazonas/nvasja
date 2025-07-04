package org.sanjose.views.dict;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.util.ConfigurationUtil;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultaRucDni {

    private static final Logger log = LoggerFactory.getLogger(ConsultaRucDni.class);
    private static ConsultaRucDni instance = null;

    public static final String RUC = "ruc";
    public static final String DNI = "dni";

    private RestTemplate restTemplate;
    private String baseUrlRuc;
    private String baseUrlDni;
    private ConsultaRucDni() {
        baseUrlRuc = ConfigurationUtil.get("RUC_URL");
        baseUrlDni = ConfigurationUtil.get("DNI_URL");
        restTemplate = new RestTemplateBuilder(rt-> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ ConfigurationUtil.get("RUC_TOKEN"));
            request.getHeaders().add("Referer", "http://apis.net.pe/api-ruc");
            List<MediaType> acceptList = new ArrayList<>();
            acceptList.add(MediaType.APPLICATION_JSON);
            request.getHeaders().setAccept(acceptList);
            return execution.execute(request, body);
        })).setReadTimeout(8000).build();
    }


    public static ConsultaRucDni getInstance() {
        if (instance==null) {
            instance = new ConsultaRucDni();
        }
        return instance;
    }

    public Map<String, String> get(String type, String numero) throws ConsultaRucDniException {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> jsonMap = new HashMap<>();
        try {
            String url = "";
            if (type.equals("ruc")) {
                url = ConfigurationUtil.get("RUC_VERSION").equals("v2") ? baseUrlRuc + "/full?numero=" + numero : baseUrlRuc + "/" + type + "?numero=" + numero;
            } else {
                url = ConfigurationUtil.get("RUC_VERSION").equals("v2") ? baseUrlDni + "?numero=" + numero : baseUrlDni + "/" + type + "?numero=" + numero;
            }
            String json = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            // Remove "localesAnexos" if it exists
            jsonMap.remove("localesAnexos");

            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        } catch (HttpClientErrorException he) {
            throw new ConsultaRucDniException("Could not find " + type.toUpperCase() + " " + numero);
        } catch (Exception e) {
            log.error("Problem reading ConsultaRucDni response: " + e);
            throw new ConsultaRucDniException("Problema al consultar el " + type.toUpperCase() + " " + numero + "\n" + e.getMessage(), e);
        }
        return map;
    }
}
