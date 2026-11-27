package ru.practicum.stats.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndpointHitMapper {

	public static EndpointHit toEntity(EndpointHitDto dto) {
		return EndpointHit.builder()
				.app(dto.getApp())
				.uri(dto.getUri())
				.ip(dto.getIp())
				.timestamp(dto.getTimestamp())
				.build();
	}

	public static EndpointHitDto toDto(EndpointHit entity) {
		return EndpointHitDto.builder()
				.id(entity.getId())
				.app(entity.getApp())
				.uri(entity.getUri())
				.ip(entity.getIp())
				.timestamp(entity.getTimestamp())
				.build();
	}
}
