package dev.nullzwo.enrich.poc.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public record DateRange(LocalDate earliest, LocalDate latest) {
	public static DateRange parseWithOpenEndFallback(DateTimeFormatter timeFormatter, String earliest, String latest) {
		return new DateRange(
				tryParse(timeFormatter, earliest).orElse(LocalDate.MIN),
				tryParse(timeFormatter, latest).orElse(LocalDate.MAX)
		);
	}

	private static Optional<LocalDate> tryParse(DateTimeFormatter timeFormatter, String dateString) {
		return Optional.ofNullable(dateString).flatMap(s -> {
			try {
				return Optional.of(LocalDate.parse(dateString, timeFormatter));
			} catch (DateTimeParseException ex) {
				return Optional.empty();
			}
		});
	}

	public boolean isInRage(LocalDate date) {
		return !date.isBefore(earliest) && !date.isAfter(latest);
	}
}
