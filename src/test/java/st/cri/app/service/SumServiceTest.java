package st.cri.app.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class SumServiceTest {
    // Test 1: Verificar la suma y aplicación de porcentaje con valores válidos.
    @Test
    void testSumAndPercentageCalculation() {
        SumService sumService = new SumService();
        double result = sumService.calculateSumWithPercentage(5, 5, 10);
        assertEquals(11.0, result);
    }

    // Test 2: Simular el servicio externo mock y validar diferentes porcentajes.
    @Test
    void testExternalServiceMockForDifferentPercentages() {
        ExternalService externalServiceMock = mock(ExternalService.class);
        when(externalServiceMock.getPercentage()).thenReturn(5, 10, 20);

        SumService sumService = new SumService(externalServiceMock);
        assertEquals(10.5, sumService.calculateSumWithPercentageOfExternal(5, 5));
        assertEquals(11.0, sumService.calculateSumWithPercentageOfExternal(5, 5));
        assertEquals(12.0, sumService.calculateSumWithPercentageOfExternal(5, 5));

        verify(externalServiceMock, times(3)).getPercentage();
    }

    // Test 3: Manejar el escenario de servicio externo con fallo y reintentos.
    @Test
    void testRetriesAndFallback() {
        ExternalService externalService = mock(ExternalService.class);

        // Configura el mock para arrojar una excepción en cada llamada.
        when(externalService.getPercentage())
                .thenThrow(RuntimeException.class)
                .thenThrow(RuntimeException.class)
                .thenThrow(RuntimeException.class);

        SumService sumService = new SumService(externalService);

        // Debe realizar tres intentos antes de arrojar una excepción.
        assertThrows(RuntimeException.class, () -> sumService.calculateSumWithPercentageOfExternal(5, 5));

        verify(externalService, times(3)).getPercentage();
    }

    // Test 4: Comprobar el retorno del último valor en caso de fallo persistente.
    @Test
    void testReturnLastValidValueOnFailure() {
        ExternalService externalService = mock(ExternalService.class);

        // Configura el mock para que la primera llamada devuelva 10 y la segunda arroje una excepción.
        when(externalService.getPercentage())
                .thenReturn(10) // primer valor
                .thenThrow(RuntimeException.class); // luego falla

        SumService sumService = new SumService(externalService);

        double result1 = sumService.calculateSumWithPercentageOfExternal(5, 5);

        assertEquals(11, result1);

        // Llamamos a calculateSumWithPercentage nuevamente, pero esta vez debería usar el valor en caché.
        double result2 = sumService.calculateSumWithPercentageOfExternal(5, 5);

        assertEquals(11, result2); // último válido

        // Verificamos que getPercentage se haya llamado exactamente cuatro veces en total.
        // La primera vez OK y luego tres veces fallidas.
        verify(externalService, times(1 + SumService.MAX_RETRIES)).getPercentage();
    }

    // Test: Verificar que el valor en caché se utiliza durante 30 minutos antes de obtenerlo nuevamente del servicio externo.
    @Test
    void testCacheExpiration() {
        ExternalService externalService = mock(ExternalService.class);

        SumService sumService = new SumService(externalService);

        // Configura el mock para devolver un valor de porcentaje fijo (por ejemplo, 10) en la primera llamada
        // y luego 20 en la segunda llamada.
        when(externalService.getPercentage()).thenReturn(10).thenReturn(20);

        // Realiza el primer cálculo con el servicio.
        double result1 = sumService.calculateSumWithExternalPercentage(5, 5);
        assertEquals(11.0, result1);

        // Verifica que se llamó a getPercentage una vez.
        verify(externalService, times(1)).getPercentage();

        // Cambia el tiempo simulando un paso de 29 minutos (menos de 30 minutos).
        sumService.setLastPercentageUpdateTime(System.currentTimeMillis() - 29 * 60 * 1000);

        // Realiza un segundo cálculo con el servicio.
        double result2 = sumService.calculateSumWithExternalPercentage(5, 5);
        assertEquals(11.0, result2); // Sigue devolviendo 10%

        // Verifica que no se haya llamado a getPercentage nuevamente.
        verify(externalService, times(1)).getPercentage();

        // Cambia el tiempo simulando un paso de 31 minutos (más de 30 minutos).
        sumService.setLastPercentageUpdateTime(System.currentTimeMillis() - 31 * 60 * 1000);

        // Realiza un tercer cálculo con el servicio.
        double result3 = sumService.calculateSumWithExternalPercentage(5, 5);
        assertEquals(12.0, result3); // 20% en lugar de 10%

        // Verifica que se haya llamado a getPercentage nuevamente dos veces en total.
        verify(externalService, times(2)).getPercentage();
    }
}