package com.legal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.legal.dao.UserRepository;
import com.legal.entites.Users;
import com.legal.helper.Mes;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model m) {
		
		m.addAttribute("title","Home<-InDoc");
		return "home";
	}
	
	@RequestMapping("/service")
	public String service(Model m) {
		
		m.addAttribute("title","Services<-InDoc");
		return "service";
	}
	
	@RequestMapping("/about")
	public String aboutus(Model m) {
		
		m.addAttribute("title","About us<-InDoc");
		return "about";
	}
	
	@RequestMapping("/contact")
	public String contactus(Model m) {
		
		m.addAttribute("title","Contact Info<-InDoc");
		return "contact";
	}
	
	@RequestMapping("/signin")
	public String signin(Model m) {
		
		Users users = new Users();
		m.addAttribute("title","Signin <-InDoc");
		m.addAttribute("users",users);
		return "signin";
	}
	
	@PostMapping("/register")
	public String adduser(@Valid @ModelAttribute("users") Users users, BindingResult result,@RequestParam(value="agreement",defaultValue="false") boolean agreement, @RequestParam(value="ismale",defaultValue="false") boolean ismale,Model model, HttpSession session) {
		
		System.out.println(users.getName());
		System.out.println(users.getPhone());
		System.out.println(users.getEmail());
		System.out.println(users.isIsmale());
		System.out.println(users.getPassword());
		System.out.println(users.isAgreement());
		
		
		try {
		
			if(result.hasErrors()) {
				System.out.println(result.toString());
				model.addAttribute("users",users);
				return "signin";
			}
			
			if(!agreement) {
				System.out.println("you have not agreed terms and conditions");
				throw new Exception("Agree Terms and conditions");
			}
			
			users.setRole("ROLE_USER");
			users.setPassword(passwordEncoder.encode(users.getPassword()));
			Users save = userRepository.save(users);
			model.addAttribute("users",new Users());
			
			session.setAttribute("message",new Mes("Successfully registered","alert-success"));
			
			
		}
		
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("users",users);
			session.setAttribute("message",new Mes("Something went wrong"+e.getMessage(),"alert-danger"));
			return "signin";
			
		}
		
		return "login";
	}
	
	@RequestMapping("/login")
	public String login(Model m) {
		
		m.addAttribute("title","Login <-InDoc");
		
		return "login";
		
	}
	
	

}
