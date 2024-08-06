import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novitskii.weatherservice.DTO.MeasurementDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;

public class NotParallelLoad {
    private static final RestTemplate restTemplate = new RestTemplate();

    private static String randomString() {
        int length = 7;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    private static float generateRandomFloat(float min, float max) {
        return (float) (min + Math.random() * (max - min));
    }

    @SuppressWarnings("all")
    private static void send1000PostAdds(String sensorName, String urlPostAdd) {

        for (int i = 0; i < 1000; i++) {
            MeasurementDTO measurementDTO = new MeasurementDTO();
            measurementDTO.setValue(generateRandomFloat(-100, 100));
            measurementDTO.setRaining(Math.random() > 0.5);

            measurementDTO.setSensor(sensorName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MeasurementDTO> request = new HttpEntity<>(measurementDTO, headers);

            ResponseEntity<HttpStatus> response = restTemplate.postForEntity(urlPostAdd, request, HttpStatus.class);

            System.out.println(response.getStatusCode());
        }
    }

    @SuppressWarnings("all")
    private static String registerSensor(String urlPostRegister) {

        Map<String, String> json_post_register = new HashMap<>();
        String sensorName = randomString() + " sensor";
        json_post_register.put("name", sensorName);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(json_post_register);
        ResponseEntity<HttpStatus> response = restTemplate.postForEntity(urlPostRegister, request,
                HttpStatus.class);
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Could not register sensor: " + response.getStatusCode());
        }
        return sensorName;
    }

    private static List getAllMeasurements(String urlGetAll) {
        ResponseEntity<List> response = restTemplate.getForEntity(urlGetAll, List.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not get measurements: " + response.getStatusCode());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<LinkedHashMap<String, Object>> body = response.getBody();
        List<MeasurementDTO> measurementDTOS = objectMapper.convertValue(body, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }

            @Override
            public int compareTo(TypeReference<List<MeasurementDTO>> o) {
                return super.compareTo(o);
            }
        });
        return measurementDTOS;
    }

    @SuppressWarnings("all")
    private static Long getAllRainyDays(String urlGetAllRainy) {
        ResponseEntity<Long> response = restTemplate.getForEntity(urlGetAllRainy, Long.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not get amount of rainy days" + response.getStatusCode());
        }
        return response.getBody();
    }

    public static void main(String[] args) throws InterruptedException {
        String urlPostRegister = "http://localhost:8080/sensors/registration";
        String urlPostAdd = "http://localhost:8080/measurements/add";
        String urlGetAll = "http://localhost:8080/measurements";
        String urlGetAllRainy = "http://localhost:8080/measurements/rainyDaysCount";

        String sensor = registerSensor(urlPostRegister);

        send1000PostAdds(sensor, urlPostAdd);
        List<MeasurementDTO> allMeasurements = getAllMeasurements(urlGetAll);
        System.out.println(allMeasurements.size());
        Long rainyDaysCount = getAllRainyDays(urlGetAllRainy);
        System.out.println(rainyDaysCount);
    }
}
