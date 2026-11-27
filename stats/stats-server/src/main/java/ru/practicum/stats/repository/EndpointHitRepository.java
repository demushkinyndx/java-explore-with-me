package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

	/**
	 * По всем uri за период: число уникальных ip по каждой паре app+uri, отсортированное по убыванию hits.
	 */
	@Query("""
			SELECT new ru.practicum.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
			FROM EndpointHit h
			WHERE h.timestamp BETWEEN :start AND :end
			GROUP BY h.app, h.uri
			ORDER BY COUNT(DISTINCT h.ip) DESC
			""")
	List<ViewStatsDto> findUniqueStats(@Param("start") LocalDateTime start,
									   @Param("end") LocalDateTime end);

	/**
	 * По переданным uri за период: общее число просмотров по каждой паре app+uri, отсортированное по убыванию hits.
	 */
	@Query("""
			SELECT new ru.practicum.dto.ViewStatsDto(h.app, h.uri, COUNT(h))
			FROM EndpointHit h
			WHERE h.timestamp BETWEEN :start AND :end
			  AND h.uri IN :uris
			GROUP BY h.app, h.uri
			ORDER BY COUNT(h) DESC
			""")
	List<ViewStatsDto> findStatsByUris(@Param("start") LocalDateTime start,
									   @Param("end") LocalDateTime end,
									   @Param("uris") List<String> uris);

	/**
	 * По переданным uri за период: число уникальных ip по каждой паре app+uri, отсортированное по убыванию hits.
	 */
	@Query("""
			SELECT new ru.practicum.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
			FROM EndpointHit h
			WHERE h.timestamp BETWEEN :start AND :end
			  AND h.uri IN :uris
			GROUP BY h.app, h.uri
			ORDER BY COUNT(DISTINCT h.ip) DESC
			""")
	List<ViewStatsDto> findUniqueStatsByUris(@Param("start") LocalDateTime start,
											 @Param("end") LocalDateTime end,
											 @Param("uris") List<String> uris);

	/**
	 * По всем uri за период: общее число просмотров по каждой паре app+uri, отсортированное по убыванию hits.
	 */
	@Query("""
			SELECT new ru.practicum.dto.ViewStatsDto(h.app, h.uri, COUNT(h))
			FROM EndpointHit h
			WHERE h.timestamp BETWEEN :start AND :end
			GROUP BY h.app, h.uri
			ORDER BY COUNT(h) DESC
			""")
	List<ViewStatsDto> findStats(@Param("start") LocalDateTime start,
								 @Param("end") LocalDateTime end);
}
