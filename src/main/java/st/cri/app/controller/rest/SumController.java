package st.cri.app.controller.rest;

import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import st.cri.app.controller.util.RateLimiter;
import st.cri.app.exception.NotAvailableException;
import st.cri.app.service.CallHistoryService;
import st.cri.app.service.SumService;

@RestController
@Api(tags = "Suma y Porcentaje")
public class SumController {
    private final SumService sumService;
    private final CallHistoryService callHistoryService;
    private final RateLimiter rateLimiter;

    public SumController(final SumService sumService, final CallHistoryService callHistoryService,
                         final RateLimiter rateLimiter) {
        this.sumService = sumService;
        this.callHistoryService = callHistoryService;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/calculate")
    @ApiOperation(value = "Calcula la suma con porcentaje externo", response = Double.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Suma exitosa"),
            @ApiResponse(code = 400, message = "Parámetros inválidos"),
            @ApiResponse(code = 429, message = "Se ha superado el límite de request por minuto")
    })
    public ResponseEntity<?> calculate(
            @ApiParam(value = "Primer número", required = true) @RequestParam(name = "a") int a,
            @ApiParam(value = "Segundo número", required = true) @RequestParam(name = "b") int b) {
        if (!rateLimiter.allowRequest()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Se ha superado el límite de request por minuto. Intente nuevamente más tarde.");
        }

        try {
            final double result = sumService.calculateSumWithExternalPercentage(a, b);

            // Llama al servicio para guardar el historial de forma asincrónica
            callHistoryService.saveCallHistory("/calculate", result);

            return ResponseEntity.ok(result);
        } catch (NotAvailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("El servicio externo no está disponible. Intente nuevamente más tarde.");
        }
    }
}
