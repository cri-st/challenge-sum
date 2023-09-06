package st.cri.app.controller.util;

import org.springframework.stereotype.Component;

@Component
public class RateLimiter {
    private long lastRequestTime = 0L;
    private int requests = 0;
    private final int maxRequestsPerMinute = 3;

    public synchronized boolean allowRequest() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastRequestTime < 60000) {
            if (requests >= maxRequestsPerMinute) {
                return false; // Se ha superado el límite
            }
        } else {
            // Reiniciar el conteo si ha pasado más de un minuto
            lastRequestTime = currentTimeMillis;
            requests = 0;
        }
        requests++;
        return true;
    }
}

