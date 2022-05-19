package dev.nullzwo.enrich.poc.domain.spi;

import dev.nullzwo.enrich.poc.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Locale.IsoCountryCode;

public interface DataRefreshPort {
	record PhraseRefreshData(BrandRange brandRange, List<Locale> locales, LocalDate validity) {}

	void refreshModelDescription(PhraseRefreshData refreshData, VgModelCode vgModelCode);
	void refreshModelRangeDescription(PhraseRefreshData refreshData, ModelRange modelRange);
	void refreshSeriesDescription(PhraseRefreshData refreshData, Series series);
	void refreshBodyTypeDescription(PhraseRefreshData refreshData, BodyType bodyType);

	void refreshModelData(BrandRange brandRange, IsoCountryCode countryCode, VgModelCode vgModelCode);
	void refreshModelData(BrandRange brandRange, IsoCountryCode countryCode, AgModelCode agModelCode, LocalDate productionDate);
}
