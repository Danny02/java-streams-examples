package dev.nullzwo.enrich.poc.domain.spi;

import dev.nullzwo.enrich.poc.domain.model.BrandRange;
import dev.nullzwo.enrich.poc.domain.model.VehicleModel;
import dev.nullzwo.enrich.poc.domain.model.VgModelCode;

import java.util.Locale.IsoCountryCode;
import java.util.Map;

public interface ModelDataPort {
	Map<VgModelCode, VehicleModel> getModelData(BrandRange brandRange, IsoCountryCode countryCode);
}
