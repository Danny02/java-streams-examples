package dev.nullzwo.enrich.poc.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.List;

public record VehicleModel(
		AgModelCode agModelCode,
		ConfigurationDates configurationDates,
		String series,
		Option<String> modelRange,
		String bodyType,
		String derivative,
		String steering,
		//TODO why doubling of order/effect date from timeslice and in configuration dates
		DateRange orderDate,
		DateRange effectDate
//		@JsonProperty("fuelType")
//		private String fuelType;
//		@JsonProperty("series")
//		private String series;
//		@JsonProperty("technicalData")
//		private TechnicalData technicalData;
//		@JsonProperty("bodyStyle")
//		private String bodyStyle;
//		@JsonProperty("modelRange")
//		private String modelRange;
//		@JsonProperty("transmission")
//		private String transmission;
//		@JsonProperty("driveType")
//		private String driveType;
//		@JsonProperty("hybridFlag")
//		private String hybridFlag;
) {
}
