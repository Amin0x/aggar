package com.amin.aggar.frontend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FrontendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void arabicLanguagePathSelectsArabicAndOffersEnglishSwitch() throws Exception {
		mockMvc.perform(get("/ar/login"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"ar\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("dir=\"rtl\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/en/login\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("تسجيل الدخول")));
	}

	@Test
	void englishLanguagePathSelectsEnglish() throws Exception {
		mockMvc.perform(get("/en/login"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"en\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("dir=\"ltr\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/ar/login\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sign In")));
	}

	@Test
	void unprefixedRoutesDefaultToArabicInsteadOfNullLanguage() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isTemporaryRedirect())
				.andExpect(header().string("Location", "/ar/login"));
	}

	@Test
	void homeCanUseAnUnprefixedUrlAndDefaultsToArabic() throws Exception {
		mockMvc.perform(get("/").header("Accept-Language", "en"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"ar\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("dir=\"rtl\"")));
	}

	@Test
	void cityAddPageKeepsLanguagePrefixAndSwitchesLanguages() throws Exception {
		mockMvc.perform(get("/ar/cities/add"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"ar\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/en/cities/add\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/ar/cities/add\"")));
	}

	@Test
	void unprefixedCityAddRouteRedirectsToArabicPath() throws Exception {
		mockMvc.perform(get("/cities/add"))
				.andExpect(status().isTemporaryRedirect())
				.andExpect(header().string("Location", "/ar/cities/add"));
	}
}
