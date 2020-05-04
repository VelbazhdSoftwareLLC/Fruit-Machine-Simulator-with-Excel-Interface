package eu.veldsoft.slot.simulator;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Utilities used in many other classes.
 * 
 * @author Todor Balabanov
 */
final class Util {
	/** Pseudo-random number generator. */
	static final RandomGenerator PRNG = new MersenneTwister();

	/** Index of the none symbol in the array of symbols. */
	static final int NO_SYMBOL_INDEX = -1;
}
