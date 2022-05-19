package dev.nullzwo.enrich.poc.domain.model.phrases;

import dev.nullzwo.enrich.poc.domain.model.VgModelCode;

import java.io.Serializable;
import java.util.Map;

public record AggregatedPhrases(
		Map<String, BodyTypePhrases> bodyTypes,
		Map<String, ModelRangePhrase> modelRanges,
		Map<String, SeriesPhrase> series,
		Map<VgModelCode, ModelPhrase> models
) {

	public boolean isEmpty() {
		return bodyTypes.isEmpty() && modelRanges.isEmpty() && series.isEmpty() && models.isEmpty();
	}
}
