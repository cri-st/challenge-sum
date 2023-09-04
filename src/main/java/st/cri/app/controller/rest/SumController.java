package st.cri.app.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import st.cri.app.service.CallHistoryService;
import st.cri.app.service.SumService;

@RestController
public class SumController {
    private final SumService sumService;
    private final CallHistoryService callHistoryService;

    public SumController(final SumService sumService, final CallHistoryService callHistoryService) {
        this.sumService = sumService;
        this.callHistoryService = callHistoryService;
    }

    @GetMapping("/calculate")
    @ApiOperation(value = "Calcula la suma con porcentaje estable", response = Double.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Suma exitosa"),
            @ApiResponse(code = 400, message = "Parámetros inválidos")
    })
    public ResponseEntity<Double> calculate(
            @ApiParam(value = "Primer número", required = true) @RequestParam(name = "a") int a,
            @ApiParam(value = "Segundo número", required = true) @RequestParam(name = "b") int b) {
        double result = sumService.calculateSumWithStablePercentage(a, b);

        // Llama al servicio para guardar el historial de forma asincrónica
        callHistoryService.saveCallHistory("/calculate", result);

        return ResponseEntity.ok(result);
    }
}
