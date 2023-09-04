package st.cri.app.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
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

        // Realiza una solicitud al endpoint /calculate
        double result = restTemplate.getForObject("http://localhost:" + port + "/calculate?a=5&b=5", Double.class);

        // Verifica que el resultado sea el esperado
        assertEquals(14.0, result);

        // Espera un tiempo suficiente para permitir que el guardado en otro hilo se complete
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verifica que se haya guardado el historial en la base de datos
        long historyCount = callHistoryRepository.count();
        assertEquals(1 + actualHistoryCount, historyCount);
    }
}

