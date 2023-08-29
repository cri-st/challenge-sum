package st.cri.app.controller.advice;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private List<String> global;
    private Map<String, List<String>> fields;
}
