package dev.nullzwo.enrich.poc.domain.model;

import io.vavr.control.Option;

import java.time.LocalDate;

public record BasePod(
		Option<Brand> brand,
		Option<ProductType> productType,
		VgModelCode vgModelCode,
		Option<AgModelCode> agModelCode,
		Option<LocalDate> productionDate
) {

	public Option<BrandRange> getBrandRange() {
		return productType.flatMap(pt -> brand.flatMap(b -> b.toBrandRange(pt)));
	}
}
