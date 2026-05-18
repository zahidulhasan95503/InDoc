package com.legal.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.legal.DTO.AiResponse;

import com.legal.dao.UserRepository;
import com.legal.entites.Users;
import com.legal.entites.message;
import com.legal.service.Aiassistance_youtube;
import com.legal.helper.Mes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Aiassistance_youtube aiService;

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	@ModelAttribute
	public void addcommondata(Principal principal, Model model) {
		String username = principal.getName();

		Users user = userRepository.GetUserByUserName(username);

		model.addAttribute("users", user);
	}

	@GetMapping("/central")
	public String central(Model m, Principal principal) {
		m.addAttribute("title", "InDoc<-Central Documents");
		return "normal/central.html";
	}

	@GetMapping("/state")
	public String state(Model m, Principal principal) {
		m.addAttribute("title", "InDoc<-State Documents");
		m.addAttribute("states", Arrays.asList(
				"Andhra Pradesh", "Karnataka", "Madhya Pradesh", "Maharashtra",
				"Rajasthan", "Tamil Nadu", "Telangana", "Uttar Pradesh", "Delhi", "West bengal"));
		return "normal/state.html";
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
			AiResponse response = aiService.getAssistance(
					"Your role is to assist with problems related to Aadhar document. If asked about anything outside of these topics, don't respond.",
					Messages.getContent());

			m.addAttribute("info", response.getAiContent());
			m.addAttribute("videos", response.getVideos());
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
			Model model, Principal principal) {

		try {
			if (result.hasErrors()) {
				model.addAttribute("users", user);
				return "normal/update_user";
			}

			// Security fix: Only update allowed fields to prevent mass assignment (e.g.,
			// role escalation)
			Users currentUser = userRepository.GetUserByUserName(principal.getName());
			currentUser.setName(user.getName());
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());

			userRepository.save(currentUser);
			session.setAttribute("message", new Mes("Profile updated successfully", "success"));
		} catch (Exception e) {
			log.error("Error updating user: {}", e.getMessage());
			session.setAttribute("message", new Mes("Failed to update profile: " + e.getMessage(), "danger"));
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
			AiResponse response = aiService.getAssistance(
					"Your role is just to assist with problems related to PAN card and nothing other than that. If asked about anything outside of this topic, don't respond.",
					Messages.getContent());

			m.addAttribute("info", response.getAiContent());
			m.addAttribute("videos", response.getVideos());
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
			AiResponse response = aiService.getAssistance(
					"Your role is just to assist with problems related to VoterId card and nothing other than that. If asked about anything outside of this topic, don't respond.",
					Messages.getContent());

			m.addAttribute("info", response.getAiContent());
			m.addAttribute("videos", response.getVideos());
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
			AiResponse response = aiService.getAssistance(
					"Your role is just to assist with problems related to Passport and nothing other than that. If asked about anything outside of this topic, don't respond.",
					Messages.getContent());

			m.addAttribute("info", response.getAiContent());
			m.addAttribute("videos", response.getVideos());
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
			AiResponse response = aiService.getAssistance(
					"Your role is just to assist with problems related to birth certificate and nothing other than that. If asked about anything outside of this topic, don't respond.",
					Messages.getContent());

			m.addAttribute("info", response.getAiContent());
			m.addAttribute("videos", response.getVideos());
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

	// =================== STATE-SPECIFIC DOCUMENT PAGES ===================
	// Handles all documents dynamically

	@GetMapping("/getdynamicans")
	public String dynamicChatGptResult(@ModelAttribute("message") message Messages,
			@RequestParam("type") String type,
			@RequestParam(value = "state", required = false) String state,
			Model m) {

		String displayTitle = toDisplayTitle(type);
		String stateDisplay = (state != null && !state.isEmpty()) ? state : "";

		if (Messages.getContent() != null && !Messages.getContent().trim().isEmpty()) {

			String queryLower = Messages.getContent().toLowerCase();

			// Validation: User requested strict enforcement of context keywords within the
			// search to prevent irrelevant querying.
			boolean isValidSearch = true;

			// Validation: Ensure the user's query explicitly mentions the document title
			// e.g. If on "Ration Card" page, the query must contain "ration card"
			if (!queryLower.contains(displayTitle.toLowerCase())) {
				isValidSearch = false;
			}

			if (isValidSearch) {
				String role = "Your role is to assist with problems related to the " + displayTitle
						+ " document in India, specifically for the state of " + stateDisplay
						+ ". If asked about anything outside of this topic, politely decline and remind them you can only help with "
						+ displayTitle + " problems in " + stateDisplay + ".";

				AiResponse response = aiService.getAssistance(role,
						displayTitle + " " + stateDisplay + " " + Messages.getContent());

				m.addAttribute("info", response.getAiContent());
				m.addAttribute("videos", response.getVideos());
			} else {
				// Failed validation
				m.addAttribute("info",
						"Please search only about " + displayTitle
								+ (stateDisplay.isEmpty() ? "" : " for " + stateDisplay)
								+ " (e.g., ensure your query specifically mentions these keywords).");
			}
		} else {
			m.addAttribute("info", "Please provide a description of your issue.");
		}

		m.addAttribute("documentType", type);
		m.addAttribute("documentTitle", displayTitle);
		m.addAttribute("stateName", stateDisplay);

		return "normal/document_issue";
	}

	@GetMapping("/document")
	public String document(@RequestParam String state, Model model) {
		model.addAttribute("stateName", state);
		return "normal/document";
	}

	@GetMapping("/document_issue")
	public String documentIssue(@RequestParam String state, @RequestParam(required = false) String type, Model model) {
		model.addAttribute("stateName", state);
		if (type != null) {
			model.addAttribute("documentType", type);
			model.addAttribute("documentTitle", toDisplayTitle(type));
		}
		return "normal/document_issue";
	}

	private String getPlacesKeyword(String type) {
		switch (type.toLowerCase()) {
			case "ration_card":
				return "Ration Card Office or MeeSeva Centre";
			case "driving_licence":
				return "RTO Office";
			case "marriage_certificate":
				return "Registrar of Marriage Office";
			case "house_land":
				return "Sub Registrar Office";
			case "death_certificate":
				return "Municipal Corporation Health Department";
			case "caste_certificate":
				return "Tehsildar Office";
			case "registered_will":
				return "Sub Registrar Office";
			default:
				return toDisplayTitle(type) + " Office";
		}
	}

	@GetMapping("/document_centre")
	public String documentCentre(@RequestParam String state, @RequestParam(required = false) String type, Model model) {
		model.addAttribute("stateName", state);
		if (type != null) {
			model.addAttribute("documentType", type);
			model.addAttribute("documentTitle", toDisplayTitle(type));
		}
		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
		return "normal/document_centre";
	}

	/** Converts "ration_card" -> "Ration Card" */
	private String toDisplayTitle(String type) {
		String[] words = type.replace("_", " ").split(" ");
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			if (!word.isEmpty()) {
				sb.append(Character.toUpperCase(word.charAt(0)))
						.append(word.substring(1).toLowerCase())
						.append(" ");
			}
		}
		return sb.toString().trim();
	}

}
