package com.legal.controller;

import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.Rendering;

import com.legal.DTO.YouTubeVideo;
import com.legal.dao.UserRepository;
import com.legal.entites.Users;
import com.legal.entites.message;
import com.legal.service.YouTubeService;
import com.legal.helper.Mes;

import io.netty.handler.timeout.TimeoutException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatModel chatModel;

	@Autowired
	private YouTubeService youtubeService;

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	@ModelAttribute
	public void addcommondata(Principal principal, Model model) {
		String username = principal.getName();

		Users user = userRepository.GetUserByUserName(username);

		model.addAttribute("users", user);
	}

	@GetMapping("/index")
	public String index(Model m, Principal principal) {
		m.addAttribute("title", "InDoc<-User dasboard");

		return "normal/index.html";
	}

	///////////////////////////////////////// Aadhaar
	///////////////////////////////////////// //////////////////////////////////////////////

	@GetMapping("/aadhar")
	public String aadhar(Model m, Principal principal) {
		String username = principal.getName();
		Users user = userRepository.GetUserByUserName(username);
		m.addAttribute("users", user);
		m.addAttribute("title", "InDoc<-Aadhar");
		return "normal/aadhar";
	}

	@GetMapping("/aadhar_issue")
	public String aadhar_issue(Model m, Principal principal) {
		String username = principal.getName();
		Users user = userRepository.GetUserByUserName(username);
		m.addAttribute("users", user);
		return "normal/aadhar_issue";
	}

	@GetMapping("/getans")
	public String chatgptresult(@ModelAttribute("message") message Messages, Model m) {

		if (Messages.getContent().toLowerCase().contains("aadhar card")) {
			org.springframework.ai.chat.prompt.Prompt prompt = new org.springframework.ai.chat.prompt.Prompt(
					List.of(
							new org.springframework.ai.chat.messages.SystemMessage(
									"Your role is to assist with problems related to Aadhar document. If asked about anything outside of these topics, don't respond."),
							new org.springframework.ai.chat.messages.UserMessage(Messages.getContent())));

			ChatResponse response = chatModel.call(prompt);

			try {
				List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
				m.addAttribute("videos", videos);
			} catch (Exception e) {
				log.error("YouTube API error: {}", e.getMessage());
				m.addAttribute("error", "Failed to fetch video results. Please try again later.");
			}

			m.addAttribute("info", response.getResult().getOutput().getContent());
		} else {
			m.addAttribute("info", "search only about aadhar card");
		}

		return "normal/aadhar_issue";
	}

	@GetMapping("/aadhar_centre")
	public String aadhar_centre(Model model) {
		model.addAttribute("places", "Aadhaar Seva Kendras");
		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
		return "normal/aadhar_centre";
	}

	@GetMapping("/update_user")
	public String update_user() {
		return "normal/update_user";
	}

	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("users") Users user, BindingResult result, HttpSession session,
			Model model) {

		try {
			if (result.hasErrors()) {
				model.addAttribute("users", user);
				return "normal/update_user";
			}
			userRepository.save(user);
			session.setAttribute("message", new Mes("contact updated succesfully", "success"));
		} catch (Exception e) {
			log.error("Error updating user: {}", e.getMessage());
		}
		return "normal/update_user";
	}

	//////////////////////////////// PAN CARD
	//////////////////////////////// ///////////////////////////////////////////////////

	@GetMapping("/pan")
	public String pan() {
		return "normal/pan";
	}

	@GetMapping("/pan_issue")
	public String pan_issue() {
		return "normal/pan_issue";
	}

	@GetMapping("/getpanans")
	public String panchatgptresult(@ModelAttribute("message") message Messages, Model m) {

		if (Messages.getContent().toLowerCase().contains("pan card")) {
			org.springframework.ai.chat.prompt.Prompt prompt = new org.springframework.ai.chat.prompt.Prompt(
					List.of(
							new org.springframework.ai.chat.messages.SystemMessage(
									"Your role is just to assist with problems related to PAN card and nothing other than that. If asked about anything outside of this topic, don't respond."),
							new org.springframework.ai.chat.messages.UserMessage(Messages.getContent())));

			ChatResponse response = chatModel.call(prompt);

			try {
				List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
				m.addAttribute("videos", videos);
			} catch (Exception e) {
				log.error("YouTube API error: {}", e.getMessage());
				m.addAttribute("error", "Failed to fetch video results. Please try again later.");
			}

			m.addAttribute("info", response.getResult().getOutput().getContent());
		} else {
			m.addAttribute("info", "search only about PAN card");
		}
		return "normal/pan_issue";
	}

	@GetMapping("/pan_centre")
	public String pan_centre(Model model) {
		model.addAttribute("places", "PAN centeres near me");
		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
		return "normal/pan_centre";
	}

	///////////////////////// VOTER ID/////////////////////////////////

	@GetMapping("/voter")
	public String voter() {
		return "normal/voter";
	}

	@GetMapping("/voter_issue")
	public String voter_issue() {
		return "normal/voter_issue";
	}

	@GetMapping("/getvoterans")
	public String voterchatgptresult(@ModelAttribute("message") message Messages, Model m) {

		if (Messages.getContent().toLowerCase().contains("voter card")) {
			org.springframework.ai.chat.prompt.Prompt prompt = new org.springframework.ai.chat.prompt.Prompt(
					List.of(
							new org.springframework.ai.chat.messages.SystemMessage(
									"Your role is just to assist with problems related to VoterId card and nothing other than that. If asked about anything outside of this topic, don't respond."),
							new org.springframework.ai.chat.messages.UserMessage(Messages.getContent())));

			ChatResponse response = chatModel.call(prompt);

			try {
				List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
				m.addAttribute("videos", videos);
			} catch (Exception e) {
				log.error("YouTube API error: {}", e.getMessage());
				m.addAttribute("error", "Failed to fetch video results. Please try again later.");
			}

			m.addAttribute("info", response.getResult().getOutput().getContent());
		} else {
			m.addAttribute("info", "search only about Voter card");
		}
		return "normal/voter_issue";
	}

	@GetMapping("/voter_centre")
	public String voter_centre(Model model) {
		model.addAttribute("places", "local Electoral Registration Office");
		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
		return "normal/voter_centre";
	}

	////////////////// LAND DOCUMENTS ////////////////

	@GetMapping("/passport")
	public String land() {
		return "normal/passport";
	}

	@GetMapping("/passport_issue")
	public String passport_issue() {
		return "normal/passport_issue";
	}

	@GetMapping("/getpassportans")
	public String passportchatgptresult(@ModelAttribute("message") message Messages, Model m) {

		if (Messages.getContent().toLowerCase().contains("passport")) {
			org.springframework.ai.chat.prompt.Prompt prompt = new org.springframework.ai.chat.prompt.Prompt(
					List.of(
							new org.springframework.ai.chat.messages.SystemMessage(
									"Your role is just to assist with problems related to Passport and nothing other than that. If asked about anything outside of this topic, don't respond."),
							new org.springframework.ai.chat.messages.UserMessage(Messages.getContent())));

			ChatResponse response = chatModel.call(prompt);

			try {
				List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
				m.addAttribute("videos", videos);
			} catch (Exception e) {
				log.error("YouTube API error: {}", e.getMessage());
				m.addAttribute("error", "Failed to fetch video results. Please try again later.");
			}

			m.addAttribute("info", response.getResult().getOutput().getContent());
		} else {
			m.addAttribute("info", "search only about Passportsss"); // Keeping original typo if any
		}
		return "normal/passport_issue";
	}

	@GetMapping("/passport_centre")
	public String passport_centre(Model model) {
		model.addAttribute("places", "Passport Seva Kendras / POPSK");
		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
		return "normal/passport_centre";
	}

	////////////////////////////////////// BIRTH DOCUMENTS ////////////////

	@GetMapping("/birth")
	public String birth() {
		return "normal/birth";
	}

	@GetMapping("/birth_issue")
	public String birth_issue() {
		return "normal/birth_issue";
	}

	@GetMapping("/getbirthans")
	public String birthchatgptresult(@ModelAttribute("message") message Messages, Model m) {

		if (Messages.getContent().toLowerCase().contains("birth certificate")) {
			org.springframework.ai.chat.prompt.Prompt prompt = new org.springframework.ai.chat.prompt.Prompt(
					List.of(
							new org.springframework.ai.chat.messages.SystemMessage(
									"Your role is just to assist with problems related to birth certificate and nothing other than that. If asked about anything outside of this topic, don't respond."),
							new org.springframework.ai.chat.messages.UserMessage(Messages.getContent())));

			ChatResponse response = chatModel.call(prompt);

			try {
				List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
				m.addAttribute("videos", videos);
			} catch (Exception e) {
				log.error("YouTube API error: {}", e.getMessage());
				m.addAttribute("error", "Failed to fetch video results. Please try again later.");
			}

			m.addAttribute("info", response.getResult().getOutput().getContent());
		} else {
			m.addAttribute("info", "search only about birth certificate");
		}
		return "normal/birth_issue";
	}

	@GetMapping("/birth_centre")
	public String birth_centre(Model model) {
		model.addAttribute("places", "birth certificate office near me");
		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
		return "normal/birth_centre";
	}
}
