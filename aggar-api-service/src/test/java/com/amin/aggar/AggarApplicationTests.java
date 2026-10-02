package com.amin.aggar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import com.amin.aggar.repository.PropertyRepository;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.security.jwt.secret=test-secret-with-at-least-thirty-two-bytes")
@AutoConfigureMockMvc
class AggarApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtEncoder jwtEncoder;

	@Autowired
	private PropertyRepository propertyRepository;

	@Test
	void publicBrowseRoutesRemainAvailable() throws Exception {
		mockMvc.perform(get("/api/states"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/properties"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/properties/view/waterfront-condo"))
				.andExpect(status().isOk());

		Long propertyId = propertyRepository.findBySlug("waterfront-condo")
				.orElseThrow().getId();
		mockMvc.perform(get("/api/properties/{id}", propertyId))
				.andExpect(status().isOk());
	}

	@Test
	void mutationsAndSensitiveReadsRequireAuthentication() throws Exception {
		mockMvc.perform(post("/api/states")
						.contentType("application/json")
						.content("{}"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/api/users"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/api/price-history"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(post("/api/properties/1/comments")
						.contentType("application/json")
						.content("{\"content\":\"comment\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminRoutesRejectOrdinaryUsersAndAllowAdmins() throws Exception {
		mockMvc.perform(get("/api/users")
						.header("Authorization", "Bearer " + token("member", "ROLE_USER")))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/users")
						.header("Authorization", "Bearer " + token("admin", "ROLE_ADMIN")))
				.andExpect(status().isOk());
	}

	private String token(String username, String role) {
		Instant now = Instant.now();
		return jwtEncoder.encode(JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(),
				JwtClaimsSet.builder()
						.issuer("aggar-api")
						.subject(username)
						.issuedAt(now)
						.expiresAt(now.plusSeconds(300))
						.claim("roles", List.of(role))
						.build())).getTokenValue();
	}

}
