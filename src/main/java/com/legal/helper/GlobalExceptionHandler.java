package com.legal.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

   private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

   @ExceptionHandler(Exception.class)
   public String handle_general_exception(Exception excep, Model model) {

      log.error("Unhandled server exception: ", excep);

      model.addAttribute("errorMessage", "Something went wrong on our server. Please try again later.");
      return "error";

   }

}
