
# Welegal (InDoc) 🇮🇳📄(https://indoc.onrender.com/)

**Welegal** is a comprehensive Spring Boot web application designed to simplify the process of understanding and obtaining Indian legal documents. It helps users navigate complex administrative procedures, troubleshoot common document issues, and locate nearby service centers across multiple Indian states.

## 🚀 Key Features

* **Comprehensive Document Guides:** Step-by-step procedures and issue-resolution guides for essential Indian documents, including:
  * Aadhar Card
  * PAN Card
  * Voter ID
  * Passport
  * Ration Card
  * Birth & Death Certificates
  * Driving Licence
  * Property (House/Land) Registrations
  * Marriage Certificates
* **State-Specific Localization:** Detailed procedures seeded for multiple states (Andhra Pradesh, Delhi, Karnataka, Madhya Pradesh, Maharashtra, Rajasthan, Tamil Nadu, Telangana, Uttar Pradesh, West Bengal).
* **🤖 AI-Powered Legal Assistant:** Integrated with **Google Gemini** (via Spring AI) to provide dynamic, intelligent answers to user queries regarding document procedures.
* **📺 Contextual Video Help:** YouTube Data API integration fetches relevant tutorial videos directly alongside AI assistance.
* **🗺️ Center Locator:** Google Maps API integration helps users find nearby administrative centers for document processing.
* **Secure User Portal:** User authentication, session management, and profile updating powered by Spring Security.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.3.2
* **Database:** MySQL (Spring Data JPA / Hibernate)
* **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
* **AI Integration:** Spring AI (`1.0.0-M2` or later) mapping to Google Gemini APIs
* **External APIs:** Google Gemini API, YouTube Data API v3, Google Maps API
* **Containerization:** Docker (Multi-stage build)

## ⚙️ Local Setup & Installation

### Prerequisites
* [Java 17](https://adoptium.net/) or higher
* Maven (or use the provided `./mvnw` wrapper)
* MySQL Server (running locally or remotely)

### 1. Database Configuration
Create a local MySQL database named `welegal`. The application will automatically create the required tables when it starts up.

### 2. Environment Variables
To keep your API keys secure, do not hardcode them. Set the following environment variables on your system or inside your IDE (like Antigravity, Eclipse, or VS Code) before running the application:

```bash
DB_URL=jdbc:mysql://localhost:3306/welegal
DB_USER=your_mysql_username
DB_PASS=your_mysql_password
PORT=8081
GEMINI_API_KEY=your_google_gemini_api_key
YOUTUBE_API_KEY=your_youtube_api_key
GOOGLE_MAPS_API_KEY=your_google_maps_api_key

# Clean and compile the project
./mvnw clean install

# Run the Spring Boot application
./mvnw spring-boot:run

# 1. Build the Docker image
docker build -t welegal-app .

# 2. Run the container (Make sure to pass the required environment variables)
docker run -p 8081:8081 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/welegal \
  -e DB_USER=root \
  -e DB_PASS=yourpassword \
  -e GEMINI_API_KEY=your_api_key \
  -e YOUTUBE_API_KEY=your_api_key \
  -e GOOGLE_MAPS_API_KEY=your_api_key \
  welegal-app
