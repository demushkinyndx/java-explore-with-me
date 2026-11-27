package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("StatsController")
class StatsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("POST /hit возвращает 201 Created")
	void hit_shouldReturnCreated() throws Exception {
		EndpointHitDto hitDto = EndpointHitDto.builder()
				.app("ewm-main-service")
				.uri("/events/1")
				.ip("192.163.0.1")
				.timestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
				.build();

		mockMvc.perform(post("/hit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(hitDto)))
				.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("GET /stats возвращает агрегированные просмотры")
	void getStats_shouldReturnAggregatedHits() throws Exception {
		EndpointHitDto firstHit = EndpointHitDto.builder()
				.app("ewm-main-service")
				.uri("/events/2")
				.ip("192.163.0.1")
				.timestamp(LocalDateTime.of(2022, 9, 6, 12, 0, 0))
				.build();
		EndpointHitDto secondHit = EndpointHitDto.builder()
				.app("ewm-main-service")
				.uri("/events/2")
				.ip("192.163.0.2")
				.timestamp(LocalDateTime.of(2022, 9, 6, 13, 0, 0))
				.build();

		mockMvc.perform(post("/hit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(firstHit)))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/hit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(secondHit)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/stats")
						.param("start", "2022-09-06 00:00:00")
						.param("end", "2022-09-07 00:00:00")
						.param("uris", "/events/2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].app", is("ewm-main-service")))
				.andExpect(jsonPath("$[0].uri", is("/events/2")))
				.andExpect(jsonPath("$[0].hits", is(2)));
	}
}
