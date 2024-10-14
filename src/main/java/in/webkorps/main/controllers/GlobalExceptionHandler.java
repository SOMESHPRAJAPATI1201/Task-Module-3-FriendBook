package in.webkorps.main.controllers;

import in.webkorps.main.entity.User;
import in.webkorps.main.utlls.CustomException;
import in.webkorps.main.utlls.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RedirectView handleCustomException(CustomException ex, Model model) {
        Logger.LOGGER.warn(ex.getMessage());
        model.addAttribute("alert","Issue With Request Sent");
        model.addAttribute("user",new User());
        return new RedirectView("/logPage");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RedirectView handleGenericException(Exception ex,Model model) {
        Logger.LOGGER.warn(ex.getMessage());
        model.addAttribute("alert","Issue With Server");
        model.addAttribute("user",new User());
        return new RedirectView("/logPage");
    }
}

