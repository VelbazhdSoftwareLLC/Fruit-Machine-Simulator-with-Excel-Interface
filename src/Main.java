
/*==============================================================================
*                                                                              *
* Fruit Machine Simulator with Excel Interface version 0.0.1                   *
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Application single entry point class.
 * 
 * @author Todor Balabanov
 */
public class Main {

	/**
	 * Pseudo-random number generator.
	 */
	private static final Random PRNG = new Random();

	/**
	 * Index of the none symbol in the array of symbols.
	 */
	private static final int NO_SYMBOL_INDEX = -1;

	/**
	 * Index of the scatter symbol in the array of symbols.
	 */
	private static final Set<Integer> SCATTER_INDICES = new HashSet<Integer>();

	/**
	 * Index of the wild symbol in the array of symbols.
	 */
	private static final Set<Integer> WILD__INDICES = new HashSet<Integer>();

	/**
	 * List of symbols names.
	 */
	private static final List<String> SYMBOLS_NAMES = new ArrayList<String>();

	/**
	 * Slot game pay table.
	 */
	private static final int[][] PAYTABLE = {};

	/**
	 * Lines combinations.
	 */
	private static final int[][] LINES = {};

	/**
	 * Stips in the base game as symbols names.
	 */
	private static final String[][] BASE_REELS = {};

	/**
	 * Stips in base game.
	 */
	private static int[][] baseReels = null;

	/**
	 * Stips in free spins.
	 */
	private static int[][] freeReels = null;

	/**
	 * Use reels stops in brute force combinations generation.
	 */
	private static int[] reelsStops = {};

	/**
	 * Current visible symbols on the screen.
	 */
	private static int[][] view = {};

	/**
	 * Current free spins multiplier.
	 */
	private static int freeGamesMultiplier = 0;

	/**
	 * If wild is presented in the line multiplier.
	 */
	private static int wildInLineMultiplier = 0;

	/**
	 * If scatter win is presented on the screen.
	 */
	private static int scatterMultiplier = 1;

	/**
	 * Total bet in single base game spin.
	 */
	private static int singleLineBet = 1;

	/**
	 * Total bet in single base game spin.
	 */
	private static int totalBet = singleLineBet * LINES.length;

	/**
	 * Free spins to be played.
	 */
	private static int freeGamesNumber = 0;

	/**
	 * Total amount of won money.
	 */
	private static long wonMoney = 0L;

	/**
	 * Total amount of lost money.
	 */
	private static long lostMoney = 0L;

	/**
	 * Total amount of won money in base game.
	 */
	private static long baseMoney = 0L;

	/**
	 * Total amount of won money in free spins.
	 */
	private static long freeMoney = 0L;

	/**
	 * Max amount of won money in base game.
	 */
	private static long baseMaxWin = 0L;

	/**
	 * Max amount of won money in free spins.
	 */
	private static long freeMaxWin = 0L;

	/**
	 * Total number of base games played.
	 */
	private static long totalNumberOfGames = 0L;

	/**
	 * Total number of free spins played.
	 */
	private static long totalNumberOfFreeGames = 0L;

	/**
	 * Total number of free spins started.
	 */
	private static long totalNumberOfFreeGameStarts = 0L;

	/**
	 * Total number of free spins started.
	 */
	private static long totalNumberOfFreeGameRestarts = 0L;

	/**
	 * Hit rate of wins in base game.
	 */
	private static long baseGameHitRate = 0L;

	/**
	 * Hit rate of wins in free spins.
	 */
	private static long freeGamesHitRate = 0L;

	/**
	 * Verbose output flag.
	 */
	private static boolean verboseOutput = false;

	/**
	 * Free spins flag.
	 */
	private static boolean freeOff = false;

	/**
	 * Wild substitution flag.
	 */
	private static boolean wildsOff = false;

	/**
	 * Brute force all winning combinations in base game only flag.
	 */
	private static boolean bruteForce = false;

	/**
	 * Symbols win hit rate in base game.
	 */
	private static long[][] baseSymbolMoney = {

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

	};

	/**
	 * Symbols hit rate in base game.
	 */
	private static long[][] baseGameSymbolsHitRate = {

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

	};

