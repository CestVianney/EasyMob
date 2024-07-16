package com.easymob.bdd;

import com.easymob.model.MonstreMetamobRecense;
import com.easymob.model.ResponseBody;
import com.easymob.utils.TypeAjoutEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MetamobCrud {
    private  final RestTemplate restTemplate = getRestTemplate();
    private  String nomPersonnage;

    public MetamobCrud() {
        nomPersonnage = BddCrud.getNomPersonnage();
    }

    public boolean addMonstres(List<Integer> ids, TypeAjoutEnum type, String quantite) throws JsonProcessingException {
        String url = "https://api.metamob.fr/utilisateurs/" + nomPersonnage + "/monstres";

        List<String> bodies = new ArrayList<>();
        for (Integer id : ids) {
            String body = "{\n" +
                    "  \"id\": \"" + id + "\",\n" +
                    "  \"etat\": \"" + type.getValue() + "\",\n" +
                    "  \"quantite\": \"" + quantite + "\"\n" +
                    "}";
            bodies.add(body);
        }
        String finalBody = "[" + String.join(",", bodies) + "]";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(finalBody, getHeaders().getHeaders()), String.class);
            ObjectMapper mapper = new ObjectMapper();
            ResponseBody responseBody = mapper.readValue(response.getBody(), ResponseBody.class);
            return responseBody.getReussite() != null && !responseBody.getReussite().isEmpty();
        } catch (Exception e) {
            if(e.getMessage().contains("https://api.metamob.fr/")){
                System.out.println("-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/6/-/-/Connection reset");
            }
        }
        return false;
    }

    public boolean getAllMonstres() {
        try {
            BddCrud.deleteAllMonstres();
            String url = "https://api.metamob.fr/monstres";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getHeaders(), String.class);

            ObjectMapper mapper = new ObjectMapper();
            List<MonstreMetamobRecense> monstres = mapper.readValue(response.getBody(), new TypeReference<List<MonstreMetamobRecense>>() {
            });
            monstres.forEach(BddCrud::addMonstre);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 200) {
                    super.handleError(response);
                }
            }
        });

        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            HttpHeaders headers = response.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
            if (contentType != null && contentType.contains(",")) {
                headers.set(HttpHeaders.CONTENT_TYPE, contentType.replace(",", ";"));
            }
            return response;
        });

        return restTemplate;
    }

    private HttpEntity<String> getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("HTTP-X-APIKEY", BddCrud.getApiKey());
        headers.set("HTTP-X-USERKEY", BddCrud.getUserKey());
        return new HttpEntity<>(headers);
    }
}