package com.hyperativatechtest.features.card.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativatechtest.config.TestAwsConfig;
import com.hyperativatechtest.config.TestRabbitMqConfig;
import com.hyperativatechtest.features.auth.repository.UserRepository;
import com.hyperativatechtest.features.card.dto.simple.CardLookupResponse;
import com.hyperativatechtest.features.card.dto.simple.CardRequest;
import com.hyperativatechtest.features.card.dto.simple.CardResponse;
import com.hyperativatechtest.features.card.repository.CardRepository;
import com.hyperativatechtest.features.common.entity.Role;
import com.hyperativatechtest.features.common.entity.User;
import com.hyperativatechtest.features.common.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestRabbitMqConfig.class, TestAwsConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("CardController Integration Tests")
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    @Transactional
    void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Regular user");
            userRole.setEnabled(true);
            roleRepository.save(userRole);
        }

        Role userRole = roleRepository.findByName("USER").orElseThrow();
        User testUser = User.builder()
                .username("cardtestuser")
                .password(passwordEncoder.encode("TestPass123!"))
                .role(userRole)
                .build();
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "cardtestuser", roles = "USER")
    @DisplayName("Should create card successfully")
    void shouldCreateCardSuccessfully() throws Exception {
        CardRequest cardRequest = CardRequest.builder()
                .cardNumber("4456897999999999")
                .build();

        MvcResult result = mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.maskedCardNumber").exists())
                .andReturn();

        CardResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CardResponse.class);
        assertNotNull(response.getId());
        assertNotNull(response.getMaskedCardNumber());
        assertEquals(1, cardRepository.count());
    }

    @Test
    @WithMockUser(username = "cardtestuser", roles = "USER")
    @DisplayName("Should prevent duplicate card creation")
    void shouldPreventDuplicateCardCreation() throws Exception {
        CardRequest cardRequest = CardRequest.builder()
                .cardNumber("4456897999999999")
                .build();

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isConflict());

        assertEquals(1, cardRepository.count());
    }

    @Test
    @WithMockUser(username = "cardtestuser", roles = "USER")
    @DisplayName("Should lookup card successfully")
    void shouldLookupCardSuccessfully() throws Exception {
        CardRequest cardRequest = CardRequest.builder()
                .cardNumber("4456897922969999")
                .build();

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/cards/lookup")
                        .param("cardNumber", "4456897922969999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.maskedCardNumber").exists())
                .andReturn();

        CardLookupResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CardLookupResponse.class);
        assertNotNull(response.getId());
        assertNotNull(response.getMaskedCardNumber());
    }

    @Test
    @WithMockUser(username = "cardtestuser", roles = "USER")
    @DisplayName("Should return not found for non-existing card")
    void shouldReturnNotFoundForNonExistingCard() throws Exception {
        mockMvc.perform(get("/api/cards/lookup")
                        .param("cardNumber", "1111111111111111"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should require authentication for card creation")
    void shouldRequireAuthenticationForCardCreation() throws Exception {
        CardRequest cardRequest = CardRequest.builder()
                .cardNumber("4456897999999999")
                .build();

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for card lookup")
    void shouldRequireAuthenticationForCardLookup() throws Exception {
        mockMvc.perform(get("/api/cards/lookup")
                        .param("cardNumber", "4456897999999999"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cardtestuser", roles = "USER")
    @DisplayName("Should validate card number format")
    void shouldValidateCardNumberFormat() throws Exception {
        CardRequest invalidRequest = CardRequest.builder()
                .cardNumber("123")
                .build();

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(0, cardRepository.count());
    }

    @Test
    @WithMockUser(username = "cardtestuser", roles = "USER")
    @DisplayName("Should handle multiple card creation")
    void shouldHandleMultipleCardCreation() throws Exception {
        CardRequest card1 = CardRequest.builder()
                .cardNumber("4111111111111111")
                .build();

        CardRequest card2 = CardRequest.builder()
                .cardNumber("5555555555554444")
                .build();

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card2)))
                .andExpect(status().isCreated());

        assertEquals(2, cardRepository.count());
    }
}

