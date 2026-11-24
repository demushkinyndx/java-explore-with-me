package ru.practicum.stats.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.dto.DateTimeFormatPatterns;

import java.time.LocalDateTime;

public record ApiError(String status, String reason, String message,
				@JsonFormat(pattern = DateTimeFormatPatterns.DATE_TIME) LocalDateTime timestamp) {
}
