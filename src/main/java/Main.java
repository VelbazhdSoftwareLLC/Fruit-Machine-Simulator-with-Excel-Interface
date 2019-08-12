/*==============================================================================
*                                                                              *
* Fruit Machine Simulator with Excel Interface version 1.0.0                   *
* Copyrights (C) 2017-2019 Velbazhd Software LLC                               *
*                                                                              *
* developed by Todor Balabanov ( todor.balabanov@gmail.com )                   *
* Sofia, Bulgaria                                                              *
*                                                                              *
* This program is free software: you can redistribute it and/or modify         *
* it under the terms of the GNU General Public License as published by         *
* the Free Software Foundation, either version 3 of the License, or            *
* (at your option) any later version.                                          *
*                                                                              *
* This program is distributed in the hope that it will be useful,              *
* but WITHOUT ANY WARRANTY; without even the implied warranty of               *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                *
* GNU General Public License for more details.                                 *
*                                                                              *
* You should have received a copy of the GNU General Public License            *
* along with this program. If not, see <http://www.gnu.org/licenses/>.         *
*                                                                              *
==============================================================================*/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Application single entry point class.
 * 
 * @author Todor Balabanov
 */
public class Main {

	/** Pseudo-random number generator. */
	private static final RandomGenerator PRNG = new MersenneTwister();

	/** Index of the none symbol in the array of symbols. */
	private static final int NO_SYMBOL_INDEX = -1;

	/** Index of the scatter symbol in the array of symbols. */
	private static final Set<Integer> SCATTER_INDICES = new HashSet<Integer>();

	/** Index of the wild symbol in the array of symbols. */
	private static final Set<Integer> WILD_INDICES = new HashSet<Integer>();

	/** Index of the extend wild symbol in the array of symbols. */
	private static final Set<Integer> EXTEND_WILD_INDICES = new HashSet<Integer>();

	/** List of symbols names. */
	private static final List<String> SYMBOLS_NAMES = new ArrayList<String>();

	/** List of symbols names. */
	private static final List<Integer> SYMBOLS_NUMBERS = new ArrayList<Integer>();

	/** Slot game pay table. */
	private static int[][] paytable = {};

	/** Lines combinations. */
	private static int[][] lines = {};

	/** Target RTP percent. */
	private static double targetRtp = 0;

	/** Stips in the base game as symbols names. */
	private static String[][] baseStrips = {};

	/** Stips in the free spins as symbols names. */
	private static String[][] freeStrips = {};

	/** Stips in base game. */
	private static int[][] baseReels = null;

	/** Stips in free spins. */
	private static int[][] freeReels = null;

	/**
	 * Use reels stops in brute force combinations generation and collapse
	 * feature.
	 */
	private static int[] reelsStops = {};

	/** Current visible symbols on the screen. */
	private static int[][] view = {};

	/** Cells on the screen which took part of the wins. */
	private static boolean[][] winners = {};

	/** Current free spins multiplier. */
	private static int freeGamesMultiplier = 0;

	/** If wild is presented in the line multiplier. */
	private static int wildInLineMultiplier = 0;

	/** If scatter win is presented on the screen. */
	private static int scatterMultiplier = 0;

	/** Total bet in single base game spin. */
	private static int singleLineBet = 0;

	/** Total bet in single base game spin. */
	private static int totalBet = 0;

	/** Free spins to be played. */
	private static int freeGamesNumber = 0;

	/** Total amount of won money. */
	private static long wonMoney = 0L;

	/** Total amount of lost money. */
	private static long lostMoney = 0L;

	/** Total amount of won money in base game. */
	private static long baseMoney = 0L;

	/**
	 * All values as win in the base game (even zeros) for the whole simulation.
	 */
	private static List<Integer> baseOutcomes = new ArrayList<Integer>();

	/** Total amount of won money in free spins. */
	private static long freeMoney = 0L;

	/**
	 * All values as win in the free spins (even zeros) for the whole
	 * simulation.
	 */
	private static List<Integer> freeOutcomes = new ArrayList<Integer>();

	/** Max amount of won money in base game. */
	private static long baseMaxWin = 0L;

	/** Max amount of won money in free spins. */
	private static long freeMaxWin = 0L;

	/** Total number of base games played. */
	private static long totalNumberOfGames = 0L;

	/** Total number of free spins played. */
	private static long totalNumberOfFreeGames = 0L;

	/** Total number of free spins started. */
	private static long totalNumberOfFreeGameStarts = 0L;

	/** Total number of free spins started. */
	private static long totalNumberOfFreeGameRestarts = 0L;

	/** Maximum number of free games in a single start. */
	private static int maxSingleRunFreeGames = 0;

	/** Maximum number of collapses in a single start. */
	private static int maxCollapses = 0;

	/** Hit rate of wins in base game. */
	private static long baseGameHitRate = 0L;

	/** Hit rate of wins in free spins. */
	private static long freeGamesHitRate = 0L;

	/** Verbose output flag. */
	private static boolean verboseOutput = false;

	/** Free spins flag. */
	private static boolean freeOff = false;

	/** Wild substitution flag. */
	private static boolean wildsOff = false;

	/** Burning Hot style of wild expansion flag. */
	private static boolean burningHotWilds = false;

	/** Lucky & Wild style of wild expansion flag. */
	private static boolean luckyAndWildWilds = false;

	/** Lucky Lady's Charm style of simulation flag. */
	private static boolean luckyLadysCharm = false;

	/** Age of Troy style of simulation flag. */
	private static boolean ageOfTroy = false;

	/** 20 Hot Blast style of simulation flag. */
	private static boolean twentyHotBlast = false;

	/** Brute force all winning combinations in base game only flag. */
	private static boolean bruteForce = false;

	/** Size of the first bin in the histogram. */
	private static int initialBin = 1;

	/** Increment used for next bin in the histogram. */
	private static int binIncrement = 0;

	/** Number of allowed stack repeats in the shuffling process. */
	private static int numberOfAllowedStackRepeats = 1;

	/** Symbols win hit rate in base game. */
	private static long[][] baseSymbolMoney = {};

	/** Symbols hit rate in base game. */
	private static long[][] baseGameSymbolsHitRate = {};

	/** Symbols win hit rate in base game. */
	private static long[][] freeSymbolMoney = {};

	/** Symbols hit rate in base game. */
	private static long[][] freeGameSymbolsHitRate = {};

	/** Distribution of the wins according their amount in the base game. */
	private static Map<Integer, Long> baseWinsHistogram = new HashMap<Integer, Long>();

	/** Distribution of the wins according their amount in the free spins. */
	private static Map<Integer, Long> freeWinsHistogram = new HashMap<Integer, Long>();

	/**
	 * Calculate all combinations in base game.
	 * 
	 * @return Total number of combinations in the base game.
	 */
	private static long baseGameNumberOfCombinations() {
		/*
		 * Minus one is needed in order first combination to start from zeros in
		 * brute force calculations.
		 */
		reelsStops = new int[baseReels.length];
		for (int i = 1; i < reelsStops.length; i++) {
			reelsStops[i] = 0;
		}
		reelsStops[0] = -1;

		long result = 1;
		for (int i = 0; i < baseReels.length; i++) {
			result *= baseReels[i].length;
		}

		return result;
	}

	/**
	 * Data initializer.
	 */
	private static void initialize() {
		/* Transform symbols names to integer values. */
		baseReels = new int[baseStrips.length][];
		for (int i = 0; i < baseStrips.length; i++) {
			baseReels[i] = new int[baseStrips[i].length];
			for (int j = 0; j < baseStrips[i].length; j++) {
				for (int s = 0; s < SYMBOLS_NAMES.size(); s++) {
					if (SYMBOLS_NAMES.get(s).trim()
							.equals(baseStrips[i][j].trim()) == true) {
						baseReels[i][j] = s;
						break;
					}
				}
			}
		}

		/* Transform symbols names to integer values. */
		freeReels = new int[freeStrips.length][];
		for (int i = 0; i < freeStrips.length; i++) {
			freeReels[i] = new int[freeStrips[i].length];
			for (int j = 0; j < freeStrips[i].length; j++) {
				for (int s = 0; s < SYMBOLS_NAMES.size(); s++) {
					if (SYMBOLS_NAMES.get(s).trim()
							.equals(freeStrips[i][j].trim()) == true) {
						freeReels[i][j] = s;
						break;
					}
				}
			}
		}

		/* Initialize view with no symbols. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				view[i][j] = NO_SYMBOL_INDEX;
			}
		}

		/* Adjust multipliers. */
		singleLineBet = 1;

		/* Calculate total bet. */
		totalBet = singleLineBet * lines.length;

		/* Allocate memory for the counters. */
		baseSymbolMoney = new long[paytable.length][SYMBOLS_NAMES.size()];
		baseGameSymbolsHitRate = new long[paytable.length][SYMBOLS_NAMES
				.size()];
		freeSymbolMoney = new long[paytable.length][SYMBOLS_NAMES.size()];
		freeGameSymbolsHitRate = new long[paytable.length][SYMBOLS_NAMES
				.size()];
		// TODO Counters should be initialized with zeros.

		baseOutcomes.clear();
		freeOutcomes.clear();
	}

