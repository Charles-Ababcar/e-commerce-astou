// package com.example.demo.controller;

// import com.example.demo.dto.ApiResponse;
// import com.example.demo.service.GeminiService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.io.IOException;

// @RestController
// @RequestMapping("/api/gemini")
// public class GeminiController {

//     @Autowired
//     private GeminiService geminiService;

//     @PostMapping("/generate")
//     public ResponseEntity<ApiResponse<String>> generateText(@RequestBody String prompt) throws IOException {
//         String generatedText = geminiService.generateText(prompt);
//         ApiResponse<String> response = new ApiResponse<>("Successfully generated text", generatedText);
//         return new ResponseEntity<>(response, HttpStatus.OK);
//     }
// }
