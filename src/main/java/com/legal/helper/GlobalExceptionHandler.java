package com.legal.helper;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(Exception.class)
   public String handle_general_exception(Exception excep, Model model) {

      excep.printStackTrace();

      model.addAttribute("errorMessage", "Something went wrong on our server. Please try again later.");
      return "error";

   }

}
