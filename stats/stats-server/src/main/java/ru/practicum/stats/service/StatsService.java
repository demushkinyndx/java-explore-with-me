package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.exception.BadRequestException;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService implements StatsServiceInterface {

	private final EndpointHitRepository endpointHitRepository;

	@Override
	@Transactional
	public void saveHit(EndpointHitDto hitDto) {
		endpointHitRepository.save(EndpointHitMapper.toEntity(hitDto));
	}

	@Override
	public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
		if (start.isAfter(end)) {
			throw new BadRequestException("Дата начала не может быть позже даты конца");
		}

		boolean filterByUris = uris != null && !uris.isEmpty();
		boolean uniqueOnly = Boolean.TRUE.equals(unique);

		if (uniqueOnly) {
			return filterByUris
					? endpointHitRepository.findUniqueStatsByUris(start, end, uris)
					: endpointHitRepository.findUniqueStats(start, end);
		}

		return filterByUris
				? endpointHitRepository.findStatsByUris(start, end, uris)
				: endpointHitRepository.findStats(start, end);
	}
}
