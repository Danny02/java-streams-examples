package dev.nullzwo.enrich.poc.domain.model.phrases;

import java.io.Serializable;

/**
 * Model phrase return by UCP call
 */
public record ModelRangePhrase(String modelRange, Phrases phrases) {
}
