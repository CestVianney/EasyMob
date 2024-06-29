package com.archis.bdd;

import com.archis.model.Monstre;
import com.archis.model.MonstreMetamob;
import com.archis.model.ResponseBody;
import com.archis.utils.TypeAjoutEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


public class MetamobCrud {
    private  final RestTemplate restTemplate = getRestTemplate();
    private  String nomPersonnage;
    private  boolean isMetamobActive;

    public MetamobCrud() {
        nomPersonnage = BddCrud.getNomPersonnage();
        isMetamobActive = BddCrud.isMetamobActive();
    }

    public boolean addMonstre(int id, TypeAjoutEnum type, String quantite) throws JsonProcessingException {
        if(!isMetamobActive) {
            return true;
        }
        String url = "https://api.metamob.fr/utilisateurs/" + nomPersonnage + "/monstres";
        String body = "[{\n" +
                "  \"id\": \"" + id + "\",\n" +
                "  \"etat\": \"" + type.getValue() + "\",\n" +
                "  \"quantite\": \"" + quantite + "\"\n" +
                "}]";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, getHeaders().getHeaders()), String.class);

        ObjectMapper mapper = new ObjectMapper();
        ResponseBody responseBody = mapper.readValue(response.getBody(), ResponseBody.class);

        if (responseBody.getReussite() != null && !responseBody.getReussite().isEmpty()) {
            return true;
        } else {
           return false;
        }
    }

    public List<Monstre> getMonstresFromMetamob() {
        if(!isMetamobActive) {
            return List.of();
        }
        String url = "https://api.metamob.fr/utilisateurs/" + nomPersonnage + "/monstres";
        String result = getValuesFromUrl(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<MonstreMetamob> resultBrut = mapper.readValue(result, new TypeReference<List<MonstreMetamob>>(){});
            return resultBrut.stream().map(MonstreMetamob::mapToMonstre).toList();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public String getValuesFromUrl(String url) {
        if(!isMetamobActive) {
            return "";
        }
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getHeaders(), String.class);
        return response.getBody();
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // Set custom error handler to handle invalid MIME type
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 200) {
                    super.handleError(response);
                }
            }
        });

        // Add interceptor to correct MIME type
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            HttpHeaders headers = response.getHeaders();
            // Ensure Content-Type is set to application/json
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Fix any incorrectly formatted Content-Type headers
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