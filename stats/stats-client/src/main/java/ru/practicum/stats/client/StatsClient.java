package ru.practicum.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.DateTimeFormatPatterns;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class StatsClient {

	private static final DateTimeFormatter DATE_TIME_FORMATTER =
			DateTimeFormatter.ofPattern(DateTimeFormatPatterns.DATE_TIME);

	private final RestTemplate restTemplate;
	private final String serverUrl;

	public StatsClient(String serverUrl) {
		this(serverUrl, createDefaultRestTemplate());
	}

	public StatsClient(String serverUrl, RestTemplate restTemplate) {
		this.serverUrl = serverUrl;
		this.restTemplate = restTemplate;
	}

	private static RestTemplate createDefaultRestTemplate() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter(objectMapper));
		return restTemplate;
	}

	public void hit(EndpointHitDto hitDto) {
		restTemplate.exchange(
				serverUrl + "/hit",
				HttpMethod.POST,
				new HttpEntity<>(hitDto),
				Void.class
		);
	}

	public List<ViewStatsDto> getStats(LocalDateTime start,
									   LocalDateTime end,
									   List<String> uris,
									   Boolean unique) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(serverUrl + "/stats")
				.queryParam("start", start.format(DATE_TIME_FORMATTER))
				.queryParam("end", end.format(DATE_TIME_FORMATTER))
				.queryParam("unique", unique);

		if (uris != null) {
			uris.forEach(uri -> builder.queryParam("uris", uri));
		}

		URI uri = builder.encode().build().toUri();

		ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {
				}
		);

		List<ViewStatsDto> body = response.getBody();
		return body == null ? Collections.emptyList() : body;
	}
}
