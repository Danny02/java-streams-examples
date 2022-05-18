package dev.nullzwo.enrich.poc.domain.model;

public enum BrandRange {
	BMW_CAR("bmwCar"),
	BMW_MOTORCYCLE("bmwBike"),
	MINI_CAR("mini"),
	BMWI_CAR("bmwi"),
	ALPINA_CAR("alpinaCar");

	private String value;

	BrandRange(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
