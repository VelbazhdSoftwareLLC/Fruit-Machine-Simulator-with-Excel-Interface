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
	static final Symbol NO_SYMBOL = new Symbol();

	/* Static initializer. */
	static {
		NO_SYMBOL.index = -1;
		NO_SYMBOL.name = "";
		NO_SYMBOL.type = Symbol.Type.NONE;
		NO_SYMBOL.pays = new int[0];
		NO_SYMBOL.image = null;
	}
}
