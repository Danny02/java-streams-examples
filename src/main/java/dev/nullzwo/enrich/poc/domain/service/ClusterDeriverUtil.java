package dev.nullzwo.enrich.poc.domain.service;

import dev.nullzwo.enrich.poc.domain.model.Engine;
import dev.nullzwo.enrich.poc.domain.model.EngineCluster;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.nullzwo.enrich.poc.domain.service.ClusterDeriverUtil.Deriver.matches;
import static dev.nullzwo.enrich.poc.domain.model.EngineCluster.*;
import static dev.nullzwo.enrich.poc.domain.service.MarketingEngineDeriverUtil.*;

public class ClusterDeriverUtil {

	interface MarketingnameDeriver {
		Option<String> derive(String derivedName);
	}

	record Derivation(EngineCluster cluster, String marketingName) {
	}

	interface Deriver {
		Option<Derivation> derive(String derivedName);

		static Deriver matches(String regex, EngineCluster cluster) {
			return matches(regex, cluster, MarketingEngineDeriverUtil::deriveStandardEngineMarketEngine);
		}

		static Deriver matches(String regex, EngineCluster cluster, MarketingnameDeriver deriver) {
			return derivedName -> {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(derivedName);

				return matcher.find() ? Option.of(new Derivation(PLUGIN_HYBRID, deriver.derive(derivedName).getOrElse(derivedName))) : Option.none();
			};
		}
	}

	public static Engine deriveMarketEngine(String name) {
		var derivedName = name.replace(" ", "").strip();

		var derivers = List.<Deriver>of(
				ClusterDeriverUtil::deriveMModelCluster,
				matches("^[0-9][0-9][0-9]E", PLUGIN_HYBRID),
				matches("^[0-9][0-9][0-9]TI", TI_MODEL, n -> deriveTIModelMarketEngine(n)),
				matches("^X[0-9][0-9][A-Z]", X_MODEL_OLD),
				matches("^X[0-9][0-9][0-9][A-Z]", X_MODEL_NEW),
				matches("^Z[0-9][0-9][0-9][A-Z]", Z_MODEL),
				matches("^[0-9][0-9][0-9]L[A-Z]", LONG_VERSION),
				matches("^IX[0-9]", IX_MODEL, n -> deriveOldXModelMarketEngine(n)),
				matches("^[0-9][0-9][0-9][A-Z]ED", EFFICIENT_DYNAMICS, n -> deriveEfficientDynamicsMarketEngine(n)),
				matches("^S[0-9][0-9][A-Z]", OLD_X1_E84, n -> deriveOldX1E84MarketEngine(n)),
				matches("^[0-9][0-9][0-9]XE", ALL_WHEEL_DRIVE_PLUS_HYBRID, n -> deriveAllWheelDriveMarketEngine(n)),
				matches("^X3$", OLD_X3_MODEL, n -> deriveOldXModelMarketEngine(n)),
				matches("^X5$", OLD_X5_MODEL, n -> deriveOldXModelMarketEngine(n)),
				matches("^X6$", OLD_X6_MODEL, n -> deriveOldXModelMarketEngine(n)),
				ClusterDeriverUtil::deriveStandardEngineCLuster
		);

		var derived = derivers.foldLeft(Option.<Derivation>none(), (first, second) -> first.orElse(() -> second.derive(derivedName)));

		return derived.map(d -> new Engine(derivedName, d.marketingName, null, true, true, d.cluster))
				.getOrElse(new Engine(derivedName, name, null, false, false, null));
	}

	private static Option<Derivation> deriveMModelCluster(String derivedName) {
		Pattern pattern = Pattern.compile("^M|^X[0-9]M|^Z[0-9]M");
		Matcher matcher = pattern.matcher(derivedName);
		if (matcher.find()) {
			if (derivedName.contains("COMP") || derivedName.contains("CS") || derivedName.length() < 5) {
				return Option.of(new Derivation(M_MODEL, deriveOldXModelMarketEngine(derivedName).getOrElse(derivedName)));
			} else {
				return Option.of(new Derivation(M_PERFORMANCE, deriveMPerformanceMarketEngine(derivedName).getOrElse(derivedName)));
			}
		}
		return Option.none();
	}


	private static Option<Derivation> deriveStandardEngineCLuster(String derivedName) {
		if (!derivedName.contains("ED")) {
			Pattern pattern = Pattern.compile("^[0-9][0-9][I|D]");
			Pattern pattern1 = Pattern.compile("^[0-9][0-9][0-9][I|D]");
			Matcher matcher = pattern.matcher(derivedName);
			Matcher matcher1 = pattern1.matcher(derivedName);
			if (matcher.find() || matcher1.find()) {
				return Option.of(new Derivation(STANDARD_ENGINE, deriveStandardEngineMarketEngine(derivedName).getOrElse(derivedName)));
			}
		}
		return Option.none();
	}

}
