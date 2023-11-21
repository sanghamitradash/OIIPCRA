package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OIIPCRAResponse2 {
    private int status;
    private ResponseEntity statusCode;
    private String message;
    public Object data ;
    private Long recordsFiltered;
    private Long recordsTotal;

    public <T> OIIPCRAResponse2(int i, ResponseEntity<T> tResponseEntity, String message, Map<String, Object> result) {
    }
}
