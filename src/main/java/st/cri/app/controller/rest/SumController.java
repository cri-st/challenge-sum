package st.cri.app.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import st.cri.app.service.SumService;

@RestController
public class SumController {
    private final SumService sumService;

    public SumController(final SumService sumService) {
        this.sumService = sumService;
    }

    @GetMapping("/calculate")
    @ApiOperation(value = "Calcula la suma con porcentaje estable", response = Double.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Suma exitosa"),
            @ApiResponse(code = 400, message = "Parámetros inválidos")
    })
    public double calculate(
            @ApiParam(value = "Primer número", required = true) @RequestParam(name = "a") int a,
            @ApiParam(value = "Segundo número", required = true) @RequestParam(name = "b") int b) {
        return sumService.calculateSumWithStablePercentage(a, b);
    }
}