	/**
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels
	 *            Reels strips.
	 */
	private static void nextCombination(int[] reelsStops) {
		reelsStops[0] += 1;

		/* Handle all reels one by one. */
		for (int i = 0; i < reelsStops.length; i++) {
			/* Do nothing if the edge of the reel is not reached. */
			if (reelsStops[i] < baseReels[i].length) {
				continue;
			}

			/* Put the reel in starting position. */
			reelsStops[i] = 0;

			/* Move next reel with one position. */
			if (i < reelsStops.length - 1) {
				reelsStops[i + 1] += 1;
			}
		}
	}

	/**
	 * Clear flags for the cells used in wins formation.
	 * 
	 * @param winners
	 *            Flags of the cells.
	 */
	private static void clear(boolean winners[][]) {
		for (int i = 0; i < winners.length; i++) {
			for (int j = 0; j < winners[i].length; j++) {
				winners[i][j] = false;
			}
		}
	}

	/**
	 * If there is a win do collapse the cells took part in the win.
	 * 
	 * @param view
	 *            Screen view.
	 * @param winners
	 *            Cells took part in the win.
	 * @param reels
	 *            Reels used for the symbols replacement.
	 * @param stops
	 *            Positions where reels were stopped.
	 */
	private static void collapse(int view[][], boolean winners[][],
			int reels[][], int stops[]) {
		/* Clear symbols which was part of the total win. */
		for (int i = 0; i < winners.length; i++) {
			for (int j = 0; j < winners[i].length; j++) {
				if (winners[i][j] == false) {
					continue;
				}

				view[i][j] = NO_SYMBOL_INDEX;
			}
		}

		/* Pull down symbols above the holes. */
		for (int i = 0; i < view.length; i++) {
			boolean done = true;

			for (int j = 1; j < view[i].length; j++) {
				/*
				 * Swap empty symbol with the symbol above it and restart reel
				 * checking.
				 */
				if (view[i][j - 1] != NO_SYMBOL_INDEX
						&& view[i][j] == NO_SYMBOL_INDEX) {
					view[i][j] = view[i][j - 1];
					view[i][j - 1] = NO_SYMBOL_INDEX;
					done = false;
				}
			}

			/* Do the reel checking again. */
			if (done == false) {
				i--;
			}
		}

		/* Fill the empty cells. */
		for (int i = 0; i < view.length; i++) {
			for (int j = view[i].length - 1; j >= 0; j--) {
				/* If the cell is not empty do nothing. */
				if (view[i][j] != NO_SYMBOL_INDEX) {
					continue;
				}

				/* Get the symbol above the stop position. */
				stops[i]--;
				if (stops[i] < 0) {
					stops[i] = reels[i].length - 1;
				}

				/* Fill the empty cell. */
				view[i][j] = reels[i][stops[i]];
			}
		}
	}

	/**
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels
	 *            Reels strips.
	 * @param stops
	 *            Positions on which reels were stopped.
	 */
	private static void spin(int[][] reels, int stops[]) {
		/* Spin all reels. */
		for (int i = 0; i < view.length && i < reels.length; i++) {
			int column[] = new int[view[i].length];

			/* Switch between Brute Force and Monte Carlo. */
			if (bruteForce == true) {
				column[0] = reelsStops[i];
			} else {
				column[0] = stops[i] = PRNG.nextInt(reels[i].length);
			}

			/* Fill symbols for the particular column. */
			for (int c = 1; c < column.length; c++) {
				column[c] = (column[0] + c) % reels[i].length;
			}

			/* Copy the column into the view array. */
			for (int j = 0; j < view[i].length; j++) {
				view[i][j] = reels[i][column[j]];
			}
		}
	}

	/**
	 * Calculate win in particular line.
	 *
	 * @param line
	 *            Single line.
	 *
	 * @return Calculated win.
	 */
	private static int[] wildLineWin(int[] line) {
		/* Wild index with counter and win amount. */
		int[][] values = new int[WILD_INDICES.size()][];
		for (int i = 0; i < WILD_INDICES.size(); i++) {
			values[i] = new int[]{(Integer) (WILD_INDICES.toArray()[i]), 0, 0};
		}

		/* If there is no leading wild there is no wild win. */
		if (WILD_INDICES.contains(line[0]) == false) {
			return (new int[]{NO_SYMBOL_INDEX, 0, 0});
		}

		/* Each wild can lead to different level of win. */
		int index = 0;
		for (int j = 0; j < values.length; j++) {
			/* Wild symbol passing to find first regular symbol. */
			for (int i = 0; i < line.length; i++) {
				/* First no wild symbol found. */
				if (line[i] != values[j][0]) {
					break;
				}

				/* Count how long is the wild line. */
				values[j][1]++;
			}

			/* Calculate win marked by line with wilds. */
			values[j][2] = singleLineBet * paytable[values[j][1]][values[j][0]];
			if (values[index][2] < values[j][2]) {
				index = j;
			}
		}

		return (values[index]);
	}

