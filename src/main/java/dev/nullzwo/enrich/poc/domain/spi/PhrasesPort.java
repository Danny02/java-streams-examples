package dev.nullzwo.enrich.poc.domain.spi;

import dev.nullzwo.enrich.poc.domain.model.BrandRange;
import dev.nullzwo.enrich.poc.domain.model.phrases.AggregatedPhrases;
import io.vavr.collection.Map;

import java.time.LocalDate;
import java.util.Locale;

public interface PhrasesPort {
	Map<Locale, AggregatedPhrases> getAggregatedPhrases(BrandRange brandRange, Locale countryCode, LocalDate date);
}
