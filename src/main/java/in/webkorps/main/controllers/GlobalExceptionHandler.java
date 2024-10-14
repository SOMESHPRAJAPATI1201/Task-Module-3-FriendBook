package in.webkorps.main.controllers;

import in.webkorps.main.entity.User;
import in.webkorps.main.utlls.CustomException;
import in.webkorps.main.utlls.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;
=======
import org.springframework.web.bind.annotation.ResponseStatus;
>>>>>>> 6a875d55ef2437f141a6ae237e7c30a7f161df43

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
<<<<<<< HEAD
    public RedirectView handleCustomException(CustomException ex, Model model) {
        Logger.LOGGER.warn(ex.getMessage());
        model.addAttribute("alert","Issue With Request Sent");
        model.addAttribute("user",new User());
        return new RedirectView("/logPage");
=======
    public String handleCustomException(CustomException ex, Model model) {
        Logger.LOGGER.warn(ex.getMessage());
        model.addAttribute("alert","Issue With Request Sent");
        model.addAttribute("user",new User());
        return "Login1";
>>>>>>> 6a875d55ef2437f141a6ae237e7c30a7f161df43
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
<<<<<<< HEAD
    public RedirectView handleGenericException(Exception ex,Model model) {
        Logger.LOGGER.warn(ex.getMessage());
        model.addAttribute("alert","Issue With Server");
        model.addAttribute("user",new User());
        return new RedirectView("/logPage");
=======
    public String handleGenericException(Exception ex,Model model) {
        Logger.LOGGER.warn(ex.getMessage());
        model.addAttribute("alert","Issue With Server");
        model.addAttribute("user",new User());
        return "Login1";
>>>>>>> 6a875d55ef2437f141a6ae237e7c30a7f161df43
    }
}