	/**
	 * Calculate win in particular line.
	 *
	 * @param line
	 *            Single line.
	 * @param statistics
	 *            Statistical information output.
	 * @param index
	 *            Line index from the list of the lines.
	 *
	 * @return Calculated win.
	 */
	private static int lineWin(int line[], int statistics[][], int index) {
		/* Scatter can not lead win combination. */
		if (SCATTER_INDICES.contains(line[0]) == true) {
			return 0;
		}

		/* Calculate wild win if there is any. */
		int[] wildWin = wildLineWin(line);

		/* Keep first symbol in the line. */
		int symbol = line[0];

		/* Wild symbol passing to find first regular symbol. */
		for (int i = 0; i < line.length; i++) {
			if (line[i] == NO_SYMBOL_INDEX) {
				break;
			}

			/* Scatter stops the line. */
			if (SCATTER_INDICES.contains(line[i]) == true) {
				break;
			}

			/* First no wild symbol found. */
			if (WILD_INDICES.contains(line[i]) == false) {
				if (SCATTER_INDICES.contains(line[i]) == false) {
					symbol = line[i];
				}

				break;
			}
		}

		/* Line win without wild is multiplied by one. */
		int lineMultiplier = 1;

		/* Wild symbol substitution. */
		for (int i = 0; i < line.length && wildsOff == false; i++) {
			/* Scatter is not substituted. */
			if (SCATTER_INDICES.contains(line[i]) == true) {
				continue;
			}

			/* Only wilds are substituted. */
			if (WILD_INDICES.contains(line[i]) == false) {
				continue;
			}

			/* Substitute wild with regular symbol. */
			line[i] = symbol;

			/* Line win with wild is multiplied by line multiplier. */
			lineMultiplier = wildInLineMultiplier;
		}

		/* Count symbols in winning line. */
		int number = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == symbol) {
				number++;
			} else {
				break;
			}
		}

		/* Clear unused symbols. */
		for (int i = number; i < line.length; i++) {
			line[i] = NO_SYMBOL_INDEX;
		}

		/* Calculate single line win. */
		int win = singleLineBet * paytable[number][symbol] * lineMultiplier;

		/* Adjust the win according wild line information. */
		if (win < wildWin[2]) {
			symbol = wildWin[0];
			number = wildWin[1];
			win = wildWin[2];
		}

		/*
		 * Collect statistics for the scatter wins (symbol count, symbol index,
		 * win).
		 */
		statistics[index] = new int[]{number, symbol, win};

		return (win);
	}

	/**
	 * Initialize an empty line.
	 * 
	 * @param size
	 *            Size of a single line.
	 * 
	 * @return Single line as array with no symbols.
	 */
	private static int[] emptyLine(int size) {
		int[] line = new int[size];

		for (int i = 0; i < line.length; i++) {
			line[i] = NO_SYMBOL_INDEX;
		}

		return line;
	}

	/**
	 * Calculate win in all possible lines.
	 *
	 * @param view
	 *            Symbols visible in screen view.
	 * @param statistics
	 *            Statistical information output.
	 *
	 * @return Calculated win.
	 */
	private static int linesWin(int[][] view, int statistics[][]) {
		int win = 0;

		/* Check wins in all possible lines. */
		for (int l = 0; l < lines.length; l++) {
			/* Initialize an empty line. */
			int[] line = emptyLine(lines[l].length);

			/* Prepare line for combination check. */
			for (int i = 0; i < line.length; i++) {
				int index = lines[l][i];
				line[i] = view[i][index];
			}

			int result = lineWin(line, statistics, l);

			/* Mark cells used in win formation only if there is a win. */
			for (int i = 0; result > 0 && i < line.length
					&& line[i] != NO_SYMBOL_INDEX; i++) {
				int index = lines[l][i];
				winners[i][index] = true;
			}

			/* Accumulate line win. */
			win += result;
		}

		return (win);
	}

	/**
	 * Calculate win from scatters.
	 *
	 * @param view
	 *            Screen with symbols.
	 * @param statistics
	 *            Statistical information output.
	 *
	 * @return Win from scatters.
	 */
	private static int scatterWin(int[][] view, int statistics[][]) {
		/* Create as many counters as many scatters there in the game. */
		Map<Integer, Integer> numberOfScatters = new HashMap<Integer, Integer>();
		for (Integer scatter : SCATTER_INDICES) {
			numberOfScatters.put(scatter, 0);
		}

		/* Count scatters on the screen. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (SCATTER_INDICES.contains(view[i][j]) == true) {
					numberOfScatters.put(view[i][j],
							numberOfScatters.get(view[i][j]) + 1);
				}
			}
		}

		int k = 0;
		int win = 0;
		for (Integer scatter : SCATTER_INDICES) {
			/* Calculate scatter win. */
			int value = 0;
			if (luckyLadysCharm == true) {
				value = paytable[numberOfScatters.get(scatter)][scatter]
						* scatterMultiplier;
			} else {
				value = paytable[numberOfScatters.get(scatter)][scatter]
						* totalBet * scatterMultiplier;
			}

			/* If there is no win do nothing. */
			if (value <= 0) {
				continue;
			}

			/*
			 * Collect statistics for the scatter wins (number of scatters,
			 * scatter index, win).
			 */
			statistics[k++] = new int[]{numberOfScatters.get(scatter), scatter,
					value};

			/* Mark cells used in win formation only if there is a win. */
			for (int i = 0; i < view.length; i++) {
				for (int j = 0; j < view[i].length; j++) {
					if (view[i][j] != scatter) {
						continue;
					}

					winners[i][j] = true;
				}
			}

			/* It is needed if there are more scatter symbols. */
			win += value;
		}

		return (win);
	}

	/**
	 * Setup parameters for free spins mode.
	 */
	private static void freeGamesSetup() {
		if (bruteForce == true) {
			return;
		}

		if (freeOff == true) {
			return;
		}

		/* Calculate number of scatters. */
		int numberOfScatters = 0;
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				// TODO If there are more than one scatter symbol it is not
				// common all of them to trigger free games.
				if (SCATTER_INDICES.contains(view[i][j]) == true) {
					numberOfScatters++;
				}
			}
		}

		/* Adjust number of free spins according Lucky Lady's Charm rules */
		if (luckyLadysCharm == true) {
			/* In base game 3+ scatters turn into free spins. */
			if (numberOfScatters >= 3 && freeGamesNumber == 0) {
				freeGamesNumber = 15;
				totalNumberOfFreeGameStarts++;
			} else if (numberOfScatters >= 3 && freeGamesNumber > 0) {
				freeGamesNumber += 15;
				totalNumberOfFreeGameRestarts++;
			}
		}

		/* Adjust number of free spins according Age of Troy rules */
		if (ageOfTroy == true) {
			/* In base game 3 scatters turn into free spins. */
			if (numberOfScatters == 3 && freeGamesNumber == 0) {
				freeGamesNumber = 12;
				totalNumberOfFreeGameStarts++;
			} else if (numberOfScatters == 3 && freeGamesNumber > 0) {
				freeGamesNumber += 12;
				totalNumberOfFreeGameRestarts++;
			}
		}
	}

	/**
	 * Update histogram information when there is a win.
	 * 
	 * @param histogram
	 *            Histogram array.
	 * @param win
	 *            Win value.
	 */
	private static void updateHistogram(Map<Integer, Long> histogram,
			Integer win) {
		/*
		 * If the win is bigger than array cells available the win is not
		 * counted.
		 */
		if (histogram.containsKey(win) == false) {
			histogram.put(win, 1L);
		} else {
			histogram.put(win, histogram.get(win) + 1L);
		}
	}

	/**
	 * Expand wilds according Burning Hot rules.
	 * 
	 * @param view
	 *            Screen with symbols.
	 */
	private static boolean burningHotSubstitution(int[][] view) {
		boolean result = false;

		/* Check wins in all possible lines. */
		int progress = 0;
		start : for (int l = 0; l < lines.length; l++) {
			/* Initialize an empty line. */
			int[] line = emptyLine(lines[l].length);

			/* Prepare line for combination check. */
			for (int i = 0; i < line.length; i++) {
				int index = lines[l][i];
				line[i] = view[i][index];

				/*
				 * If current symbol is not wild there is no need to check for a
				 * win.
				 */
				int substituent = line[i];
				if (WILD_INDICES.contains(line[i]) == false) {
					continue;
				}

				/*
				 * If current symbol is wild, but there is no win no expansion
				 * is done.
				 */
				if (lineWin(line, new int[lines.length][3], l) <= 0) {
					continue;
				}

				/* Continue to not progressed part of the screen. */
				if (i <= progress) {
					continue;
				}

				/* Flag for substitution. */
				result = true;

				/*
				 * If current symbol is wild and there is a win expansion is
				 * done.
				 */
				for (int j = 0; j < view[i].length; j++) {
					view[i][j] = substituent;
				}

				/*
				 * Checking should start form the real beginning, but with track
				 * of the progressed part.
				 */
				progress = i;
				l = -1;
				continue start;
			}
		}

		return result;
	}

	/**
	 * Expand wilds according Lucky & Wild rules.
	 * 
	 * @param original
	 *            Screen with symbols.
	 */
	private static boolean luckyAndWildSubstitution(int[][] original) {
		boolean result = false;

		/* Deep copy of the view. */
		int[][] view = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			view[i] = new int[original[i].length];
			for (int j = 0; j < original[i].length; j++) {
				view[i][j] = original[i][j];
			}
		}

		// TODO It should not be substituted by this way, but it will be done
		// like this, because of the customer request.
		int substituent = WILD_INDICES.iterator().next();

		/* Expand wilds. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				/* Do nothing if the wild is not extend wild. */
				if (EXTEND_WILD_INDICES.contains(view[i][j]) == false) {
					continue;
				}

				/* Extend wild. */
				for (int k = i - 1; k <= i + 1; k++) {
					for (int l = j - 1; l <= j + 1; l++) {
						/* Check range boundaries. */
						if (k < 0) {
							continue;
						}
						if (l < 0) {
							continue;
						}
						if (k >= view.length) {
							continue;
						}
						if (l >= view[i].length) {
							continue;
						}

						/* Scatters are not substituted. */
						if (SCATTER_INDICES.contains(view[k][l]) == true) {
							continue;
						}

						/* Flag for substitution. */
						result = true;

						/* Substitution. */
						original[k][l] = substituent;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Expand wilds according 20 Hot Blast rules.
	 * 
	 * @param original
	 *            Screen with symbols.
	 */
	private static boolean twentyHotBlastSubstitution(int[][] original) {
		boolean result = false;

		/* Deep copy of the view. */
		int[][] view = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			view[i] = new int[original[i].length];
			for (int j = 0; j < original[i].length; j++) {
				view[i][j] = original[i][j];
			}
		}

		int substituent = WILD_INDICES.iterator().next();

		/* Prepare view for wins checking by expanding the wild. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (view[i][j] != substituent) {
					continue;
				}

				for (int l = 0; l < view[i].length; l++) {
					view[i][l] = substituent;
				}

				break;
			}
		}

		/* Deep copy of the view with the expanded wilds. */
		if (linesWin(view, new int[lines.length][3]) > 0) {
			for (int i = 0; i < view.length; i++) {
				for (int j = 0; j < view[i].length; j++) {
					original[i][j] = view[i][j];
				}
			}
		}

		return result;
	}

	/**
	 * Play single collapse game.
	 * 
	 * @param multiplier
	 *            Collapse round win multiplier.
	 * @param stops
	 *            Positions in which the reels are stopped.
	 * 
	 * @return Won amount.
	 */
	private static int singleCollapseGame(int multiplier, int stops[]) {
		collapse(view, winners, baseReels, stops);

		/* Win accumulated by lines. */
		int[][] linesStatistics = new int[lines.length][3];
		int[][] scatterStatistics = new int[SCATTER_INDICES.size()][3];
		int win = linesWin(view, linesStatistics)
				+ scatterWin(view, scatterStatistics);

		/* Collect statistics for the lines win. */
		for (int statistics[] : linesStatistics) {
			if (statistics[2] <= 0) {
				continue;
			}

			baseSymbolMoney[statistics[0]][statistics[1]] += multiplier
					* statistics[2];
			baseGameSymbolsHitRate[statistics[0]][statistics[1]]++;
		}

		/* Collect statistics for the scatters win. */
		for (int statistics[] : scatterStatistics) {
			if (statistics[2] <= 0) {
				continue;
			}

			baseSymbolMoney[statistics[0]][statistics[1]] += multiplier
					* statistics[2];
			baseGameSymbolsHitRate[statistics[0]][statistics[1]]++;
		}

		/* There is collapse multiplier. */
		win *= multiplier;

		/*
		 * Keep values for mathematical expectation and standard deviation
		 * calculation.
		 */
		baseOutcomes.add(win);

		/* Add win to the statistics. */
		baseMoney += win;
		wonMoney += win;
		if (baseMaxWin < win) {
			baseMaxWin = win;
		}

		/* Count base game hit rate. */
		if (win > 0) {
			baseGameHitRate++;
		}

		/* Count in the histogram. */
		if (win > 0) {
			updateHistogram(baseWinsHistogram, win);
		}

		return win;
	}

	/**
	 * Play single free spin game.
	 */
	private static void singleFreeGame() {
		if (bruteForce == true) {
			return;
		}

		if (freeOff == true) {
			return;
		}

		/* Spin reels. */
		clear(winners);
		spin(freeReels, new int[freeReels.length]);

		/* Win accumulated by lines. */
		int[][] linesStatistics = new int[lines.length][3];
		int[][] scatterStatistics = new int[SCATTER_INDICES.size()][3];
		int win = linesWin(view, linesStatistics)
				+ scatterWin(view, scatterStatistics);
		win *= freeGamesMultiplier;

		/* Collect statistics for the lines win. */
		for (int statistics[] : linesStatistics) {
			if (statistics[2] <= 0) {
				continue;
			}

			freeSymbolMoney[statistics[0]][statistics[1]] += statistics[2]
					* freeGamesMultiplier;
			freeGameSymbolsHitRate[statistics[0]][statistics[1]]++;
		}

		/* Collect statistics for the scatters win. */
		for (int statistics[] : scatterStatistics) {
			if (statistics[2] <= 0) {
				continue;
			}

			freeSymbolMoney[statistics[0]][statistics[1]] += statistics[2]
					* freeGamesMultiplier;
			freeGameSymbolsHitRate[statistics[0]][statistics[1]]++;
		}

		/*
		 * Keep values for mathematical expectation and standard deviation
		 * calculation.
		 */
		freeOutcomes.add(win);

		/* Add win to the statistics. */
		freeMoney += win;
		wonMoney += win;
		if (freeMaxWin < win) {
			freeMaxWin = win;
		}

		/* Count free games hit rate. */
		if (win > 0) {
			freeGamesHitRate++;
		}

		/* Count in the histogram. */
		if (win > 0) {
			updateHistogram(freeWinsHistogram, win);
		}

		/* Check for free games. */
		freeGamesSetup();
	}

	/**
	 * Play single base game.
	 */
	private static void singleBaseGame() {
		/* In brute force mode reels stops are not random. */
		if (bruteForce == true) {
			nextCombination(reelsStops);
		}

		/* Spin is working even in brute force mode. */
		clear(winners);
		int stops[] = new int[baseReels.length];
		spin(baseReels, stops);
		// /*DEBUG*/ printView(System.err);
		// /*DEBUG*/ System.err.println();

		/* Do Burning Hot style wilds expansion. */
		if (burningHotWilds == true) {
			burningHotSubstitution(view);
		}

		/* Do Lucky & Wild style wilds expansion. */
		if (luckyAndWildWilds == true) {
			luckyAndWildSubstitution(view);
		}

		/* Do 20 Hot Blast style wilds expansion. */
		if (twentyHotBlast == true) {
			twentyHotBlastSubstitution(view);
		}

		/* Win accumulated by lines. */
		int[][] linesStatistics = new int[lines.length][3];
		int[][] scatterStatistics = new int[SCATTER_INDICES.size()][3];
		int win = linesWin(view, linesStatistics)
				+ scatterWin(view, scatterStatistics);

		/* Collect statistics for the lines win. */
		for (int statistics[] : linesStatistics) {
			if (statistics[2] <= 0) {
				continue;
			}

			baseSymbolMoney[statistics[0]][statistics[1]] += statistics[2];
			baseGameSymbolsHitRate[statistics[0]][statistics[1]]++;
		}

		/* Collect statistics for the scatters win. */
		for (int statistics[] : scatterStatistics) {
			if (statistics[2] <= 0) {
				continue;
			}

			baseSymbolMoney[statistics[0]][statistics[1]] += statistics[2];
			baseGameSymbolsHitRate[statistics[0]][statistics[1]]++;
		}

		/*
		 * Keep values for mathematical expectation and standard deviation
		 * calculation.
		 */
		baseOutcomes.add(win);

		/* Add win to the statistics. */
		baseMoney += win;
		wonMoney += win;
		if (baseMaxWin < win) {
			baseMaxWin = win;
		}

		/* Count base game hit rate. */
		if (win > 0) {
			baseGameHitRate++;
		}

		/* Count in the histogram. */
		if (win > 0) {
			updateHistogram(baseWinsHistogram, win);
		}

		/* Run extra wins after cells collapse in 20 Hot Blast mode. */
		int counter = 1;
		int multiplier = 2;
		while (twentyHotBlast == true && win > 0) {
			win = singleCollapseGame(multiplier, stops);

			/* Each collapse rise the multiplier by one. */
			multiplier++;
			counter++;
		}

		/* Keep track of collapses retriggering. */
		if (counter > maxCollapses) {
			maxCollapses = counter;
		}

		/* Check for free games. */
		freeGamesSetup();

		/* Play all free games. */
		int singleRunFreeGames = 0;
		while (freeGamesNumber > 0) {
			totalNumberOfFreeGames++;

			singleFreeGame();
			singleRunFreeGames++;

			freeGamesNumber--;
		}
		if (singleRunFreeGames > maxSingleRunFreeGames) {
			maxSingleRunFreeGames = singleRunFreeGames;
		}
	}

	/**
	 * Print about information.
	 */
	private static void printAbout() {
		System.out.println(
				"*******************************************************************************");
		System.out.println(
				"* Fruit Machine Simulator with Excel Interface version 0.0.1                  *");
		System.out.println(
				"* Copyrights (C) 2017-2019 Velbazhd Software LLC                              *");
		System.out.println(
				"*                                                                             *");
		System.out.println(
				"* developed by Todor Balabanov ( todor.balabanov@gmail.com )                  *");
		System.out.println(
				"* Sofia, Bulgaria                                                             *");
		System.out.println(
				"*                                                                             *");
		System.out.println(
				"* This program is free software: you can redistribute it and/or modify        *");
		System.out.println(
				"* it under the terms of the GNU General Public License as published by        *");
		System.out.println(
				"* the Free Software Foundation, either version 3 of the License, or           *");
		System.out.println(
				"* (at your option) any later version.                                         *");
		System.out.println(
				"*                                                                             *");
		System.out.println(
				"* This program is distributed in the hope that it will be useful,             *");
		System.out.println(
				"* but WITHOUT ANY WARRANTY; without even the implied warranty of              *");
		System.out.println(
				"* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *");
		System.out.println(
				"* GNU General Public License for more details.                                *");
		System.out.println(
				"*                                                                             *");
		System.out.println(
				"* You should have received a copy of the GNU General Public License           *");
		System.out.println(
				"* along with this program. If not, see <http://www.gnu.org/licenses/>.        *");
		System.out.println(
				"*                                                                             *");
		System.out.println(
				"*******************************************************************************");
	}

	/**
	 * Print all simulation input data structures.
	 */
	private static void printDataStructures() {
		System.out.println("Symbols:");
		int size = Math.min(SYMBOLS_NAMES.size(), SYMBOLS_NUMBERS.size());
		System.out.println("Name\tIndex\tType");
		for (int i = 0; i < size; i++) {
			System.out.print(SYMBOLS_NAMES.get(i) + "\t");
			System.out.print(SYMBOLS_NUMBERS.get(i) + "\t");

			if (SCATTER_INDICES.contains(SYMBOLS_NUMBERS.get(i)) == true) {
				System.out.print("Scatter");
			} else if (EXTEND_WILD_INDICES
					.contains(SYMBOLS_NUMBERS.get(i)) == true) {
				System.out.print("Extended");
			} else if (WILD_INDICES.contains(SYMBOLS_NUMBERS.get(i)) == true) {
				System.out.print("Wild");
			} else {
				System.out.print("Regular");
			}

			System.out.println();
		}
		System.out.println();

		System.out.println("Paytable:");
		for (int i = 0; i < paytable.length; i++) {
			System.out.print("\t" + i + " of");
		}
		System.out.println();
		for (int j = 0; j < paytable[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < paytable.length; i++) {
				System.out.print(paytable[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();

		/* Visualize with stars and O letter. */
		System.out.println("Lines:");
		for (int j = 0; j < view[0].length; j++) {
			for (int l = 0; l < lines.length; l++) {
				for (int i = 0; i < lines[l].length; i++) {
					if (j == lines[l][i]) {
						System.out.print("*");
					} else {
						System.out.print("O");
					}
				}
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println();

		/* Vertical print of the reels. */ {
			int max = 0;
			for (int i = 0; baseReels != null && i < baseReels.length; i++) {
				if (max < baseReels[i].length) {
					max = baseReels[i].length;
				}
			}
			System.out.println("Base Game Reels:");
			for (int j = 0; baseReels != null && j < max; j++) {
				for (int i = 0; i < baseReels.length; i++) {
					if (j < baseReels[i].length) {
						System.out.print(SYMBOLS_NAMES.get(baseReels[i][j]));
					}
					System.out.print("\t");
				}
				System.out.print("\t");
				for (int i = 0; i < baseReels.length; i++) {
					if (j < baseReels[i].length) {
						System.out.print(SYMBOLS_NUMBERS.get(baseReels[i][j]));
					}
					System.out.print("\t");
				}
				System.out.println();
			}
			System.out.println();
		}

		/* Vertical print of the reels. */ {
			int max = 0;
			for (int i = 0; freeReels != null && i < freeReels.length; i++) {
				if (max < freeReels[i].length) {
					max = freeReels[i].length;
				}
			}
			System.out.println("Free Games Reels:");
			for (int j = 0; freeReels != null && j < max; j++) {
				for (int i = 0; i < freeReels.length; i++) {
					if (j < freeReels[i].length) {
						System.out.print(SYMBOLS_NAMES.get(freeReels[i][j]));
					}
					System.out.print("\t");
				}
				System.out.print("\t");
				for (int i = 0; i < freeReels.length; i++) {
					if (j < freeReels[i].length) {
						System.out.print(SYMBOLS_NUMBERS.get(freeReels[i][j]));
					}
					System.out.print("\t");
				}
				System.out.println();
			}
			System.out.println();
		}

		System.out.println("Base Game Reels:");
		/* Count symbols in reels. */ {
			int[][] counters = new int[paytable.length - 1][SYMBOLS_NAMES
					.size()];
			// TODO Counters should be initialized with zeros.
			for (int i = 0; baseReels != null && i < baseReels.length; i++) {
				for (int j = 0; j < baseReels[i].length; j++) {
					counters[i][baseReels[i][j]]++;
				}
			}
			for (int i = 0; baseReels != null && i < baseReels.length; i++) {
				System.out.print("\tReel " + (i + 1));
			}
			System.out.println();
			for (int j = 0; j < SYMBOLS_NAMES.size(); j++) {
				System.out.print(SYMBOLS_NAMES.get(j) + "\t");
				for (int i = 0; i < counters.length; i++) {
					System.out.print(counters[i][j] + "\t");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
			System.out.print("Total:\t");
			long combinations = (baseReels == null) ? 0L : 1L;
			for (int i = 0; i < counters.length; i++) {
				int sum = 0;
				for (int j = 0; j < counters[0].length; j++) {
					sum += counters[i][j];
				}
				System.out.print(sum + "\t");
				if (sum != 0) {
					combinations *= sum;
				}
			}
			System.out.println();
			System.out.println("---------------------------------------------");
			System.out.println("Combinations:\t" + combinations);
		}
		System.out.println();

		System.out.println("Free Games Reels:");
		/* Count symbols in reels. */ {
			int[][] counters = new int[paytable.length - 1][SYMBOLS_NAMES
					.size()];
			// TODO Counters should be initialized with zeros.
			for (int i = 0; freeReels != null && i < freeReels.length; i++) {
				for (int j = 0; j < freeReels[i].length; j++) {
					counters[i][freeReels[i][j]]++;
				}
			}
			for (int i = 0; freeReels != null && i < freeReels.length; i++) {
				System.out.print("\tReel " + (i + 1));
			}
			System.out.println();
			for (int j = 0; j < SYMBOLS_NAMES.size(); j++) {
				System.out.print(SYMBOLS_NAMES.get(j) + "\t");
				for (int i = 0; i < counters.length; i++) {
					System.out.print(counters[i][j] + "\t");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
			System.out.print("Total:\t");
			long combinations = (freeReels == null) ? 0L : 1L;
			for (int i = 0; i < counters.length; i++) {
				int sum = 0;
				for (int j = 0; j < counters[0].length; j++) {
					sum += counters[i][j];
				}
				System.out.print(sum + "\t");
				if (sum != 0) {
					combinations *= sum;
				}
			}
			System.out.println();
			System.out.println("---------------------------------------------");
			System.out.println("Combinations:\t" + combinations);
		}
		System.out.println();
	}

	/**
	 * Print simulation statistics.
	 */
	private static void printStatistics() {
		System.out.println("Won money:\t" + wonMoney);
		System.out.println("Lost money:\t" + lostMoney);
		System.out.println("Total Number of Games:\t" + totalNumberOfGames);
		System.out.println();
		System.out.println("Total RTP:\t"
				+ ((double) wonMoney / (double) lostMoney) + "\t\t"
				+ (100.0D * (double) wonMoney / (double) lostMoney) + "%");
		System.out.println("Base Game RTP:\t"
				+ ((double) baseMoney / (double) lostMoney) + "\t\t"
				+ (100.0D * (double) baseMoney / (double) lostMoney) + "%");
		System.out.println("Free Game RTP:\t"
				+ ((double) freeMoney / (double) lostMoney) + "\t\t"
				+ (100.0D * (double) freeMoney / (double) lostMoney) + "%");
		System.out.println();
		System.out.println("Hit Frequency in Base Game:\t"
				+ ((double) baseGameHitRate / (double) totalNumberOfGames)
				+ "\t\t" + (100.0D * (double) baseGameHitRate
						/ (double) totalNumberOfGames)
				+ "%");
		System.out
				.println(
						"Hit Frequency in Free Game:\t"
								+ ((double) freeGamesHitRate
										/ (double) totalNumberOfFreeGames)
								+ "\t\t"
								+ (100.0D * (double) freeGamesHitRate
										/ (double) totalNumberOfFreeGames)
								+ "%");
		System.out.println("Hit Frequency Base Game into Free Game:\t"
				+ ((double) totalNumberOfFreeGameStarts
						/ (double) totalNumberOfGames)
				+ "\t\t" + (100.0D * (double) (totalNumberOfFreeGameStarts)
						/ (double) totalNumberOfGames)
				+ "%");
		System.out.println("Hit Frequency Free Game into Free Game:\t"
				+ ((double) totalNumberOfFreeGameRestarts
						/ (double) totalNumberOfFreeGameStarts)
				+ "\t\t" + (100.0D * (double) (totalNumberOfFreeGameRestarts)
						/ (double) totalNumberOfFreeGameStarts)
				+ "%");
		System.out.println();

		System.out.println("Max Win in Base Game:\t" + baseMaxWin);
		System.out.println("Max Win in Free Game:\t" + freeMaxWin);
		System.out.println("Max Number of Free Games in Single Run:\t"
				+ maxSingleRunFreeGames);
		System.out.println(
				"Max Number of Collapses in Single Run:\t" + maxCollapses);
		System.out.println();
		System.out.print("Base Game Win Mean:\t");
		/* Mean */ {
			double mean = 0;
			for (Integer value : baseOutcomes) {
				mean += value;
			}
			mean /= baseOutcomes.size() != 0 ? baseOutcomes.size() : 1;
			System.out.println(mean);
		}
		System.out.print("Base Game Win Standard Deviation:\t");
		/* Standard Deviation */ {
			double mean = 0;
			for (Integer value : baseOutcomes) {
				mean += value;
			}
			mean /= baseOutcomes.size() != 0 ? baseOutcomes.size() : 1;

			double deviation = 0;
			for (Integer value : baseOutcomes) {
				deviation += (value - mean) * (value - mean);
			}
			deviation /= baseOutcomes.size() != 0 ? baseOutcomes.size() : 1;
			deviation = Math.sqrt(deviation);
			System.out.println(deviation);
		}
		System.out.print("Free Games Win Mean:\t");
		/* Mean */ {
			double mean = 0;
			for (Integer value : freeOutcomes) {
				mean += value;
			}
			mean /= freeOutcomes.size() != 0 ? freeOutcomes.size() : 1;
			System.out.println(mean);
		}
		System.out.print("Free Games Win Standard Deviation:\t");
		/* Standard Deviation */ {
			double mean = 0;
			for (Integer value : freeOutcomes) {
				mean += value;
			}
			mean /= freeOutcomes.size() != 0 ? freeOutcomes.size() : 1;

			double deviation = 0;
			for (Integer value : freeOutcomes) {
				deviation += (value - mean) * (value - mean);
			}
			deviation /= freeOutcomes.size() != 0 ? freeOutcomes.size() : 1;
			deviation = Math.sqrt(deviation);
			System.out.println(deviation);
		}
		System.out.println();
		System.out.println("Base Game Wins Histogram:");
		/* Histogram. */ {
			for (int bin = initialBin; bin < baseMaxWin; bin += bin
					+ binIncrement) {
				System.out.print("< " + bin + "\t");
			}
			System.out.println();
			for (int left = 0, right = initialBin; right < baseMaxWin; left = right, right += right
					+ binIncrement) {
				double sum = 0;
				for (int value = left; value < right; value++) {
					if (baseWinsHistogram.containsKey(value) == false) {
						continue;
					}
					sum += baseWinsHistogram.get(value);
				}
				System.out.print(sum + "\t");
			}
		}
		System.out.println();
		System.out.println("Free Games Wins Histogram:");
		/* Histogram. */ {
			for (int bin = initialBin; bin < freeMaxWin; bin += bin
					+ binIncrement) {
				System.out.print("< " + bin + "\t");
			}
			System.out.println();
			for (int left = 0, right = initialBin; right < freeMaxWin; left = right, right += right
					+ binIncrement) {
				double sum = 0;
				for (int value = left; value < right; value++) {
					if (freeWinsHistogram.containsKey(value) == false) {
						continue;
					}
					sum += freeWinsHistogram.get(value);
				}
				System.out.print(sum + "\t");
			}
		}
		System.out.println();
		System.out.println();

		System.out.println("Base Game Symbols RTP:");
		System.out.print("\t");
		for (int i = 0; i < baseSymbolMoney.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < baseSymbolMoney[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < baseSymbolMoney.length; i++) {
				System.out.print(
						(double) baseSymbolMoney[i][j] / (double) lostMoney
								+ "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Base Game Symbols Hit Rate:");
		System.out.print("\t");
		for (int i = 0; i < baseGameSymbolsHitRate.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < baseGameSymbolsHitRate[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < baseGameSymbolsHitRate.length; i++) {
				System.out.print((double) baseGameSymbolsHitRate[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Base Game Symbols Hit Frequency:");
		System.out.print("\t");
		for (int i = 0; i < baseGameSymbolsHitRate.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < baseGameSymbolsHitRate[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < baseGameSymbolsHitRate.length; i++) {
				System.out.print((double) baseGameSymbolsHitRate[i][j]
						/ (double) totalNumberOfGames + "\t");
			}
			System.out.println();
		}
		System.out.println();

		System.out.println("Free Games Symbols RTP:");
		System.out.print("\t");
		for (int i = 0; i < freeSymbolMoney.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < freeSymbolMoney[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < freeSymbolMoney.length; i++) {
				System.out.print(
						(double) freeSymbolMoney[i][j] / (double) lostMoney
								+ "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Free Games Symbols Hit Frequency:");
		System.out.print("\t");
		for (int i = 0; i < freeGameSymbolsHitRate.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < freeGameSymbolsHitRate[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < freeGameSymbolsHitRate.length; i++) {
				System.out.print((double) freeGameSymbolsHitRate[i][j]
						/ (double) totalNumberOfGames + "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Free Games Symbols Hit Rate:");
		System.out.print("\t");
		for (int i = 0; i < freeGameSymbolsHitRate.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < freeGameSymbolsHitRate[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < freeGameSymbolsHitRate.length; i++) {
				System.out.print((double) freeGameSymbolsHitRate[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * Print screen view.
	 *
	 * @param out
	 *            Print stream reference.
	 */
	private static void printView(PrintStream out) {
		int max = view[0].length;
		for (int i = 0; i < view.length; i++) {
			if (max < view[i].length) {
				max = view[i].length;
			}
		}

		for (int j = 0; j < max; j++) {
			for (int i = 0; i < view.length && j < view[i].length; i++) {
				if (view[i][j] == NO_SYMBOL_INDEX) {
					out.print("***\t");
					continue;
				}

				out.print(SYMBOLS_NAMES.get(view[i][j]) + "\t");
			}

			out.println();
		}
	}

	/**
	 * Print simulation execution command.
	 *
	 * @param args
	 *            Command line arguments list.
	 */
	private static void printExecuteCommand(String[] args) {
		System.out.println("Execute command:");
		System.out.println();
		System.out.print("java Main ");
		for (int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
	}

	/**
	 * Load data structures from ODS file.
	 * 
	 * @param inputFileName
	 *            Name of the input file.
	 * @param baseReelsSheetName
	 *            Name of the base game reels sheet.
	 * @param freeReelsSheetName
	 *            Name of the free spins reels sheet.
	 */
	private static void loadGameStructure(String inputFileName,
			String baseReelsSheetName, String freeReelsSheetName) {
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(
					new FileInputStream(new File(inputFileName)));
		} catch (IOException e) {
			System.out
					.println("Input file " + inputFileName + " is not usable!");
			System.err.println(e);
			System.exit(0);
		}

		XSSFSheet sheet = null;

		/* Load common game information. */
		sheet = workbook.getSheet("Summary");
		int numberOfReels = (int) sheet.getRow(1).getCell(1)
				.getNumericCellValue();
		int numberOfRows = (int) sheet.getRow(2).getCell(1)
				.getNumericCellValue();
		int numberOfLines = (int) sheet.getRow(3).getCell(1)
				.getNumericCellValue();
		int numberOfSymbols = (int) sheet.getRow(4).getCell(1)
				.getNumericCellValue();
		// double rtp = targetRtp =
		// sheet.getRow(5).getCell(1).getNumericCellValue();
		scatterMultiplier = (int) (sheet.getRow(7).getCell(1)
				.getNumericCellValue());
		wildInLineMultiplier = (int) sheet.getRow(8).getCell(1)
				.getNumericCellValue();
		freeGamesMultiplier = (int) sheet.getRow(9).getCell(1)
				.getNumericCellValue();

		/* Store all symbol names and mark special like wilds and scatters. */
		WILD_INDICES.clear();
		EXTEND_WILD_INDICES.clear();
		SCATTER_INDICES.clear();
		sheet = workbook.getSheet("Symbols");
		for (int s = 1; s <= numberOfSymbols; s++) {
			SYMBOLS_NAMES.add(sheet.getRow(s).getCell(0).getStringCellValue());
			SYMBOLS_NUMBERS.add(
					(int) sheet.getRow(s).getCell(2).getNumericCellValue());

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Wild") == true) {
				WILD_INDICES.add(s - 1);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Extend") == true) {
				WILD_INDICES.add(s - 1);
				EXTEND_WILD_INDICES.add(s - 1);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Scatter") == true) {
				SCATTER_INDICES.add(s - 1);
			}
		}

		/* Load pay table. */
		sheet = workbook.getSheet("Paytable");
		paytable = new int[numberOfReels + 1][numberOfSymbols];
		for (int r = 1; r <= numberOfSymbols; r++) {
			for (int c = 1; c <= numberOfReels; c++) {
				paytable[c][r - 1] = (int) (sheet.getRow(r)
						.getCell(numberOfReels - c + 1).getNumericCellValue());
			}
		}

		/* Load lines. */
		sheet = workbook.getSheet("Lines");
		lines = new int[numberOfLines][numberOfReels];
		for (int l = 0; l < numberOfLines; l++) {
			for (int r = 0; r < numberOfRows; r++) {
				for (int c = 0; c < numberOfReels; c++) {
					if (sheet.getRow(l * (numberOfRows + 1) + r).getCell(c)
							.getStringCellValue().contains("*") == true) {
						lines[l][c] = r;
					}
				}
			}
		}

		/* Load base game reels. */
		sheet = workbook.getSheet(baseReelsSheetName);
		baseStrips = new String[numberOfReels][];
		for (int c = 0; c < baseStrips.length; c++) {
			/* Calculate length of the reel. */
			int length = 0;
			for (int r = 0; true; r++) {
				try {
					/* Check for valid symbol values. */
					String value = sheet.getRow(r).getCell(c)
							.getStringCellValue();
					if (SYMBOLS_NAMES.contains(value) == false) {
						break;
					}
				} catch (Exception e) {
					break;
				}

				length++;
			}

			/* Read the reel itself. */
			baseStrips[c] = new String[length];
			for (int r = 0; r < baseStrips[c].length; r++) {
				baseStrips[c][r] = sheet.getRow(r).getCell(c)
						.getStringCellValue();
			}
		}

		/* Load free spins reels. */
		sheet = workbook.getSheet(freeReelsSheetName);
		freeStrips = new String[numberOfReels][];
		for (int c = 0; c < freeStrips.length; c++) {
			/* Calculate length of the reel. */
			int length = 0;
			for (int r = 0; true; r++) {
				try {
					/* Check for valid symbol values. */
					String value = sheet.getRow(r).getCell(c)
							.getStringCellValue();
					if (SYMBOLS_NAMES.contains(value) == false) {
						break;
					}
				} catch (Exception e) {
					break;
				}

				length++;
			}

			/*
			 * Read the reel itself.
			 */
			freeStrips[c] = new String[length];
			for (int r = 0; r < freeStrips[c].length; r++) {
				freeStrips[c][r] = sheet.getRow(r).getCell(c)
						.getStringCellValue();
			}
		}

		view = new int[numberOfReels][numberOfRows];
		winners = new boolean[numberOfReels][numberOfRows];
	}

	/**
	 * Generate initial reels according pay table values.
	 * 
	 * @param targetLength
	 *            Initial desired length of the reels.
	 * 
	 * @return Array of symbols as initial strips.
	 */
	private static String[][] initialReels(int targetLength) {
		/* Initialize sums. */
		double values[] = new double[SYMBOLS_NAMES.size()];
		for (int symbol = 0; symbol < values.length; symbol++) {
			values[symbol] = 0D;
		}

		/* Sum win coefficients for each symbol. */
		double total = 0;
		for (int numberOf = 0; numberOf < paytable.length; numberOf++) {
			for (int symbol = 0; symbol < paytable[numberOf].length; symbol++) {
				values[symbol] += paytable[numberOf][symbol];
				total += paytable[numberOf][symbol];
			}
		}

		/* Normalize values between 0 and 1 and find the minimum. */
		double min = 1;
		for (int symbol = 0; symbol < values.length; symbol++) {
			/*
			 * Zeros are not applicable for symbols distribution calculation.
			 */
			if (values[symbol] == 0) {
				continue;
			}

			values[symbol] /= total;
			values[symbol] = 1 / values[symbol];

			if (min > values[symbol]) {
				min = values[symbol];
			}
		}

		/* Estimate amount of each symbol on the reel. */
		total = 0;
		for (int symbol = 0; symbol < values.length; symbol++) {
			values[symbol] *= (1D / min);
			total += values[symbol];
		}
		double fixLength = targetLength / total;

		/* Fix the length if it is too big. */
		total = 0;
		for (int symbol = 0; symbol < values.length; symbol++) {
			values[symbol] *= fixLength;
			values[symbol] = Math.ceil(values[symbol]);
			total += values[symbol];
		}

		/* Populate initial reels. */
		String strips[][] = new String[view.length][(int) total];
		for (int symbol = 0, level = 0; symbol < values.length; symbol++) {
			for (int counter = 0; counter < values[symbol]; counter++) {
				for (int reel = 0; reel < strips.length; reel++) {
					strips[reel][level] = SYMBOLS_NAMES.get(symbol);
				}
				level++;
			}
		}

		return strips;
	}

	/**
	 * Shuffle loaded reals in stack of symbols.
	 * 
	 * @param strips
	 *            Symbol names as array.
	 * @param stackSize
	 *            Size of the stack. If it is one there is no stack and it is
	 *            regular shuffling.
	 * @param repeats
	 *            Number of allowed repeats in neighboring stacks.
	 */
	private static void shuffle(String[][] strips, int stackSize, int repeats) {
		/* Stack of symbols can not be negative or zero. */
		if (stackSize < 1) {
			stackSize = 1;
		}

		System.err.print("Repeats by reels:\t");

		/* Handle each reel by itself. */
		for (int reel = 0; reel < strips.length; reel++) {
			/* Reel should be sorted first in order to form stacked groups. */
			List<String> sortedReel = Arrays.asList(strips[reel]);
			Collections.sort(sortedReel);

			/* Form structures to hold groups. */
			List<List<String>> stacks = new ArrayList<>();
			List<String> current = new ArrayList<String>();
			stacks.add(current);

			/* Form groups. */
			for (String symbol : sortedReel) {
				/* If the group is empty just add the symbol. */
				if (current.isEmpty() == true) {
					current.add(symbol);
					continue;
				}

				/* If the group is full create and add new group. */
				if (current.size() >= stackSize) {
					current = new ArrayList<String>();
					stacks.add(current);
				}

				/*
				 * If the next symbol is different than the symbols in the
				 * current group create and add new group.
				 */
				if (current.contains(symbol) == false) {
					current = new ArrayList<String>();
					stacks.add(current);
				}

				/* Add the symbol to the current group. */
				current.add(symbol);
			}

			/* If there are empty groups remove them. */
			for (int i = stacks.size() - 1; i >= 0; i--) {
				if (stacks.get(i).size() == 0) {
					stacks.remove(i);
				}
			}

			/*
			 * Do the real shuffling until there is no same groups next to each
			 * other.
			 */
			int counter;
			Collections.shuffle(stacks);
			do {
				/* Extra shuffle for the neighbors. */
				for (int i = 0; i < stacks.size(); i++) {
					int index1 = (i + 1) % stacks.size();
					List<String> left = stacks.get(i);
					List<String> right = stacks.get(index1);

					/*
					 * If first symbols in the groups are not equal there is
					 * nothing to be done.
					 */
					if (left.get(0).equals(right.get(0)) == false) {
						continue;
					}

					/*
					 * Probabilistic try [size] times to find different stack.
					 * It is probabilistic, because it is possible different
					 * stacks not to exist. For example when the reel has only
					 * one symbol in it.
					 */
					for (int j = 0; j < stacks.size(); j++) {
						int index2 = PRNG.nextInt(stacks.size());
						List<String> random = stacks.get(index2);

						/*
						 * If both stacks are equal there is no reason to swap
						 * them.
						 */
						if (random.get(0).equals(right.get(0)) == true) {
							continue;
						}

						/* Swap stacks if they are different. */
						Collections.swap(stacks, index1, index2);
						break;
					}
				}

				/* Check all groups which are next to each other. */
				counter = 0;
				for (int i = 0; i < stacks.size(); i++) {
					List<String> left = stacks.get(i);
					List<String> right = stacks.get((i + 1) % stacks.size());

					/*
					 * If first symbols in the groups are equal count it.
					 */
					if (left.get(0).equals(right.get(0)) == true) {
						counter++;
					}
				}
			} while (counter > repeats);
			System.err.print(counter);
			System.err.print("\t");

			/* Put symbols back to the original reel. */
			int position = 0;
			for (List<String> group : stacks) {
				for (String symbol : group) {
					strips[reel][position] = symbol;
					position++;
				}
			}
		}

		System.err.println();
		System.err.println();
	}

	/**
	 * Application single entry point method.
	 * 
	 * java Main -g 10m -p 100k -input "./doc/game001.xlsx" -basereels "Base
	 * Reels 95.5 RTP"
	 * 
	 * java Main -g 100m -p 1m -input "./doc/game001.xlsx"-binsize 1
	 * -binincrement 0 -freeoff -basereels "Base Reels 95.5 RTP"
	 * 
	 * @param args
	 *            Command line arguments.
	 * 
	 * @throws ParseException
	 *             When there is a problem with command line arguments.
	 */
	public static void main(String[] args) throws ParseException {
		/* Print execution command. */
		printExecuteCommand(args);
		System.out.println();

		/* Handling command line arguments with library. */
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Help screen."));

		options.addOption(Option.builder("input").argName("file").hasArg()
				.valueSeparator().desc("Input Excel file name.").build());

		options.addOption(Option.builder("basereels").argName("sheet").hasArg()
				.valueSeparator().desc("Excel sheet name with base game reels.")
				.build());
		options.addOption(Option.builder("freereels").argName("sheet").hasArg()
				.valueSeparator()
				.desc("Excel sheet name with free spins reels.").build());

		options.addOption(Option.builder("g").longOpt("generations")
				.argName("number").hasArg().valueSeparator()
				.desc("Number of games (default 20m).").build());
		options.addOption(Option.builder("p").longOpt("progress")
				.argName("number").hasArg().valueSeparator()
				.desc("Progress on each iteration number (default 1m).")
				.build());

		options.addOption(Option.builder("binsize").argName("size").hasArg()
				.valueSeparator()
				.desc("Histograms of the wins with initial bin size (default 1).")
				.build());
		options.addOption(Option.builder("binincrement").argName("size")
				.hasArg().valueSeparator()
				.desc("Histograms of the wins with bin increment for next bin (default 0).")
				.build());

		options.addOption(Option.builder("initial").argName("number").hasArg()
				.valueSeparator()
				.desc("Generate initial reels according paytable values and reel target length.")
				.build());
		options.addOption(Option.builder("shuffle").argName("number").hasArg()
				.valueSeparator()
				.desc("Shuffle loaded reels with symbols stacked by number (default 1 - no stacking).")
				.build());
		options.addOption(Option.builder("repeats").argName("number").hasArg()
				.valueSeparator()
				.desc("Determine shuffle stacked repeats (default 1 - no repeats).")
				.build());

		options.addOption(new Option("bruteforce", false,
				"Switch on brute force only for the base game."));
		options.addOption(
				new Option("freeoff", false, "Switch off free spins."));
		options.addOption(new Option("wildsoff", false, "Switch off wilds."));
		options.addOption(new Option("burninghot", false,
				"Burning Hot style of wilds expansion."));
		options.addOption(new Option("luckywild", false,
				"Lucky & Wild style of wilds expansion."));
		options.addOption(new Option("luckyladyscharm", false,
				"Lucky Lady's Charm rules of simulation."));
		options.addOption(new Option("ageoftroy", false,
				"Age of Troy rules of simulation."));
		options.addOption(new Option("twentyhotblast", false,
				"20 Hot Blast rules of simulation."));

		options.addOption(
				new Option("verbose", false, "Print intermediate results."));
		options.addOption(
				new Option("verify", false, "Print input data structures."));

		/* Parse command line arguments. */
		CommandLineParser parser = new DefaultParser();
		CommandLine commands = parser.parse(options, args);

		/* If help is required print it and quit the program. */
		if (commands.hasOption("help") == true) {
			printAbout();
			System.out.println();
			(new HelpFormatter()).printHelp("java Main", options, true);
			System.out.println();
			System.exit(0);
		}

		/* Read input file name. */
		String inputFileName = "";
		if (commands.hasOption("input") == true) {
			inputFileName = commands.getOptionValue("input");
		} else {
			System.out.println("Input file name is missing!");
			System.out.println();
			(new HelpFormatter()).printHelp("java Main", options, true);
			System.out.println();
			System.exit(0);
		}

		/* Base game reels sheet name. */
		String baseReelsSheetName = "";
		if (commands.hasOption("basereels") == true) {
			baseReelsSheetName = commands.getOptionValue("basereels");
		} else {
			System.out.println("Base game reels sheet name is missing!");
			System.out.println();
			(new HelpFormatter()).printHelp("java Main", options, true);
			System.out.println();
			System.exit(0);
		}

		/* Base game reels sheet name. */
		String freeReelsSheetName = "";
		if (commands.hasOption("freereels") == true) {
			freeReelsSheetName = commands.getOptionValue("freereels");
		} else {
			System.out.println("Free spins reels sheet name is missing!");
			System.out.println();
			(new HelpFormatter()).printHelp("java Main", options, true);
			System.out.println();
			System.exit(0);
		}

		/* Number of bins used in the wins histogram. */
		if (commands.hasOption("binsize") == true) {
			initialBin = Integer.valueOf(commands.getOptionValue("binsize"));
		}

		/* Number of bins used in the wins histogram. */
		if (commands.hasOption("binincrement") == true) {
			binIncrement = Integer
					.valueOf(commands.getOptionValue("binincrement"));
		}

		/* Reading of input file and reels data sheet. */
		loadGameStructure(inputFileName, baseReelsSheetName,
				freeReelsSheetName);
		initialize();

		/* Generate initial reels according pay table values. */
		if (commands.hasOption("initial") == true) {
			baseStrips = initialReels(
					Integer.valueOf(commands.getOptionValue("initial")));
			freeStrips = initialReels(
					Integer.valueOf(commands.getOptionValue("initial")));
			initialize();
			printDataStructures();
			System.exit(0);
		}

		/* Keep number of allowed stacks repeats. */
		if (commands.hasOption("repeats") == true) {
			numberOfAllowedStackRepeats = Integer
					.valueOf(commands.getOptionValue("repeats"));
		}

		/* Shuffle loaded reels with stacked size value. */
		if (commands.hasOption("shuffle") == true) {
			shuffle(baseStrips,
					Integer.valueOf(commands.getOptionValue("shuffle")),
					numberOfAllowedStackRepeats);
			shuffle(freeStrips,
					Integer.valueOf(commands.getOptionValue("shuffle")),
					numberOfAllowedStackRepeats);
			initialize();
			printDataStructures();
			System.exit(0);
		}

		/* Verification of the data structures. */
		if (commands.hasOption("verify") == true) {
			printDataStructures();
			System.exit(0);
		}

		/* Switch off free spins. */
		if (commands.hasOption("freeoff") == true) {
			freeOff = true;
		}

		/* Switch off wilds substitution. */
		if (commands.hasOption("wildsoff") == true) {
			wildsOff = true;
		}

		/* Switch on Burning Hot wilds expansion. */
		if (commands.hasOption("burninghot") == true) {
			burningHotWilds = true;
		}

		/* Switch on Lucky & Wild wilds expansion. */
		if (commands.hasOption("luckywild") == true) {
			luckyAndWildWilds = true;
		}

		/* Switch on Lucky Lady's Charm rules for the simulation. */
		if (commands.hasOption("luckyladyscharm") == true) {
			luckyLadysCharm = true;
		}

		/* Switch on Age of Troy rules for the simulation. */
		if (commands.hasOption("ageoftroy") == true) {
			ageOfTroy = true;
		}

		/* Switch on 20 Hot Blast rules for the simulation. */
		if (commands.hasOption("twentyhotblast") == true) {
			twentyHotBlast = true;
		}

		/* Run brute force instead of Monte Carlo simulation. */
		if (commands.hasOption("bruteforce") == true) {
			bruteForce = true;
		}

		/* Print calculation progress. */
		if (commands.hasOption("verbose") == true) {
			verboseOutput = true;
		}

		/* Default number of simulation. */
		long numberOfSimulations = 20_000_000L;

		/* Default intermediated printing interval. */
		long progressPrintOnIteration = 1_000_000L;

		/* Adjust number of simulations. */
		if (commands.hasOption("generations") == true) {
			try {
				numberOfSimulations = Long
						.valueOf(commands.getOptionValue("generations")
								.replace("m", "000000").replace("k", "000"));
			} catch (Exception e) {
			}
		}

		/* Adjust progress reporting interval. */
		if (commands.hasOption("progress") == true) {
			try {
				progressPrintOnIteration = Long
						.valueOf(commands.getOptionValue("progress")
								.replace("m", "000000").replace("k", "000"));
				verboseOutput = true;
			} catch (Exception e) {
			}
		}

		/* Calculate all combinations in base game. */
		if (bruteForce == true) {
			numberOfSimulations = baseGameNumberOfCombinations();
		}

		/* Simulation main loop. */
		for (long g = 0L; g < numberOfSimulations; g++) {
			if (verboseOutput == true && g == 0) {
				System.out.println("Games\tRTP\tRTP(Base)\tRTP(Free)");
			}

			/* Print progress report. */
			if (verboseOutput == true && g % progressPrintOnIteration == 0) {
				try {
					System.out.print(g + " of " + numberOfSimulations);
					System.out.print("\t");
					System.out.print(String.format("  %6.2f",
							100D * ((double) wonMoney / (double) lostMoney)));
					System.out.print("\t");
					System.out.print(String.format("  %6.2f",
							100D * ((double) baseMoney / (double) lostMoney)));
					System.out.print("\t");
					System.out.print(String.format("  %6.2f",
							100D * ((double) freeMoney / (double) lostMoney)));
				} catch (Exception e) {
					System.err.println(e);
				}
				System.out.println();
			}

			totalNumberOfGames++;

			lostMoney += totalBet;

			singleBaseGame();
		}

		System.out.println(
				"********************************************************************************");
		printStatistics();
		System.out.println(
				"********************************************************************************");
	}
}
