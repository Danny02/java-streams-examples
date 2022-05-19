package dev.nullzwo.enrich.poc.domain.service;

import dev.nullzwo.enrich.poc.domain.model.BrandRange;
import dev.nullzwo.enrich.poc.domain.model.phrases.AggregatedPhrases;
import dev.nullzwo.enrich.poc.domain.spi.PhrasesPort;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.time.LocalDate;
import java.util.Locale;

import static java.util.function.Function.identity;

public class PhrasesService {
	private final PhrasesPort phrasesPort;

	public PhrasesService(PhrasesPort phrasesPort) {
		this.phrasesPort = phrasesPort;
	}

	public Map<Locale, AggregatedPhrases> getAggregatedPhrases(BrandRange brandRange, List<Locale> countryCode, LocalDate date) {
		return countryCode.flatMap(c -> phrasesPort.getAggregatedPhrases(brandRange, c, date)).toMap(identity());
	}
}
