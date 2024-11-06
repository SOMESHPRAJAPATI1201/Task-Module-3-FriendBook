//package in.webkorps.main.utlls;
//
//import in.webkorps.main.config.TemplateAndResolverConfig;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    Map<String, Object> responseMap = new HashMap<>();
//
//    @ExceptionHandler(CustomException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<?> handleCustomException(CustomException ex, HttpServletRequest request) {
//        return createResponse(ex, HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
//        return createResponse(ex, HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler(NullPointerException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<?> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
//        return createResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
//        return createResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }
//
//
//    private ResponseEntity<?> createResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
//        Logger.LOGGER.warn("An error occurred: {}", ex.getMessage());
//        responseMap.clear();
//        responseMap.put("cause", ex.getMessage());
//        responseMap.put("status_code", status);
//        responseMap.put("status_code_message", status.value());
//
//        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
//
//        if (acceptHeader != null && acceptHeader.contains("application/json")) {
//            return ResponseEntity
//                    .status(status)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(responseMap);
//        } else {
//            String renderedHtml = TemplateAndResolverConfig.renderHtmlWithThymeleaf("error", responseMap);
//            return ResponseEntity
//                    .status(status)
//                    .contentType(MediaType.TEXT_HTML)
//                    .body(renderedHtml);
//        }
//    }
//}
//
