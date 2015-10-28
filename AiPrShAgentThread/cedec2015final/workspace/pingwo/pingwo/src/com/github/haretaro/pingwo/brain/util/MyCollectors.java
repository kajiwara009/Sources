package com.github.haretaro.pingwo.brain.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.aiwolf.common.data.Agent;

public class MyCollectors {
	/**
	 * ランダムに1つ返すコレクター
	 */
	public static final Collector<Agent,List<Agent>,Optional<Agent>> collectRandomly;
	
	static{
		Supplier<List<Agent>> supplier = () -> new ArrayList<Agent>();
		BiConsumer<List<Agent>,Agent> accumulator = (list,agent) -> list.add(agent);
		BinaryOperator<List<Agent>> combiner = (l1,l2) -> {
			l1.addAll(l2);
			return l1;
		};
		Function<List<Agent>,Optional<Agent>> finisher = li -> Optional.ofNullable(Util.randomSelect(li));
		collectRandomly = Collector.of(supplier, accumulator, combiner, finisher);
	}
}
