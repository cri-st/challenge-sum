package st.cri.app.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import st.cri.app.domain.CallHistory;
import st.cri.app.repository.CallHistoryRepository;

@RestController
@Api(tags = "Historial de Llamadas")
public class CallHistoryController {
    private final CallHistoryRepository callHistoryRepository;

    public CallHistoryController(final CallHistoryRepository callHistoryRepository) {
        this.callHistoryRepository = callHistoryRepository;
    }

    @ApiOperation(
            value = "Obtener Historial de Llamadas",
            notes = "Recuperar una lista paginada del historial de llamadas."
    )
    @GetMapping("/call-history")
    public ResponseEntity<Page<CallHistory>> getCallHistory(
            @ApiParam(
                    name = "page",
                    value = "Número de página",
                    defaultValue = "0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @ApiParam(
                    name = "size",
                    value = "Tamaño de página",
                    defaultValue = "10",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CallHistory> callHistoryPage = callHistoryRepository.findAll(pageable);

        return ResponseEntity.ok(callHistoryPage);
    }
}
