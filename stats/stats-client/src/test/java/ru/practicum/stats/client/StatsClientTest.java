package ru.practicum.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatsClient")
class StatsClientTest {

	private static final String SERVER_URL = "http://localhost:9090";

	@Mock
	private RestTemplate restTemplate;

	private StatsClient statsClient;

	@BeforeEach
	void setUp() {
		statsClient = new StatsClient(SERVER_URL, restTemplate);
	}

	@Test
	@DisplayName("POST в /hit с телом EndpointHit")
	void hit_shouldPostEndpointHitToServer() {
		EndpointHitDto hitDto = EndpointHitDto.builder()
				.app("ewm-main-service")
				.uri("/events/1")
				.ip("192.163.0.1")
				.timestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
				.build();

		when(restTemplate.exchange(
				eq(SERVER_URL + "/hit"),
				eq(HttpMethod.POST),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenReturn(ResponseEntity.status(201).build());

		statsClient.hit(hitDto);

		verify(restTemplate).exchange(
				eq(SERVER_URL + "/hit"),
				eq(HttpMethod.POST),
				argThat(entity -> hitDto.equals(entity.getBody())),
				eq(Void.class)
		);
	}

	@Test
	@DisplayName("GET /stats с параметрами")
	void getStats_shouldRequestStatsWithQueryParams() {
		LocalDateTime start = LocalDateTime.of(2022, 9, 6, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2022, 9, 7, 0, 0, 0);
		List<String> uris = List.of("/events/1", "/events/2");
		List<ViewStatsDto> expected = List.of(
				new ViewStatsDto("ewm-main-service", "/events/1", 3L)
		);

		when(restTemplate.exchange(
				any(URI.class),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenReturn(ResponseEntity.ok(expected));

		List<ViewStatsDto> actual = statsClient.getStats(start, end, uris, true);

		verify(restTemplate).exchange(
				argThat(uri -> {
					String query = uri.toString();
					return query.startsWith(SERVER_URL + "/stats?")
							&& query.contains("start=2022-09-06%2000:00:00")
							&& query.contains("end=2022-09-07%2000:00:00")
							&& query.contains("unique=true")
							&& query.contains("uris=/events/1")
							&& query.contains("uris=/events/2");
				}),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		);
		assertEquals(expected, actual);
	}
}
