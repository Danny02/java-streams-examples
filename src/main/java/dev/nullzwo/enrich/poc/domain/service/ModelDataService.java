package dev.nullzwo.enrich.poc.domain.service;

import dev.nullzwo.enrich.poc.domain.model.*;
import dev.nullzwo.enrich.poc.domain.spi.ModelDataPort;
import io.vavr.control.Option;

import java.time.LocalDate;
import java.util.Locale.IsoCountryCode;

public class ModelDataService {

	public record AlternativeModelIdentifier(AgModelCode agModelCode, LocalDate productionDate) {
	}

	private final ModelDataPort modelDataPort;

	public ModelDataService(ModelDataPort modelDataPort) {
		this.modelDataPort = modelDataPort;
	}

	public Option<VehicleModel> getModelData(BrandRange brandRange, IsoCountryCode countryCode,
											 VgModelCode vgModelCode, Option<AlternativeModelIdentifier> alternativeId) {
		var data = modelDataPort.getModelData(brandRange, countryCode);

		return data.get(vgModelCode).orElse(() ->
				alternativeId.flatMap(aid ->
						data.values()
								.filter(v -> aid.agModelCode().equals(v.agModelCode()))
								.filter(v -> v.configurationDates().effectDate().isInRage(aid.productionDate))
								.headOption()
				)
		);
	}

	public String getMarketingEngineName(VehicleModel model) {
		return ClusterDeriverUtil.deriveMarketEngine(model.derivative()).marketingEngineName();
	}
}
