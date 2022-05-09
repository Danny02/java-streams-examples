package dev.nullzwo.enrich.experiment1;

import java.util.UUID;

public class Domain {

	public enum Country {
		UNKOWN, GERMANY, CHINA, USA;
	}

	public record VehicleId(UUID id) {
	}

	public record DealerId(UUID id) {
	}

	public interface VehicleEvent {
	}

	public record BaseDateChanged(String vin, DealerId dealerId, Country country) implements VehicleEvent {
	}

	public record DealerChanged(DealerId from, DealerId to) implements VehicleEvent {
	}

	public record PriceChanged(double price) implements VehicleEvent {
	}
}
