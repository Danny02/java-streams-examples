package dev.nullzwo.enrich.poc.domain.model;

import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;

public enum Brand {
	BMW,
	BMWi,
	ROLLS_ROYCE,
	MINI,
	ALPINA,
	OTHER;

	public Option<BrandRange> toBrandRange(ProductType productType) {
		return switch (this) {
			case BMW -> of(ProductType.MOTORCYCLE.equals(productType) ? BrandRange.BMW_MOTORCYCLE : BrandRange.BMW_CAR);
			case BMWi -> of(BrandRange.BMWI_CAR);
			case MINI -> of(BrandRange.MINI_CAR);
			case ALPINA -> of(BrandRange.ALPINA_CAR);
			default -> none();
		};
	}
}
