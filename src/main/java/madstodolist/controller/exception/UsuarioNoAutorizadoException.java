package madstodolist.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "No se tiene suficiente permiso para acceder a esta página")
public class UsuarioNoAutorizadoException extends RuntimeException {
    public UsuarioNoAutorizadoException(String message) {
        super(message);
    }
    public UsuarioNoAutorizadoException() {};
}