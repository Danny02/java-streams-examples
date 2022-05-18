package dev.nullzwo.enrich.poc.domain.service;

import dev.nullzwo.enrich.poc.domain.model.*;
import dev.nullzwo.enrich.poc.domain.spi.ModelDataPort;

import java.time.LocalDate;
import java.util.Locale.IsoCountryCode;
import java.util.Optional;

public class ModelDataService {

	public record AlternativeModelIdentifier(AgModelCode agModelCode, LocalDate productionDate){}

	private final ModelDataPort modelDataPort;

	public ModelDataService(ModelDataPort modelDataPort) {
		this.modelDataPort = modelDataPort;
	}

	public Optional<VehicleModel> getModelData(BrandRange brandRange, IsoCountryCode countryCode,
											   VgModelCode vgModelCode, Optional<AlternativeModelIdentifier> alternativeId) {
		var data = modelDataPort.getModelData(brandRange, countryCode);

		return Optional.ofNullable(data.get(vgModelCode)).or(() ->
				alternativeId.flatMap(aid ->
						data.values()
								.stream()
								.filter(v -> aid.agModelCode().equals(v.agModelCode()))
								.filter(v -> v.configurationDates().effectDate().isInRage(aid.productionDate))
								.findFirst()
				)
		);

	}
}
