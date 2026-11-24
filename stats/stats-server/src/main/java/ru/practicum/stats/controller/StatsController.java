package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.DateTimeFormatPatterns;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.service.StatsServiceInterface;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {

	private final StatsServiceInterface statsService;

	@PostMapping("/hit")
	@ResponseStatus(HttpStatus.CREATED)
	public void hit(@Valid @RequestBody EndpointHitDto hitDto) {
		statsService.saveHit(hitDto);
	}

	@GetMapping("/stats")
	public List<ViewStatsDto> getStats(
			@RequestParam @DateTimeFormat(pattern = DateTimeFormatPatterns.DATE_TIME) LocalDateTime start,
			@RequestParam @DateTimeFormat(pattern = DateTimeFormatPatterns.DATE_TIME) LocalDateTime end,
			@RequestParam(required = false) List<String> uris,
			@RequestParam(defaultValue = "false") Boolean unique) {
		return statsService.getStats(start, end, uris, unique);
	}
}
