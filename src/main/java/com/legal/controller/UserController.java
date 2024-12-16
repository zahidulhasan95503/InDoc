package com.legal.controller;

import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

//	@Autowired
	private OllamaChatModel model;

	@Autowired
	private YouTubeService youtubeService;
//		
//	
//	public UserController(OllamaChatModel model) {
//		this.model = model;
//	}

	@ModelAttribute
	public void addcommondata(Principal principal, Model model) {

		String username = principal.getName();

		System.out.println("UserName: " + username);

		Users user = userRepository.GetUserByUserName(username);

		System.out.println(user);

		model.addAttribute("users", user);

	}

	@GetMapping("/index")
	public String index(Model m, Principal principal) {

//		String username = principal.getName();
//		
//		System.out.println("UserName: "+username);
//		
//		Users user =  userRepository.GetUserByUserName(username);
//		
//		System.out.println(user);
//		
//		m.addAttribute("users",user);

		m.addAttribute("title", "InDoc<-User dasboard");

		System.out.println("index page");
		return "normal/index.html";
	}

	///////////////////////////////////////// Aadhaar card//////////////////////////////////////////////
	
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

		// List<ChatMessage> message =
		// System.out.println(prompt.getMessage());

		return "normal/aadhar_issue";
	}

	@GetMapping("/getans")
	public String chatgptresult(@ModelAttribute("message") message Messages, Model m) {

		System.out.println(Messages);
		System.out.println(Messages.getContent());

		// String response = model.call(Message.getContent());
		// showing info in text format
		
		if(Messages.getContent().contains("aadhar card")|Messages.getContent().contains("Aadhar card")) {

		OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");

		ChatRequest request = ChatRequest.builder("llama3").withStream(false)
				.withMessages(List.of(Message.builder(Role.SYSTEM).withContent(
						"Your role is to assist with problems related to Aadhar document. If asked about anything outside of these topics, don't respond.")
						.build(), Message.builder(Role.USER).withContent(Messages.getContent()).build()))
				.withOptions(OllamaOptions.create().withTemperature(0.5f).withTopK(3).withRepeatPenalty(1.2f)
						.withPresencePenalty(0.5f).withFrequencyPenalty(0.3f))
				.build();

//		OllamaChatModel chatModel = new OllamaChatModel(ollamaApi,
//		            OllamaOptions.create()
//		                .withModel("llama3")
//		                .withTemperature(0.9f));

		OllamaApi.ChatResponse response = ollamaApi.chat(request);

		try {

			List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
			m.addAttribute("videos", videos);

		} catch (Exception e) {
			m.addAttribute("error", "Failed to fetch video results. Please try again later.");
		}

		m.addAttribute("info", response.message().content().toString());
		}
		else {
		m.addAttribute("info","search only about aadhar card");
		}
		// response.subscribe(chatResponse->{m.addAttribute("info",chatResponse.message().content().toString());});

		return "normal/aadhar_issue";
	}

	@GetMapping("/aadhar_centre")
	public String aadhar_centre(Model model) {
		
		model.addAttribute("places","Aadhar Enrollment centers near me");

		return "normal/aadhar_centre";

	}

	@GetMapping("/update_user")
	public String update_user() {

		return "normal/update_user";

	}

	@PostMapping("/update")
	public String update(@ModelAttribute Users users, HttpSession session) {

		System.out.println(users.getId());
		System.out.println(users.getName());

		try {

			userRepository.save(users);

			session.setAttribute("message", new Mes("contact updated succesfully", "success"));

		}

		catch (Exception e) {
			e.printStackTrace();

		}

		return "normal/update_user";
	}
	
	//////////////////////////////// PAN CARD ///////////////////////////////////////////////////

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

		System.out.println(Messages);
		System.out.println(Messages.getContent());

		// String response = model.call(Message.getContent());
		// showing info in text format

		if(Messages.getContent().contains("pan card")|Messages.getContent().contains("Pan card")|Messages.getContent().contains("PAN card")) {
		OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");

		ChatRequest request = ChatRequest.builder("llama3").withStream(false)
				.withMessages(List.of(Message.builder(Role.SYSTEM).withContent(
						"Your role is just to assist with problems related to PAN card and nothing other than that. If asked about anything outside of this topic, don't respond.")
						.build(), Message.builder(Role.USER).withContent(Messages.getContent()).build()))
				.withOptions(OllamaOptions.create().withTemperature(0.5f).withTopK(3).withRepeatPenalty(1.2f)
						.withPresencePenalty(0.5f).withFrequencyPenalty(0.3f))
				.build();

//		OllamaChatModel chatModel = new OllamaChatModel(ollamaApi,
//		            OllamaOptions.create()
//		                .withModel("llama3")
//		                .withTemperature(0.9f));

		OllamaApi.ChatResponse response = ollamaApi.chat(request);

		try {

			List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
			m.addAttribute("videos", videos);

		} catch (Exception e) {
			m.addAttribute("error", "Failed to fetch video results. Please try again later.");
		}

		m.addAttribute("info", response.message().content().toString());
		}
		else {
		m.addAttribute("info","search only about PAN card");
		}
		// response.subscribe(chatResponse->{m.addAttribute("info",chatResponse.message().content().toString());});

		return "normal/pan_issue";
	}
	
	@GetMapping("/pan_centre")
	public String pan_centre(Model model) {
		
		model.addAttribute("places","PAN centeres near me");
		
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

		System.out.println(Messages);
		System.out.println(Messages.getContent());

		// String response = model.call(Message.getContent());
		// showing info in text format
		if(Messages.getContent().contains("voter card")|Messages.getContent().contains("Voter card")) {
		OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");

		ChatRequest request = ChatRequest.builder("llama3").withStream(false)
				.withMessages(List.of(Message.builder(Role.SYSTEM).withContent(
						"Your role is just to assist with problems related to VoterId card and nothing other than that. If asked about anything outside of this topic, don't respond.")
						.build(), Message.builder(Role.USER).withContent(Messages.getContent()).build()))
				.withOptions(OllamaOptions.create().withTemperature(0.5f).withTopK(3).withRepeatPenalty(1.2f)
						.withPresencePenalty(0.5f).withFrequencyPenalty(0.3f))
				.build();

//		OllamaChatModel chatModel = new OllamaChatModel(ollamaApi,
//		            OllamaOptions.create()
//		                .withModel("llama3")
//		                .withTemperature(0.9f));

		OllamaApi.ChatResponse response = ollamaApi.chat(request);

		try {

			List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
			m.addAttribute("videos", videos);

		} catch (Exception e) {
			m.addAttribute("error", "Failed to fetch video results. Please try again later.");
		}

		m.addAttribute("info", response.message().content().toString());
		}
		else {
		m.addAttribute("info","search only about Voter card");
		}
		// response.subscribe(chatResponse->{m.addAttribute("info",chatResponse.message().content().toString());});

		return "normal/voter_issue";
	}
	
	@GetMapping("/voter_centre")
	public String voter_centre(Model model) {
		
		model.addAttribute("places","local Election Commission office");
		
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

		System.out.println(Messages);
		System.out.println(Messages.getContent());

		// String response = model.call(Message.getContent());
		// showing info in text format
		if(Messages.getContent().contains("passport")|Messages.getContent().contains("Passport")) {
		OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");

		ChatRequest request = ChatRequest.builder("llama3").withStream(false)
				.withMessages(List.of(Message.builder(Role.SYSTEM).withContent(
						"Your role is just to assist with problems related to Passport and nothing other than that. If asked about anything outside of this topic, don't respond.")
						.build(), Message.builder(Role.USER).withContent(Messages.getContent()).build()))
				.withOptions(OllamaOptions.create().withTemperature(0.5f).withTopK(3).withRepeatPenalty(1.2f)
						.withPresencePenalty(0.5f).withFrequencyPenalty(0.3f))
				.build();

//		OllamaChatModel chatModel = new OllamaChatModel(ollamaApi,
//		            OllamaOptions.create()
//		                .withModel("llama3")
//		                .withTemperature(0.9f));

		OllamaApi.ChatResponse response = ollamaApi.chat(request);

		try {

			List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
			m.addAttribute("videos", videos);

		} catch (Exception e) {
			m.addAttribute("error", "Failed to fetch video results. Please try again later.");
		}

		m.addAttribute("info", response.message().content().toString());
		}
		else {
		m.addAttribute("info","search only about Passportsss");
		}
		// response.subscribe(chatResponse->{m.addAttribute("info",chatResponse.message().content().toString());});

		return "normal/passport_issue";
	}
	
	@GetMapping("/passport_centre")
	public String passport_centre(Model model) {
		
		model.addAttribute("places","Passport office near me");
		
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

		System.out.println(Messages);
		System.out.println(Messages.getContent());

		// String response = model.call(Message.getContent());
		// showing info in text format
		if(Messages.getContent().contains("birth certificate")|Messages.getContent().contains("Birth Certificate")) {
		OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");

		ChatRequest request = ChatRequest.builder("llama3").withStream(false)
				.withMessages(List.of(Message.builder(Role.SYSTEM).withContent(
						"Your role is just to assist with problems related to birth certificate and nothing other than that. If asked about anything outside of this topic, don't respond.")
						.build(), Message.builder(Role.USER).withContent(Messages.getContent()).build()))
				.withOptions(OllamaOptions.create().withTemperature(0.5f).withTopK(3).withRepeatPenalty(1.2f)
						.withPresencePenalty(0.5f).withFrequencyPenalty(0.3f))
				.build();

//		OllamaChatModel chatModel = new OllamaChatModel(ollamaApi,
//		            OllamaOptions.create()
//		                .withModel("llama3")
//		                .withTemperature(0.9f));

		OllamaApi.ChatResponse response = ollamaApi.chat(request);

		try {

			List<YouTubeVideo> videos = youtubeService.searchVideos(Messages.getContent());
			m.addAttribute("videos", videos);

		} catch (Exception e) {
			m.addAttribute("error", "Failed to fetch video results. Please try again later.");
		}

		m.addAttribute("info", response.message().content().toString());
		}
		else {
		m.addAttribute("info","search only about birth certificate");
		}
		// response.subscribe(chatResponse->{m.addAttribute("info",chatResponse.message().content().toString());});

		return "normal/birth_issue";
	}
	
	@GetMapping("/birth_centre")
	public String birth_centre(Model model) {
		
		model.addAttribute("places","birth certificate office near me");
		
		return "normal/birth_centre";
	}
	
}