	/**
	 * Symbols win hit rate in base game.
	 */
	private static long[][] freeSymbolMoney = {

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

	};

	/**
	 * Symbols hit rate in base game.
	 */
	private static long[][] freeGameSymbolsHitRate = {

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

	};

	/*
	 * Static data initializer.
	 */
	static {
		/*
		 * Transform symbols names to integer values.
		 */
		baseReels = new int[BASE_REELS.length][];
		for (int i = 0; i < BASE_REELS.length; i++) {
			baseReels[i] = new int[BASE_REELS[i].length];
			for (int j = 0; j < BASE_REELS[i].length; j++) {
				for (int s = 0; s < SYMBOLS_NAMES.size(); s++) {
					if (SYMBOLS_NAMES.get(s).trim().equals(BASE_REELS[i][j].trim()) == true) {
						baseReels[i][j] = s;
						break;
					}
				}
			}
		}
	}

	/**
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels
	 *            Reels strips.
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
	 * @param reels
	 *            Reels strips.
	 *
	 * @author Todor Balabanov
	 */
	private static void spin(int[][] reels) {
		for (int i = 0, b, a, c; i < view.length && i < reels.length; i++) {
			if (bruteForce == true) {
				a = reelsStops[i];
				b = a + 1;
				c = a + 2;
			} else {
				a = PRNG.nextInt(reels[i].length);
				b = a + 1;
				c = a + 2;
			}

			b = b % reels[i].length;
			c = c % reels[i].length;

			view[i][0] = reels[i][a];
			view[i][1] = reels[i][b];
			view[i][2] = reels[i][c];
		}
	}

