package dev.nullzwo.enrich.poc.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public record VehicleModel(
		AgModelCode agModelCode,
		ConfigurationDates configurationDates
//		@JsonProperty("fuelType")
//		private String fuelType;
//				@JsonProperty("modelCode")
//		private String modelCode;
//		@JsonProperty("agModelCode")
//		private String agModelCode;
//		@JsonProperty("steering")
//		private String steering;
//		@JsonProperty("series")
//		private String series;
//		@JsonProperty("configurationDates")
//		private ConfigurationDates configurationDates;
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
//		@JsonProperty("derivative")
//		private String derivative;
//		@JsonProperty("timeslices")
//		private List<Timeslice> timeslices = new ArrayList<>();
) {
}
