package st.cri.app.service;

import org.springframework.stereotype.Service;
import st.cri.app.domain.CallHistory;
import st.cri.app.repository.CallHistoryRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CallHistoryService {
    private final ThreadPoolExecutor executor;
    private final CallHistoryRepository callHistoryRepository;

    public CallHistoryService(final CallHistoryRepository callHistoryRepository) {
        this.callHistoryRepository = callHistoryRepository;

        // Configura el ThreadPoolExecutor con el número deseado de hilos y cola de trabajo
        int corePoolSize = 5; // Número de hilos
        int maxPoolSize = 10; // Número máximo de hilos
        long keepAliveTime = 30; // Tiempo en segundos para mantener los hilos inactivos
        int queueCapacity = 100; // Capacidad de la cola de trabajo

        executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity)
        );
    }

    public void saveCallHistory(String endpoint, double result) {
        // Ejecuta la operación de guardar historial en el ThreadPoolExecutor
        executor.execute(() -> {
            // Registra la entrada en el historial si la llamada fue exitosa
            CallHistory callHistory = new CallHistory();
            callHistory.setTimestamp(LocalDateTime.now());
            callHistory.setEndpoint(endpoint);
            callHistory.setStatusCode(200); // Código de estado exitoso
            callHistory.setResponse(String.valueOf(result));

            // Simula un retraso de 1 segundo
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            callHistoryRepository.save(callHistory);
        });
    }
}

