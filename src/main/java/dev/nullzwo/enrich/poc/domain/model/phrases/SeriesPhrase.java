package dev.nullzwo.enrich.poc.domain.model.phrases;

import java.io.Serializable;

/**
 * Series phrase return by UCP call
 */
public record SeriesPhrase(String series, Phrases phrases) {
}
