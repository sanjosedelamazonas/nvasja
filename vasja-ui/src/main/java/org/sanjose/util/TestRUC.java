package org.sanjose.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sanjose.views.dict.ConsultaRucDni;
import org.sanjose.views.dict.ConsultaRucDniException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRUC {

    public static void main(String[] args) {
        try {
            System.out.println(ConsultaRucDni.getInstance().get(ConsultaRucDni.RUC, "20176709139"));
        } catch (ConsultaRucDniException he) {
            System.out.println("Could not find...");
        }
    }
}
