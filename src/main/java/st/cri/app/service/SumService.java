package st.cri.app.service;

import org.springframework.stereotype.Service;
import st.cri.app.exception.NotAvailableException;

import java.util.concurrent.TimeUnit;

@Service
public class SumService {
    public static final int MAX_RETRIES = 3;
    private static final long CACHE_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(30); // 30 minutos en milisegundos.
    private final ExternalService externalService;
    private int lastValidPercentage = -1;
    private long lastPercentageUpdateTime = 0; // Tiempo de la última actualización del porcentaje en milisegundos.

    public SumService() {
        this.externalService = new ExternalService();
    }

    public SumService(final ExternalService externalService) {
        this.externalService = externalService;
    }

    // Calcula la suma de 'a' y 'b' con un porcentaje establecido contemplando el valor en caché.
    public double calculateSumWithExternalPercentage(int a, int b) {
        // Verificar si el valor en caché todavía es válido y no ha pasado el tiempo de 30 minutos.
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPercentageUpdateTime < CACHE_EXPIRATION_TIME && lastValidPercentage != -1) {
            // Valor en caché válido, realizar el cálculo utilizando el valor en caché.
            return calculateSumWithPercentage(a, b, lastValidPercentage);
        } else {
            // El porcentaje ha cambiado o el valor en caché ha expirado, calcular con el nuevo porcentaje.
            return calculateSumWithPercentageOfExternal(a, b);
        }
    }

    // Calcula la suma de 'a' y 'b' con un porcentaje obtenido del servicio externo.
    double calculateSumWithPercentageOfExternal(int a, int b) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                // Intenta obtener el porcentaje del servicio externo.
                int percentage = externalService.getPercentage();
                this.lastValidPercentage = percentage;

                long currentTime = System.currentTimeMillis();
                // Verifica si el tiempo de la última actualización del porcentaje ha expirado.
                if (currentTime - lastPercentageUpdateTime >= CACHE_EXPIRATION_TIME) {
                    // Actualiza el tiempo de la última actualización del porcentaje.
                    this.lastPercentageUpdateTime = currentTime;
                }
                return calculateSumWithPercentage(a, b, percentage);
            } catch (Exception ex) {
                retries++; // Incrementa el contador de reintentos en caso de error.
            }
        }

        // Si falla completamente después de 3 reintentos, devuelve el último valor válido o arroja una excepción.
        if (lastValidPercentage != -1) {
            return calculateSumWithPercentage(a, b, lastValidPercentage);
        } else {
            throw new NotAvailableException("No external percentage available"); // Excepción en caso de fallo persistente.
        }
    }

    // Calcula la suma de 'a' y 'b' con un porcentaje 'p'.
    double calculateSumWithPercentage(int a, int b, int p) {
        return a + b + (a + b) * p / 100.0;
    }

    void setLastPercentageUpdateTime(long lastPercentageUpdateTime) {
        this.lastPercentageUpdateTime = lastPercentageUpdateTime;
    }
}
