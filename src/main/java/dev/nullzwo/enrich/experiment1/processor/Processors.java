package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.*;

import java.util.List;

import static dev.nullzwo.enrich.experiment1.processor.EventProcessor.process;
import static dev.nullzwo.enrich.experiment1.processor.EventProcessor.projection;

public class Processors {

	static final EventProcessor<Void> resetPrice = process(DealerChanged.class, e -> new PriceChanged(0));

	static final EventProcessor<Long> eventsPerCar = projection(0L, (count, e) -> count + 1L);

	static final EventProcessor<DealerId> detectDealerChanges = new StatefullEventProcessor<>(null, (s, e) -> {
		var next = s;
		var events = List.<VehicleEvent>of();
		if (e instanceof BaseDateChanged bdc) {
			next = bdc.dealerId();
			if (!next.equals(s)) {
				events = List.of(new DealerChanged(s, next));
			}
		}
		return new Result<>(next, events);
	});

}
