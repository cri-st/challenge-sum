package st.cri.app.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import st.cri.app.repository.CallHistoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SumControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CallHistoryRepository callHistoryRepository;

    @Test
    void testCalculateEndpoint() {
        // Guardas el contador actual del historial
        long actualHistoryCount = callHistoryRepository.count();

        // Realiza varias solicitudes al endpoint en un corto período de tiempo para superar el límite de RPM
        for (int i = 1; i <= 4; i++) {
            // Realiza una solicitud al endpoint /calculate
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/calculate?a=5&b=5", String.class);

            if (i < 4) {
                // Verifica que el resultado sea el esperado
                assertEquals(String.valueOf(14.0), response.getBody());
                // Verifica que la respuesta sea un error 200 (OK)
                assertEquals(HttpStatus.OK, response.getStatusCode());

                // Espera un tiempo suficiente para permitir que el guardado en otro hilo se complete
                try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Verifica que se haya guardado el historial en la base de datos
                long historyCount = callHistoryRepository.count();
                assertEquals(i + actualHistoryCount, historyCount);
            } else {
                // Verifica que la respuesta sea un error 429 (Too Many Requests)
                assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
            }
        }
    }
}

