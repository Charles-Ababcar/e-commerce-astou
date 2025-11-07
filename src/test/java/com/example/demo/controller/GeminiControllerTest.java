// package com.example.demo.controller;

// import com.example.demo.dto.ApiResponse;
// import com.example.demo.service.GeminiService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.BDDMockito.given;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(GeminiController.class)
// public class GeminiControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private GeminiService geminiService;

//     @Test
//     public void generateText_shouldReturnGeneratedText() throws Exception {
//         String prompt = "Tell me a joke";
//         String generatedText = "Why did the chicken cross the road?";

//         given(geminiService.generateText(anyString())).willReturn(generatedText);

//         mockMvc.perform(post("/api/gemini/generate")
//                         .contentType(MediaType.TEXT_PLAIN)
//                         .content(prompt))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data").value(generatedText));
//     }
// }