	/**
	 * Calculate win in particular line.
	 *
	 * @param line
	 *            Single line.
	 *
	 * @return Calculated win.
	 *
	 * @author Todor Balabanov
	 */
	private static int[] wildLineWin(int[] line) {
		/*
		 * Wild index with counter and win amount.
		 */
		int[][] values = new int[WILD__INDICES.size()][];
		for (int i = 0; i < WILD__INDICES.size(); i++) {
			values[i] = new int[] { (Integer) (WILD__INDICES.toArray()[i]), 0, 0 };
		}

		/*
		 * If there is no leading wild there is no wild win.
		 */
		if (WILD__INDICES.contains(line[0]) == false) {
			return (new int[] { NO_SYMBOL_INDEX, 0, 0 });
		}

		/*
		 * Each wild can lead to different level of win.
		 */
		int index = 0;
		for (int j = 0; j < values.length; j++) {
			/*
			 * Wild symbol passing to find first regular symbol.
			 */
			for (int i = 0; i < line.length; i++) {
				/*
				 * First no wild symbol found.
				 */
				if (line[i] != values[j][0]) {
					break;
				}

				/*
				 * Count how long is the wild line.
				 */
				values[j][1]++;
			}

			/*
			 * Calculate win marked by line with wilds.
			 */
			values[j][2] = singleLineBet * PAYTABLE[values[j][1]][values[j][0]];
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
	 *
	 * @return Calculated win.
	 *
	 * @author Todor Balabanov
	 */
	private static int lineWin(int[] line) {
		int[] wildWin = wildLineWin(line);

		/*
		 * Line win without wild is multiplied by one.
		 */
		wildInLineMultiplier = 1;

		/*
		 * Keep first symbol in the line.
		 */
		int symbol = line[0];

		/*
		 * Wild symbol passing to find first regular symbol.
		 */
		for (int i = 0; i < line.length; i++) {
			/*
			 * First no wild symbol found.
			 */
			if (WILD__INDICES.contains(line[i]) == false) {
				if (SCATTER_INDICES.contains(line[i]) == false) {
					symbol = line[i];
				}
				break;
			}

			/*
			 * Scatter stops the line.
			 */
			if (SCATTER_INDICES.contains(line[i]) == true) {
				break;
			}
		}

		/*
		 * Wild symbol substitution.
		 */
		for (int i = 0; i < line.length && wildsOff == false; i++) {
			if (WILD__INDICES.contains(line[i]) == false) {
				continue;
			}

			/*
			 * Substitute wild with regular symbol.
			 */
			line[i] = symbol;

			/*
			 * Line win with wild is multiplied by two.
			 */
			wildInLineMultiplier = 1;
		}

		/*
		 * Count symbols in winning line.
		 */
		int number = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == symbol) {
				number++;
			} else {
				break;
			}
		}

		/*
		 * Clear unused symbols.
		 */
		for (int i = number; i < line.length; i++) {
			line[i] = NO_SYMBOL_INDEX;
		}

		int win = singleLineBet * PAYTABLE[number][symbol] * wildInLineMultiplier;

		/*
		 * Adjust the win according wild line information.
		 */
		if (win < wildWin[2]) {
			symbol = wildWin[0];
			number = wildWin[1];
			win = wildWin[1];
		}

		/*
		 * Update statistics.
		 */
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
	 * @param view
	 *            Symbols visible in screen view.
	 *
	 * @return Calculated win.
	 *
	 * @author Todor Balabanov
	 */
	private static int linesWin(int[][] view) {
		int win = 0;

		/*
		 * Check wins in all possible lines.
		 */
		for (int l = 0; l < LINES.length; l++) {
			int[] line = { NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX, NO_SYMBOL_INDEX };

			/*
			 * Prepare line for combination check.
			 */
			for (int i = 0; i < line.length; i++) {
				int index = LINES[l][i];
				line[i] = view[i][index];
			}

			int result = lineWin(line);

			/*
			 * Accumulate line win.
			 */
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
		/*
		 * Create as many counters as many scatters there in the game.
		 */
		Map<Integer, Integer> numberOfScatters = new HashMap<Integer, Integer>();
		for (Integer scatter : SCATTER_INDICES) {
			numberOfScatters.put(scatter, 0);
		}

		/*
		 * Count scatters on the screen.
		 */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (SCATTER_INDICES.contains(view[i][j]) == true) {
					numberOfScatters.put(view[i][j], numberOfScatters.get(view[i][j]) + 1);
				}
			}
		}

		int win = 0;
		for (Integer scatter : SCATTER_INDICES) {
			win += PAYTABLE[numberOfScatters.get(scatter)][scatter] * totalBet * scatterMultiplier;

			/*
			 * Update statistics.
			 */
			if (win > 0 && freeGamesNumber == 0) {
				baseSymbolMoney[numberOfScatters.get(scatter)][scatter] += win;
				baseGameSymbolsHitRate[numberOfScatters.get(scatter)][scatter]++;
			} else if (win > 0 && freeGamesNumber > 0) {
				freeSymbolMoney[numberOfScatters.get(scatter)][scatter] += win * freeGamesMultiplier;
				freeGameSymbolsHitRate[numberOfScatters.get(scatter)][scatter]++;
			}
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

		/*
		 * In base game 3+ scatters turn into free spins.
		 */
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

		/*
		 * Spin reels.
		 */
		spin(freeReels);

		/*
		 * Win accumulated by lines.
		 */
		int win = linesWin(view) + scatterWin(view);
		win *= freeGamesMultiplier;

		/*
		 * Add win to the statistics.
		 */
		freeMoney += win;
		wonMoney += win;
		if (freeMaxWin < win) {
			freeMaxWin = win;
		}

		/*
		 * Count free games hit rate.
		 */
		if (win > 0) {
			freeGamesHitRate++;
		}

		/*
		 * Check for free games.
		 */
		freeGamesSetup();
	}

	/**
	 * Play single base game.
	 *
	 * @author Todor Balabanov
	 */
	private static void singleBaseGame() {
		/*
		 * In brute force mode reels stops are not random.
		 */
		if (bruteForce == true) {
			nextCombination(reelsStops);
		}

		/*
		 * Spin is working even in brute force mode.
		 */
		spin(baseReels);

		/*
		 * Win accumulated by lines.
		 */
		int win = linesWin(view) + scatterWin(view);

		/*
		 * Add win to the statistics.
		 */
		baseMoney += win;
		wonMoney += win;
		if (baseMaxWin < win) {
			baseMaxWin = win;
		}

		/*
		 * Count base game hit rate.
		 */
		if (win > 0) {
			baseGameHitRate++;
		}

		/*
		 * Check for free games.
		 */
		freeGamesSetup();

		/*
		 * Play all free games.
		 */
		while (freeGamesNumber > 0) {
			totalNumberOfFreeGames++;

			singleFreeGame();

			freeGamesNumber--;
		}
		freeGamesMultiplier = 1;
	}

	/**
	 * Print help information.
	 *
	 * @author Todor Balabanov
	 */
	private static void printHelp() {
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
		System.out.println("*                                                                             *");
		System.out.println("* -help           Help screen.                                                *");
		System.out.println("*                                                                             *");
		System.out.println("* -g=<number>     Number of games (default 10 000 000).                       *");
		System.out.println("* -p=<number>     Progress on each iteration number (default 10 000 000).     *");
		System.out.println("*                                                                             *");
		System.out.println("* -freeoff        Switch off free spins.                                      *");
		System.out.println("* -wildsoff       Switch off wilds.                                           *");
		System.out.println("* -bruteforce     Switch on brute force only for the base game.               *");
		System.out.println("*                                                                             *");
		System.out.println("* -verbose        Print intermediate results.                                 *");
		System.out.println("* -verify         Print input data structures.                                *");
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
		for (int i = 0; i < PAYTABLE.length; i++) {
			System.out.print("\t" + i + " of");
		}
		System.out.println();
		for (int j = 0; j < PAYTABLE[0].length; j++) {
			System.out.print(SYMBOLS_NAMES.get(j) + "\t");
			for (int i = 0; i < PAYTABLE.length; i++) {
				System.out.print(PAYTABLE[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();

		// TODO Visualize with stars and O letter.
		System.out.println("Lines:");
		for (int j = 0; j < view[0].length; j++) {
			for (int l = 0; l < LINES.length; l++) {
				for (int i = 0; i < LINES[l].length; i++) {
					if (j == LINES[l][i]) {
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
				System.out.println();
			}
			System.out.println();
		}

		System.out.println("Base Game Reels:");
		/* Count symbols in reels. */ {
			int[][] counters = {

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			};
			for (int i = 0; baseReels != null && i < baseReels.length; i++) {
				for (int j = 0; j < baseReels[i].length; j++) {
					counters[i][baseReels[i][j]]++;
				}
			}
			for (int i = 0; baseReels != null && i < baseReels.length; i++) {
				System.out.print("\tReel " + (i + 1));
			}
			System.out.println();
			for (int j = 0; j < counters[0].length; j++) {
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
			int[][] counters = {

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

					new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

			};
			for (int i = 0; freeReels != null && i < freeReels.length; i++) {
				for (int j = 0; j < freeReels[i].length; j++) {
					counters[i][freeReels[i][j]]++;
				}
			}
			for (int i = 0; freeReels != null && i < freeReels.length; i++) {
				System.out.print("\tReel " + (i + 1));
			}
			System.out.println();
			for (int j = 0; j < counters[0].length; j++) {
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
	}

	/**
	 * Print screen view.
	 *
	 * @author Todor Balabanov
	 */
	private static void printView() {
		int max = view[0].length;
		for (int i = 0; i < view.length; i++) {
			if (max < view[i].length) {
				max = view[i].length;
			}
		}

		for (int j = 0; j < max; j++) {
			for (int i = 0; i < view.length && j < view[i].length; i++) {
				System.out.print(SYMBOLS_NAMES.get(view[i][j]) + "   ");
			}

			System.out.println();
		}
	}

	/**
	 * Print simulation execution command.
	 *
	 * @param args
	 *            Command line arguments list.
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
	 * Application single entry point method.
	 * 
	 * @param args
	 *            Command line arguments.
	 * @throws ParseException
	 *             When there is a problem with command line arguments.
	 */
	public static void main(String[] args) throws ParseException {
		/*
		 * Handling command line arguments with library.
		 */
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Help screen."));

		options.addOption(Option.builder().argName("input").hasArg().numberOfArgs(2).valueSeparator()
				.desc("Input Excel file name.").required().build());

		options.addOption(Option.builder().argName("reels").hasArg().numberOfArgs(2).valueSeparator()
				.desc("Excel sheet name with reels.").required().build());

		options.addOption(Option.builder().argName("g").hasArg().numberOfArgs(2).valueSeparator()
				.desc("Number of games (default 10 000 000).").build());
		options.addOption(Option.builder().argName("p").hasArg().numberOfArgs(2).valueSeparator()
				.desc("Progress on each iteration number (default 10 000 000).").build());

		options.addOption(new Option("freeoff", false, "Switch off free spins."));
		options.addOption(new Option("wildsoff", false, "Switch off wilds."));
		options.addOption(new Option("bruteforce", false, "Switch on brute force only for the base game."));

		options.addOption(new Option("verbose", false, "Print intermediate results."));
		options.addOption(new Option("verify", false, "Print input data structures."));

		CommandLineParser parser = new DefaultParser();
		CommandLine commands = parser.parse(options, args);

		printExecuteCommand(args);
		System.out.println();

		long numberOfSimulations = 20000000L;
		long progressPrintOnIteration = 1000000L;

		/*
		 * Parse command line arguments.
		 */
		for (int a = 0; a < args.length; a++) {
			if (args.length > 0 && args[a].contains("-g=")) {
				String parameter = args[a].substring(2);

				if (parameter.contains("k")) {
					parameter = parameter.substring(0, parameter.length() - 1);
					parameter += "000";
				}

				if (parameter.contains("m")) {
					parameter = parameter.substring(0, parameter.length() - 1);
					parameter += "000000";
				}

				try {
					numberOfSimulations = Integer.valueOf(parameter);
				} catch (Exception e) {
				}
			}

			if (args.length > 0 && args[a].contains("-p=")) {
				String parameter = args[a].substring(2);

				if (parameter.contains("k")) {
					parameter = parameter.substring(0, parameter.length() - 1);
					parameter += "000";
				}

				if (parameter.contains("m")) {
					parameter = parameter.substring(0, parameter.length() - 1);
					parameter += "000000";
				}

				try {
					progressPrintOnIteration = Integer.valueOf(parameter);
					verboseOutput = true;
				} catch (Exception e) {
				}
			}

			if (args.length > 0 && args[a].contains("-freeoff")) {
				freeOff = true;
			}

			if (args.length > 0 && args[a].contains("-wildsoff")) {
				wildsOff = true;
			}

			if (args.length > 0 && args[a].contains("-bruteforce")) {
				bruteForce = true;
			}

			if (args.length > 0 && args[a].contains("-verbose")) {
				verboseOutput = true;
			}

			if (args.length > 0 && args[a].contains("-verify")) {
				printDataStructures();
				System.exit(0);
			}

			if (args.length > 0 && args[a].contains("-help")) {
				printHelp();
				System.out.println();
				System.exit(0);
			}

			if (args.length > 0 && args[a].contains("-h")) {
				printHelp();
				System.out.println();
				System.exit(0);
			}
		}

		/*
		 * Calculate all combinations in base game.
		 */
		if (bruteForce == true) {
			/*
			 * Minus one is needed in order first combination to start from zeros in brute
			 * force calculations.
			 */
			reelsStops = new int[] { -1, 0, 0, 0, 0 };
			numberOfSimulations = 1;
			for (int i = 0; i < baseReels.length; i++) {
				numberOfSimulations *= baseReels[i].length;
			}
		}

		/*
		 * Simulation main loop.
		 */
		for (long g = 0L; g < numberOfSimulations; g++) {
			if (verboseOutput == true && g == 0) {
				System.out.println("Games\tRTP\tRTP(Base)\tRTP(Free)");
			}

			/*
			 * Print progress report.
			 */
			if (verboseOutput == true && g % progressPrintOnIteration == 0) {
				try {
					System.out.print(g + " of " + numberOfSimulations);
					System.out.print("\t");
					System.out.print(String.format("  %6.2f", ((double) wonMoney / (double) lostMoney)));
					System.out.print("\t");
					System.out.print(String.format("  %6.2f", ((double) baseMoney / (double) lostMoney)));
					System.out.print("\t");
					System.out.print(String.format("  %6.2f", ((double) freeMoney / (double) lostMoney)));
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
