// package com.example.demo.service;

// import com.google.cloud.vertexai.VertexAI;
// import com.google.cloud.vertexai.api.GenerateContentResponse;
// import com.google.cloud.vertexai.generativeai.GenerativeModel;
// import org.springframework.stereotype.Service;

// import java.io.IOException;

// @Service
// public class GeminiService {

//     public String generateText(String prompt) throws IOException {
//         // TODO: Replace with your project ID and location
//         String projectId = "your-google-cloud-project-id";
//         String location = "us-central1";
//         String modelName = "gemini-pro";

//         try (VertexAI vertexAI = new VertexAI(projectId, location)) {
//             GenerativeModel model = new GenerativeModel(modelName, vertexAI);
//             GenerateContentResponse response = model.generateContent(prompt);
//             return response.toString();
//         }
//     }
// }
