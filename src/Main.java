
/*==============================================================================
*                                                                              *
* Fruit Machine Simulator with Excel Interface version 1.0.0                   *
* Copyrights (C) 2017 Velbazhd Software LLC                                    *
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

	/** Use reels stops in brute force combinations generation. */
	private static int[] reelsStops = {};

	/** Current visible symbols on the screen. */
	private static int[][] view = {};

	/** Current free spins multiplier. */
	private static int freeGamesMultiplier = 0;

	/** If wild is presented in the line multiplier. */
	private static int wildInLineMultiplier = 0;

	/** If scatter win is presented on the screen. */
	private static double scatterMultiplier = 0;

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

	/** All values as win in the base game (even zeros) for the whole simulation. */
	private static List<Integer> baseOutcomes = new ArrayList<Integer>();

	/** Total amount of won money in free spins. */
	private static long freeMoney = 0L;

	/**
	 * All values as win in the free spins (even zeros) for the whole simulation.
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

	/** Brute force all winning combinations in base game only flag. */
	private static boolean bruteForce = false;

	/** Number of bins used in the histogram. */
	private static int numberOfBins = 1000;

	/** Symbols win hit rate in base game. */
	private static long[][] baseSymbolMoney = {};

	/** Symbols hit rate in base game. */
	private static long[][] baseGameSymbolsHitRate = {};

	/** Symbols win hit rate in base game. */
	private static long[][] freeSymbolMoney = {};

	/** Symbols hit rate in base game. */
	private static long[][] freeGameSymbolsHitRate = {};

	/** Distribution of the wins according their amount in the base game. */
	private static long baseWinsHistogram[] = {};

	/** Distribution of the wins according their amount in the free spins. */
	private static long freeWinsHistogram[] = {};

	/**
	 * Highest win according pay table. It is used in wins histogram calculations.
	 */
	private static int highestPaytableWin = 0;

	/**
	 * Data initializer.
	 *
	 * @author Todor Balabanov
	 */
	private static void initialize() {
		/* Transform symbols names to integer values. */
		baseReels = new int[baseStrips.length][];
		for (int i = 0; i < baseStrips.length; i++) {
			baseReels[i] = new int[baseStrips[i].length];
			for (int j = 0; j < baseStrips[i].length; j++) {
				for (int s = 0; s < SYMBOLS_NAMES.size(); s++) {
					if (SYMBOLS_NAMES.get(s).trim().equals(baseStrips[i][j].trim()) == true) {
						baseReels[i][j] = s;
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
		freeGamesMultiplier = 0;
		wildInLineMultiplier = 0;
		singleLineBet = 1;

		/* Calculate total bet. */
		totalBet = singleLineBet * lines.length;

		/* Allocate memory for the counters. */
		baseSymbolMoney = new long[paytable.length][SYMBOLS_NAMES.size()];
		baseGameSymbolsHitRate = new long[paytable.length][SYMBOLS_NAMES.size()];
		freeSymbolMoney = new long[paytable.length][SYMBOLS_NAMES.size()];
		freeGameSymbolsHitRate = new long[paytable.length][SYMBOLS_NAMES.size()];
		baseWinsHistogram = new long[numberOfBins];
		freeWinsHistogram = new long[numberOfBins];
		// TODO Counters should be initialized with zeros.

		/* Calculate highest win according total bet and pay table values. */
		highestPaytableWin = 0;
		for (int i = 0; i < paytable.length; i++) {
			for (int j = 0; j < paytable[i].length; j++) {
				if (highestPaytableWin < paytable[i][j]) {
					highestPaytableWin = paytable[i][j];
				}
			}
		}

		baseOutcomes.clear();
		freeOutcomes.clear();
	}

	/**
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels Reels strips.
	 *
	 * @author Todor Balabanov
	 */
	private static void nextCombination(int[] reelsStops) {
		reelsStops[0] += 1;
		for (int i = 0; i < reelsStops.length; i++) {
			if (reelsStops[i] >= baseReels[i].length) {
				reelsStops[i] = 0;
				if (i < reelsStops.length - 1) {
					reelsStops[i + 1] += 1;
				}
			}
		}
	}

	/**
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels Reels strips.
	 *
	 * @author Todor Balabanov
	 */
	private static void spin(int[][] reels) {
		for (int i = 0; i < view.length && i < reels.length; i++) {
			int column[] = new int[view[i].length];

			if (bruteForce == true) {
				column[0] = reelsStops[i];
			} else {
				column[0] = PRNG.nextInt(reels[i].length);
			}

			for (int c = 1; c < column.length; c++) {
				column[c] = (column[0] + c) % reels[i].length;
			}

			for (int j = 0; j < view[i].length; j++) {
				view[i][j] = reels[i][column[j]];
			}
		}
	}

	/**
	 * Calculate win in particular line.
	 *
	 * @param line Single line.
	 *
	 * @return Calculated win.
	 *
	 * @author Todor Balabanov
	 */
	private static int[] wildLineWin(int[] line) {
		/* Wild index with counter and win amount. */
		int[][] values = new int[WILD_INDICES.size()][];
		for (int i = 0; i < WILD_INDICES.size(); i++) {
			values[i] = new int[] { (Integer) (WILD_INDICES.toArray()[i]), 0, 0 };
		}

		/* If there is no leading wild there is no wild win. */
		if (WILD_INDICES.contains(line[0]) == false) {
			return (new int[] { NO_SYMBOL_INDEX, 0, 0 });
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
	 * @param line Single line.
	 *
	 * @return Calculated win.
	 *
	 * @author Todor Balabanov
	 */
	private static int lineWin(int[] line) {
		/* Scatter can not lead win combination. */
		if (SCATTER_INDICES.contains(line[0]) == true) {
			return 0;
		}

		/* Calculate wild win if there is any. */
		int[] wildWin = wildLineWin(line);

		/* Line win without wild is multiplied by one. */
		wildInLineMultiplier = 1;

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

			/* Line win with wild is multiplied by two. */
			wildInLineMultiplier = 1;
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

		int win = singleLineBet * paytable[number][symbol] * wildInLineMultiplier;

		/* Adjust the win according wild line information. */
		if (win < wildWin[2]) {
			symbol = wildWin[0];
			number = wildWin[1];
			win = wildWin[1];
		}

		/* Update statistics. */
		if (win > 0 && freeGamesNumber == 0) {
			baseSymbolMoney[number][symbol] += win;
			baseGameSymbolsHitRate[number][symbol]++;
		} else if (win > 0 && freeGamesNumber > 0) {
			freeSymbolMoney[number][symbol] += win * freeGamesMultiplier;
			freeGameSymbolsHitRate[number][symbol]++;
		}

		return (win);
	}

	/**
	 * Calculate win in all possible lines.
	 *
	 * @param view Symbols visible in screen view.
	 *
	 * @return Calculated win.
	 *
	 * @author Todor Balabanov
	 */
	private static int linesWin(int[][] view) {
		int win = 0;

		/* Check wins in all possible lines. */
		for (int l = 0; l < lines.length; l++) {
			int[] line = { NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX };

			/* Prepare line for combination check. */
			for (int i = 0; i < line.length; i++) {
				int index = lines[l][i];
				line[i] = view[i][index];
			}

			int result = lineWin(line);

			/* Accumulate line win. */
			win += result;
		}

		return (win);
	}

	/**
	 * Calculate win from scatters.
	 *
	 * @retur Win from scatters.
	 *
	 * @author Todor Balabanov
	 */
	private static int scatterWin(int[][] view) {
		/* Create as many counters as many scatters there in the game. */
		Map<Integer, Integer> numberOfScatters = new HashMap<Integer, Integer>();
		for (Integer scatter : SCATTER_INDICES) {
			numberOfScatters.put(scatter, 0);
		}

		/* Count scatters on the screen. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (SCATTER_INDICES.contains(view[i][j]) == true) {
					numberOfScatters.put(view[i][j], numberOfScatters.get(view[i][j]) + 1);
				}
			}
		}

		int win = 0;
		for (Integer scatter : SCATTER_INDICES) {
			double value = paytable[numberOfScatters.get(scatter)][scatter] * totalBet * scatterMultiplier;

			/* If there is no win do nothing. */
			if (value <= 0) {
				continue;
			}

			/* Update statistics. */
			if (value > 0 && freeGamesNumber == 0) {
				baseSymbolMoney[numberOfScatters.get(scatter)][scatter] += value;
				baseGameSymbolsHitRate[numberOfScatters.get(scatter)][scatter]++;
			} else if (value > 0 && freeGamesNumber > 0) {
				freeSymbolMoney[numberOfScatters.get(scatter)][scatter] += value * freeGamesMultiplier;
				freeGameSymbolsHitRate[numberOfScatters.get(scatter)][scatter]++;
			}

			/* It is needed if there are more scatter symbols. */
			win += value;
		}

		return (win);
	}

	/**
	 * Setup parameters for free spins mode.
	 *
	 * @author Todor Balabanov
	 */
	private static void freeGamesSetup() {
		if (bruteForce == true) {
			return;
		}

		if (freeOff == true) {
			return;
		}

		int numberOfScatters = 0;
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				// TODO If there are more than one scatter it is not common all of them to
				// trigger free games.
				if (SCATTER_INDICES.contains(view[i][j]) == true) {
					numberOfScatters++;
				}
			}
		}

		/* In base game 3+ scatters turn into free spins. */
		if (numberOfScatters < 3 && freeGamesNumber == 0) {
			return;
		} else if (numberOfScatters >= 3 && freeGamesNumber == 0) {
			freeGamesNumber = 0;
			freeGamesMultiplier = 0;
			// totalNumberOfFreeGameStarts++;
		} else if (numberOfScatters >= 3 && freeGamesNumber > 0) {
			freeGamesNumber += 0;
			freeGamesMultiplier = 0;
			// totalNumberOfFreeGameRestarts++;
		}
	}

	/**
	 * Update histogram information when there is a win.
	 * 
	 * @param histogram Histogram array.
	 * @param biggest   Expected biggest win.
	 * @param win       Win value.
	 */
	private static void updateHistogram(long[] histogram, int biggest, int win) {
		/*
		 * If the win is bigger than highest according pay table values mark it in the
		 * last bin.
		 */
		if (win >= biggest) {
			histogram[histogram.length - 1]++;
			return;
		}

		int index = histogram.length * win / biggest;
		histogram[index]++;
	}

	/**
	 * Expand wilds according Burning Hot rules.
	 * 
	 * @param view Screen with symbols.
	 */
	private static void burningHotWilds(int[][] view) {
		/* Check wins in all possible lines. */
		int progress = 0;
		start: for (int l = 0; l < lines.length; l++) {
			int[] line = { NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX };

			/* Prepare line for combination check. */
			for (int i = 0; i < line.length; i++) {
				int index = lines[l][i];
				line[i] = view[i][index];

				/* If current symbol is not wild there is no need to check for a win. */
				if (WILD_INDICES.contains(line[i]) == true) {
					continue;
				}

				/* If current symbol is wild, but there is no win no expansion is done. */
				if (lineWin(line) <= 0) {
					continue;
				}

				/* Continue to not progressed part of the screen. */
				if (i <= progress) {
					continue;
				}

				/* If current symbol is wild and there is a win expansion is done. */
				for (int j = 0; j < view[i].length; j++) {
					view[i][j] = line[i];
				}

				/*
				 * Checking should start form the real beginning, but with track of the
				 * progressed part.
				 */
				progress = i;
				l = -1;
				continue start;
			}
		}
	}

	/**
	 * Expand wilds according Lucky & Wild rules.
	 * 
	 * @param view Screen with symbols.
	 */
	private static void luckyAndWildWilds(int[][] view) {
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

						/* Substitution. */
						view[k][l] = view[i][j];
					}
				}
			}
		}
	}

	/**
	 * Play single free spin game.
	 *
	 * @author Todor Balabanov
	 */
	private static void singleFreeGame() {
		if (bruteForce == true) {
			return;
		}

		if (freeOff == true) {
			return;
		}

		/* Spin reels. */
		spin(freeReels);

		/* Win accumulated by lines. */
		int win = linesWin(view) + scatterWin(view);
		win *= freeGamesMultiplier;

		/*
		 * Keep values for mathematical expectation and standard deviation calculation.
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

		/* Check for free games. */
		freeGamesSetup();
	}

	/**
	 * Play single base game.
	 *
	 * @author Todor Balabanov
	 */
	private static void singleBaseGame() {
		/* In brute force mode reels stops are not random. */
		if (bruteForce == true) {
			nextCombination(reelsStops);
		}

		/* Spin is working even in brute force mode. */
		spin(baseReels);
		// printView(System.err);
		// System.err.println();

		/* Do Burning Hot style wilds expansion. */
		if (burningHotWilds == true) {
			burningHotWilds(view);
		}

		/* Do Lucky & Wild style wilds expansion. */
		if (luckyAndWildWilds == true) {
			luckyAndWildWilds(view);
			// printView(System.err);
			// System.err.println();
		}

		/* Win accumulated by lines. */
		int win = linesWin(view) + scatterWin(view);

		/*
		 * Keep values for mathematical expectation and standard deviation calculation.
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
			updateHistogram(baseWinsHistogram, highestPaytableWin * totalBet, win);
		}

		/* Check for free games. */
		freeGamesSetup();

		/* Play all free games. */
		while (freeGamesNumber > 0) {
			totalNumberOfFreeGames++;

			singleFreeGame();

			freeGamesNumber--;
		}
		freeGamesMultiplier = 1;
	}

	/**
	 * Print about information.
	 *
	 * @author Todor Balabanov
	 */
	private static void printAbout() {
		System.out.println("*******************************************************************************");
		System.out.println("* Fruit Machine Simulator with Excel Interface version 0.0.1                  *");
		System.out.println("* Copyrights (C) 2017 Velbazhd Software LLC                                   *");
		System.out.println("*                                                                             *");
		System.out.println("* developed by Todor Balabanov ( todor.balabanov@gmail.com )                  *");
		System.out.println("* Sofia, Bulgaria                                                             *");
		System.out.println("*                                                                             *");
		System.out.println("* This program is free software: you can redistribute it and/or modify        *");
		System.out.println("* it under the terms of the GNU General Public License as published by        *");
		System.out.println("* the Free Software Foundation, either version 3 of the License, or           *");
		System.out.println("* (at your option) any later version.                                         *");
		System.out.println("*                                                                             *");
		System.out.println("* This program is distributed in the hope that it will be useful,             *");
		System.out.println("* but WITHOUT ANY WARRANTY; without even the implied warranty of              *");
		System.out.println("* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *");
		System.out.println("* GNU General Public License for more details.                                *");
		System.out.println("*                                                                             *");
		System.out.println("* You should have received a copy of the GNU General Public License           *");
		System.out.println("* along with this program. If not, see <http://www.gnu.org/licenses/>.        *");
		System.out.println("*                                                                             *");
		System.out.println("*******************************************************************************");
	}

	/**
	 * Print all simulation input data structures.
	 *
	 * @author Todor Balabanov
	 */
	private static void printDataStructures() {
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
			int[][] counters = new int[paytable.length - 1][SYMBOLS_NAMES.size()];
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
			int[][] counters = new int[paytable.length - 1][SYMBOLS_NAMES.size()];
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
	 *
	 * @author Todor Balabanov
	 */
	private static void printStatistics() {
		System.out.println("Won money:\t" + wonMoney);
		System.out.println("Lost money:\t" + lostMoney);
		System.out.println("Total Number of Games:\t" + totalNumberOfGames);
		System.out.println();
		System.out.println("Total RTP:\t" + ((double) wonMoney / (double) lostMoney) + "\t\t"
				+ (100.0D * (double) wonMoney / (double) lostMoney) + "%");
		System.out.println("Base Game RTP:\t" + ((double) baseMoney / (double) lostMoney) + "\t\t"
				+ (100.0D * (double) baseMoney / (double) lostMoney) + "%");
		System.out.println("Free Game RTP:\t" + ((double) freeMoney / (double) lostMoney) + "\t\t"
				+ (100.0D * (double) freeMoney / (double) lostMoney) + "%");
		System.out.println();
		System.out.println("Hit Frequency in Base Game:\t" + ((double) baseGameHitRate / (double) totalNumberOfGames)
				+ "\t\t" + (100.0D * (double) baseGameHitRate / (double) totalNumberOfGames) + "%");
		System.out
				.println("Hit Frequency in Free Game:\t" + ((double) freeGamesHitRate / (double) totalNumberOfFreeGames)
						+ "\t\t" + (100.0D * (double) freeGamesHitRate / (double) totalNumberOfFreeGames) + "%");
		System.out.println("Hit Frequency Base Game into Free Game:\t"
				+ ((double) totalNumberOfFreeGameStarts / (double) totalNumberOfGames) + "\t\t"
				+ (100.0D * (double) (totalNumberOfFreeGameStarts) / (double) totalNumberOfGames) + "%");
		System.out.println("Hit Frequency Free Game into Free Game:\t"
				+ ((double) totalNumberOfFreeGameRestarts / (double) totalNumberOfFreeGameStarts) + "\t\t"
				+ (100.0D * (double) (totalNumberOfFreeGameRestarts) / (double) totalNumberOfFreeGameStarts) + "%");
		System.out.println();

		System.out.println("Max Win in Base Game:\t" + baseMaxWin);
		System.out.println("Max Win in Free Game:\t" + freeMaxWin);
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
				System.out.print((double) baseSymbolMoney[i][j] / (double) lostMoney + "\t");
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
				System.out.print((double) baseGameSymbolsHitRate[i][j] / (double) totalNumberOfGames + "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Base Game Wins Histogram:");
		/* Histogram. */ {
			double sum = 0;
			for (int i = 0; i < baseWinsHistogram.length; i++) {
				System.out.print(baseWinsHistogram[i] + "\t");
				sum += baseWinsHistogram[i];
			}
			System.out.println();
			for (int i = 0; i < baseWinsHistogram.length; i++) {
				System.out.print(100D * baseWinsHistogram[i] / sum + "\t");
			}
			System.out.println();
			for (int i = 0, bin = highestPaytableWin * totalBet
					/ baseWinsHistogram.length; i < baseWinsHistogram.length; i++, bin += highestPaytableWin * totalBet
							/ baseWinsHistogram.length) {
				System.out.print("< " + bin + "\t");
			}
		}
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
				System.out.print((double) freeSymbolMoney[i][j] / (double) lostMoney + "\t");
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
				System.out.print((double) freeGameSymbolsHitRate[i][j] / (double) totalNumberOfGames + "\t");
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
		System.out.println("Free Games Wins Histogram:");
		/* Histogram. */ {
			double sum = 0;
			for (int i = 0; i < freeWinsHistogram.length; i++) {
				System.out.print(freeWinsHistogram[i] + "\t");
				sum += freeWinsHistogram[i];
			}
			System.out.println();
			for (int i = 0; i < freeWinsHistogram.length; i++) {
				System.out.print(100D * freeWinsHistogram[i] / sum + "\t");
			}
			System.out.println();
			for (int i = 0, bin = highestPaytableWin * totalBet
					/ freeWinsHistogram.length; i < freeWinsHistogram.length; i++, bin += highestPaytableWin * totalBet
							/ freeWinsHistogram.length) {
				System.out.print("< " + bin + "\t");
			}
		}
		System.out.println();
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
	}

	/**
	 * Print screen view.
	 *
	 * @param out Print stream reference.
	 *
	 * @author Todor Balabanov
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
				out.print(SYMBOLS_NAMES.get(view[i][j]) + "\t");
			}

			out.println();
		}
	}

	/**
	 * Print simulation execution command.
	 *
	 * @param args Command line arguments list.
	 *
	 * @author Todor Balabanov
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
	 * @param inputFileName      Name of the input file.
	 * @param baseReelsSheetName Name of the base game reels sheet.
	 * @param freeReelsSheetName Name of the free spins reels sheet.
	 */
	private static void loadGameStructure(String inputFileName, String baseReelsSheetName, String freeReelsSheetName) {
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(new File(inputFileName)));
		} catch (Exception e) {
			System.out.println("Input file " + inputFileName + " is not usable!");
			System.exit(0);
		}

		XSSFSheet sheet = null;

		/* Load common game information. */
		sheet = workbook.getSheet("Summary");
		int numberOfReels = (int) sheet.getRow(1).getCell(1).getNumericCellValue();
		int numberOfRows = (int) sheet.getRow(2).getCell(1).getNumericCellValue();
		int numberOfLines = (int) sheet.getRow(3).getCell(1).getNumericCellValue();
		int numberOfSymbols = (int) sheet.getRow(4).getCell(1).getNumericCellValue();
		// double rtp = targetRtp = sheet.getRow(5).getCell(1).getNumericCellValue();
		scatterMultiplier = sheet.getRow(7).getCell(1).getNumericCellValue();

		/* Store all symbol names and mark special like wilds and scatters. */
		WILD_INDICES.clear();
		EXTEND_WILD_INDICES.clear();
		SCATTER_INDICES.clear();
		sheet = workbook.getSheet("Symbols");
		for (int s = 1; s <= numberOfSymbols; s++) {
			SYMBOLS_NAMES.add(sheet.getRow(s).getCell(0).getStringCellValue());
			SYMBOLS_NUMBERS.add((int) sheet.getRow(s).getCell(2).getNumericCellValue());

			if (sheet.getRow(s).getCell(1).getStringCellValue().contains("Wild") == true) {
				WILD_INDICES.add(s - 1);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue().contains("Extend") == true) {
				WILD_INDICES.add(s - 1);
				EXTEND_WILD_INDICES.add(s - 1);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue().contains("Scatter") == true) {
				SCATTER_INDICES.add(s - 1);
			}
		}

		/* Load pay table. */
		sheet = workbook.getSheet("Paytable");
		paytable = new int[numberOfReels + 1][numberOfSymbols];
		for (int r = 1; r <= numberOfSymbols; r++) {
			for (int c = 1; c <= numberOfReels; c++) {
				paytable[c][r - 1] = (int) (sheet.getRow(r).getCell(numberOfReels - c + 1).getNumericCellValue());
			}
		}

		/* Load lines. */
		sheet = workbook.getSheet("Lines");
		lines = new int[numberOfLines][numberOfReels];
		for (int l = 0; l < numberOfLines; l++) {
			for (int r = 0; r < numberOfRows; r++) {
				for (int c = 0; c < numberOfReels; c++) {
					if (sheet.getRow(l * (numberOfRows + 1) + r).getCell(c).getStringCellValue()
							.contains("*") == true) {
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
					sheet.getRow(r).getCell(c).getStringCellValue();
				} catch (Exception e) {
					break;
				}

				length++;
			}

			/* Read the reel itself. */
			baseStrips[c] = new String[length];
			for (int r = 0; r < baseStrips[c].length; r++) {
				baseStrips[c][r] = sheet.getRow(r).getCell(c).getStringCellValue();
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
					sheet.getRow(r).getCell(c).getStringCellValue();
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
				freeStrips[c][r] = sheet.getRow(r).getCell(c).getStringCellValue();
			}
		}

		view = new int[numberOfReels][numberOfRows];
	}

	/**
	 * Generate initial reels according pay table values.
	 * 
	 * @param targetLength Initial desired length of the reels.
	 */
	private static void initialReels(int targetLength) {
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
		baseStrips = new String[view.length][(int) total];
		for (int symbol = 0, level = 0; symbol < values.length; symbol++) {
			for (int counter = 0; counter < values[symbol]; counter++) {
				for (int reel = 0; reel < baseStrips.length; reel++) {
					baseStrips[reel][level] = SYMBOLS_NAMES.get(symbol);
				}
				level++;
			}
		}
	}

	/**
	 * Shuffle loaded reals in stack of symbols.
	 * 
	 * @param stackSize Size of the stack. If it is one there is no stack and it is
	 *                  regular shuffling.
	 */
	private static void shuffleReels(int stackSize) {
		/* Stack of symbols can not be negative or zero. */
		if (stackSize < 1) {
			stackSize = 1;
		}

		/* Handle each reel by itself. */
		for (int reel = 0; reel < baseStrips.length; reel++) {
			/* Reel should be sorted first in order to form stacked groups. */
			List<String> sortedReel = Arrays.asList(baseStrips[reel]);
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
				 * If the next symbol is different than the symbols in the current group create
				 * and add new group.
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

			/* Do the real shuffling until there is no same groups next to each other. */
			boolean fine;
			do {
				Collections.shuffle(stacks);

				/* Check all groups which are next to each other. */
				fine = true;
				for (int i = 0; i < stacks.size() - 1; i++) {
					List<String> left = stacks.get(i);
					List<String> right = stacks.get(i + 1);

					/*
					 * If first symbols in the groups are equal it means that shuffling should be
					 * done once again.
					 */
					if (left.get(0).equals(right.get(0)) == true) {
						fine = false;
						break;
					}
				}
			} while (fine == false);

			/* Put symbols back to the original reel. */
			int position = 0;
			for (List<String> group : stacks) {
				for (String symbol : group) {
					baseStrips[reel][position] = symbol;
					position++;
				}
			}
		}
	}

	/**
	 * Application single entry point method.
	 * 
	 * java Main -g 10m -p 100k -input "./doc/game001.xlsx" -basereels "Base Reels
	 * 95.5 RTP"
	 * 
	 * java Main -g 100m -p 1m -input "./doc/game001.xlsx"-histogram 2000 -freeoff
	 * -basereels "Base Reels 95.5 RTP"
	 * 
	 * @param args Command line arguments.
	 * 
	 * @throws ParseException When there is a problem with command line arguments.
	 */
	public static void main(String[] args) throws ParseException {
		/* Print execution command. */
		printExecuteCommand(args);
		System.out.println();

		/* Handling command line arguments with library. */
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Help screen."));

		options.addOption(Option.builder("input").argName("file").hasArg().valueSeparator()
				.desc("Input Excel file name.").build());

		options.addOption(Option.builder("basereels").argName("sheet").hasArg().valueSeparator()
				.desc("Excel sheet name with base game reels.").build());
		options.addOption(Option.builder("freereels").argName("sheet").hasArg().valueSeparator()
				.desc("Excel sheet name with free spins reels.").build());

		options.addOption(Option.builder("g").longOpt("generations").argName("number").hasArg().valueSeparator()
				.desc("Number of games (default 20m).").build());
		options.addOption(Option.builder("p").longOpt("progress").argName("number").hasArg().valueSeparator()
				.desc("Progress on each iteration number (default 1m).").build());

		options.addOption(Option.builder("histogram").argName("size").hasArg().valueSeparator()
				.desc("Histograms of the wins with particular number of bins (default 1000).").build());

		options.addOption(Option.builder("initial").argName("number").hasArg().valueSeparator()
				.desc("Generate initial reels according paytable values and reel target length.").build());
		options.addOption(Option.builder("shuffle").argName("number").hasArg().valueSeparator()
				.desc("Shuffle loaded reels with symbols stacked by number (default 1 - no stacking).").build());

		options.addOption(new Option("bruteforce", false, "Switch on brute force only for the base game."));
		options.addOption(new Option("freeoff", false, "Switch off free spins."));
		options.addOption(new Option("wildsoff", false, "Switch off wilds."));
		options.addOption(new Option("burninghot", false, "Burning Hot style of wilds expansion."));
		options.addOption(new Option("luckywild", false, "Lucky & Wild style of wilds expansion."));

		options.addOption(new Option("verbose", false, "Print intermediate results."));
		options.addOption(new Option("verify", false, "Print input data structures."));

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
		}

		/* Number of bins used in the wins histogram. */
		if (commands.hasOption("histogram") == true) {
			numberOfBins = Integer.valueOf(commands.getOptionValue("histogram"));
		}

		/* Reading of input file and reels data sheet. */
		loadGameStructure(inputFileName, baseReelsSheetName, freeReelsSheetName);
		initialize();

		/* Generate initial reels according pay table values. */
		if (commands.hasOption("initial") == true) {
			initialReels(Integer.valueOf(commands.getOptionValue("initial")));
			initialize();
			printDataStructures();
			System.exit(0);
		}

		/* Shuffle loaded reels with stacked size value. */
		if (commands.hasOption("shuffle") == true) {
			shuffleReels(Integer.valueOf(commands.getOptionValue("shuffle")));
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

		/* Run brute force instead of Monte Carlo simulation. */
		if (commands.hasOption("bruteforce") == true) {
			bruteForce = true;
		}

		/* Print calculation progress. */
		if (commands.hasOption("verbose") == true) {
			verboseOutput = true;
		}

		long numberOfSimulations = 20_000_000L;
		long progressPrintOnIteration = 1_000_000L;

		/* Adjust number of simulations. */
		if (commands.hasOption("generations") == true) {
			try {
				numberOfSimulations = Long
						.valueOf(commands.getOptionValue("generations").replace("m", "000000").replace("k", "000"));
			} catch (Exception e) {
			}
		}

		/* Adjust progress reporting interval. */
		if (commands.hasOption("progress") == true) {
			try {
				progressPrintOnIteration = Long
						.valueOf(commands.getOptionValue("progress").replace("m", "000000").replace("k", "000"));
				verboseOutput = true;
			} catch (Exception e) {
			}
		}

		/* Calculate all combinations in base game. */
		if (bruteForce == true) {
			/*
			 * Minus one is needed in order first combination to start from zeros in brute
			 * force calculations.
			 */
			reelsStops = new int[baseReels.length];
			for (int i = 1; i < reelsStops.length; i++) {
				reelsStops[i] = 0;
			}
			reelsStops[0] = -1;

			numberOfSimulations = 1;
			for (int i = 0; i < baseReels.length; i++) {
				numberOfSimulations *= baseReels[i].length;
			}
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
					System.out.print(String.format("  %6.2f", 100D * ((double) wonMoney / (double) lostMoney)));
					System.out.print("\t");
					System.out.print(String.format("  %6.2f", 100D * ((double) baseMoney / (double) lostMoney)));
					System.out.print("\t");
					System.out.print(String.format("  %6.2f", 100D * ((double) freeMoney / (double) lostMoney)));
				} catch (Exception e) {
					System.err.println(e);
				}
				System.out.println();
			}

			totalNumberOfGames++;

			lostMoney += totalBet;

			singleBaseGame();
		}

		System.out.println("********************************************************************************");
		printStatistics();
		System.out.println("********************************************************************************");
	}
}
