package ru.practicum.stats.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import ru.practicum.dto.DateTimeFormatPatterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	private static final String BAD_REQUEST_REASON = "Некорректный запрос";

	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleBadRequest(BadRequestException exception) {
		log.warn("Некорректный запрос: {}", exception.getMessage());
		return makeError(HttpStatus.BAD_REQUEST, BAD_REQUEST_REASON, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleNotValid(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> "Поле " + error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));
		log.warn("Ошибка валидации: {}", message);
		return makeError(HttpStatus.BAD_REQUEST, BAD_REQUEST_REASON, message);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleNotReadable(HttpMessageNotReadableException exception) {
		String message = isDateParseError(exception)
				? "Некорректная дата, ожидается " + DateTimeFormatPatterns.DATE_TIME
				: "Некорректное тело запроса";
		log.warn("Не удалось прочитать тело запроса: {}", exception.getMessage());
		return makeError(HttpStatus.BAD_REQUEST, BAD_REQUEST_REASON, message);
	}

	@ExceptionHandler({
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleInvalidParameter(Exception exception) {
		log.warn("Некорректный параметр запроса: {}", exception.getMessage());
		return makeError(HttpStatus.BAD_REQUEST, BAD_REQUEST_REASON, exception.getMessage());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleNotFound(NoResourceFoundException exception) {
		return makeError(HttpStatus.NOT_FOUND, "Ресурс не найден", exception.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ApiError handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
		return makeError(HttpStatus.METHOD_NOT_ALLOWED, "Метод не поддерживается", exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiError handleUnexpected(Exception exception) {
		log.error("Внутренняя ошибка сервера", exception);
		return makeError(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера",
				"Произошла непредвиденная ошибка");
	}

	private ApiError makeError(HttpStatus status, String reason, String message) {
		return new ApiError(status.name(), reason, message, LocalDateTime.now());
	}

	private boolean isDateParseError(Throwable exception) {
		Throwable current = exception;
		while (current != null) {
			if (current instanceof DateTimeParseException) {
				return true;
			}
			if (
					current instanceof InvalidFormatException invalidFormatException
					&& LocalDateTime.class.isAssignableFrom(invalidFormatException.getTargetType())
			) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}
}
