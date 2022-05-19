package dev.nullzwo.enrich.poc.domain.service;

import io.vavr.control.Option;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketingEngineDeriverUtil {
	private static Option<String> lastN(String s, int n) {
		return s.length() < n ? Option.none() : Option.of(s.substring(s.length() - n));
	}

	private static String dropLast(String s, int n) {
		return s.substring(0, s.length() - n);
	}

	static Option<String> deriveStandardEngineMarketEngine(String derivedName) {
		return lastN(derivedName, 3);
	}

	static Option<String> deriveEfficientDynamicsMarketEngine(String derivedName) {
		return lastN(dropLast(derivedName, 2).strip(), 3);
	}

	static Option<String> deriveTIModelMarketEngine(String derivedName) {
		return lastN(derivedName, 4);
	}

	static Option<String> deriveAllWheelDriveMarketEngine(String derivedName) {
		return lastN(derivedName.replace("X", ""), 3);
	}

	static Option<String> deriveLongVersionMarketEngine(String derivedName) {
		return lastN(derivedName.replace("L", ""), 3);
	}

	static Option<String> deriveMPerformanceMarketEngine(String derivedName) {
		String engineName = derivedName.replace(" ", "");
		if (engineName.startsWith("M")) {
			var n = engineName.contains("L") ? 4 : 3;
			return lastN(derivedName, n).map(s -> "M" + s);
		} else if (engineName.startsWith("X") || engineName.startsWith("Z")) {
			return lastN(engineName, 4);
		}
		return Option.none();
	}

	static Option<String> deriveOldX1E84MarketEngine(String derivedName) {
		String engineName = derivedName.replace("( |ED)", "").strip();
		return lastN(engineName, 3);
	}

	//No change on derivative
	static Option<String> deriveOldXModelMarketEngine(String derivedName) {
		return Option.of(derivedName);
	}

}
