package eu.veldsoft.slot.simulator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Game simulation logic.
 * 
 * @author Todor Balabanov
 */
class Simulation {

	/** Index of the scatter symbol in the array of symbols. */
	static final Set<Integer> SCATTERS = new HashSet<Integer>();

	/** Index of the wild symbol in the array of symbols. */
	static final Set<Integer> WILDS = new HashSet<Integer>();

	/** Index of the extend wild symbol in the array of symbols. */
	static final Set<Integer> EXTENDS = new HashSet<Integer>();

	/** Set of symbols to trigger free spins in the array of symbols. */
	static final Set<Symbol> FREES = new HashSet<Symbol>();

	/** Set of symbols to trigger bonus game in the array of symbols. */
	static final Set<Symbol> BONUSES = new HashSet<Symbol>();

	/** List of symbols names. */
	static final List<Symbol> SYMBOLS = new ArrayList<Symbol>();

	/** Slot game pay table. */
	static int[][] PAYTABLE = {};

	/** Lines combinations. */
	static final List<Line> LINES = new ArrayList<Line>();

	/** Target RTP percent. */
	private static double targetRtp = 0;

	/** Stips in the base game as symbols names. */
	static String[][] baseStrips = {};

	/** Stips in the free spins as symbols names. */
	static String[][] freeStrips = {};

	/** Stips in base game. */
	static int[][] baseReels = null;

	/** Stips in free spins. */
	static int[][] freeReels = null;

	/**
	 * Use reels stops in brute force combinations generation and collapse
	 * feature.
	 */
	static int[] reelsStops = {};

	/** Current visible symbols on the screen. */
	static int[][] view = {};

	/** Cells on the screen which took part of the wins. */
	static boolean[][] winners = {};

	/** Lines on the screen which took part of the wins. */
	static int[] winnerLines = {};

	/** Current free spins multiplier. */
	static int freeGamesMultiplier = 0;

	/** If wild is presented in the line multiplier. */
	static int wildInLineMultiplier = 0;

	/** If scatter win is presented on the screen. */
	static int scatterMultiplier = 0;

	/** Balance of the game. */
	static int credit = 0;

	/** Total bet in single base game spin. */
	static int singleLineBet = 0;

	/** Total bet in single base game spin. */
	static int totalBet = 0;

	/** Total win in single base game spin. */
	static int totalWin = 0;

	/** Free spins to be played. */
	static int freeGamesNumber = 0;

	/** Total amount of won money. */
	static long wonMoney = 0L;

	/** Total amount of lost money. */
	static long lostMoney = 0L;

	/** Total amount of won money in base game. */
	static long baseMoney = 0L;

	/** Game balance, which is the credit after every base game. */
	static final List<Integer> balance = new ArrayList<Integer>();

	/** List of coins to be loaded as credit. */
	static final List<Integer> coins = new ArrayList<Integer>();

	/**
	 * All values as win in the base game (even zeros) for the whole simulation.
	 */
	static List<Integer> baseOutcomes = new ArrayList<Integer>();

	/** Total amount of won money in free spins. */
	static long freeMoney = 0L;

	/**
	 * All values as win in the free spins (even zeros) for the whole
	 * simulation.
	 */
	static List<Integer> freeOutcomes = new ArrayList<Integer>();

	/** Max amount of won money in base game. */
	static long baseMaxWin = 0L;

	/** Max amount of won money in free spins. */
	static long freeMaxWin = 0L;

	/** Total number of base games played. */
	static long totalNumberOfGames = 0L;

	/** Total number of free spins played. */
	static long totalNumberOfFreeGames = 0L;

	/** Total number of free spins started. */
	static long totalNumberOfFreeGameStarts = 0L;

	/** Total number of free spins started. */
	static long totalNumberOfFreeGameRestarts = 0L;

	/** Maximum number of free games in a single start. */
	static int maxSingleRunFreeGames = 0;

	/** Maximum number of collapses in a single start. */
	static int maxCollapses = 0;

	/** Hit rate of wins in base game. */
	static long baseGameHitRate = 0L;

	/** Hit rate of wins in free spins. */
	static long freeGamesHitRate = 0L;

	/** Verbose output flag. */
	static boolean verboseOutput = false;

	/** Free spins flag. */
	static boolean freeOff = false;

	/** Wild substitution flag. */
	static boolean wildsOff = false;

	/** Burning Hot style of wild expansion flag. */
	static boolean burningHotWilds = false;

	/** Lucky & Wild style of wild expansion flag. */
	static boolean luckyAndWildWilds = false;

	/** Lucky Lady's Charm style of simulation flag. */
	static boolean luckyLadysCharm = false;

	/** Age of Troy style of simulation flag. */
	static boolean ageOfTroy = false;

	/** 20 Hot Blast style of simulation flag. */
	static boolean twentyHotBlast = false;

	/** Extra Stars style of simulation flag. */
	static boolean extraStars = false;

	/** Arabian Nights style of simulation flag. */
	static boolean arabianNights = false;

	/** Brute force all winning combinations in base game only flag. */
	static boolean bruteForce = false;

	/** Size of the first bin in the histogram. */
	static int initialBin = 1;

	/** Increment used for next bin in the histogram. */
	static int binIncrement = 0;

	/** Symbols win hit rate in base game. */
	static long[][] baseSymbolMoney = {};

	/** Symbols hit rate in base game. */
	static long[][] baseGameSymbolsHitRate = {};

	/** Symbols win hit rate in base game. */
	static long[][] freeSymbolMoney = {};

	/** Symbols hit rate in base game. */
	static long[][] freeGameSymbolsHitRate = {};

	/** Distribution of the wins according their amount in the base game. */
	static Map<Integer, Long> baseWinsHistogram = new HashMap<Integer, Long>();

	/** Distribution of the wins according their amount in the free spins. */
	static Map<Integer, Long> freeWinsHistogram = new HashMap<Integer, Long>();

	/**
	 * Clear supporting structures.
	 */
	static void clear() {
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				view[i][j] = Util.NO_SYMBOL.index;
			}
		}

		for (int i = 0; i < winners.length; i++) {
			for (int j = 0; j < winners[i].length; j++) {
				winners[i][j] = false;
			}
		}

		for (int i = 0; i < winnerLines.length; i++) {
			winnerLines[i] = 0;
		}
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
				if (view[i][j] == Util.NO_SYMBOL.index) {
					out.print("***\t");
					continue;
				}

				out.print(SYMBOLS.get(view[i][j]).name + "\t");
			}

			out.println();
		}
	}

	/**
	 * Data initializer.
	 */
	static void initialize() {
		/* Transform symbols names to integer values. */
		baseReels = new int[baseStrips.length][];
		for (int i = 0; i < baseStrips.length; i++) {
			baseReels[i] = new int[baseStrips[i].length];
			for (int j = 0; j < baseStrips[i].length; j++) {
				for (int s = 0; s < SYMBOLS.size(); s++) {
					if (SYMBOLS.get(s).name.trim()
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
				for (int s = 0; s < SYMBOLS.size(); s++) {
					if (SYMBOLS.get(s).name.trim()
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
				view[i][j] = Util.NO_SYMBOL.index;
			}
		}

		/* Adjust multipliers. */
		singleLineBet = 1;

		/* Calculate total bet. */
		totalBet = singleLineBet * LINES.size();

		/* Allocate memory for the counters. */
		baseSymbolMoney = new long[PAYTABLE.length][SYMBOLS.size()];
		baseGameSymbolsHitRate = new long[PAYTABLE.length][SYMBOLS.size()];
		freeSymbolMoney = new long[PAYTABLE.length][SYMBOLS.size()];
		freeGameSymbolsHitRate = new long[PAYTABLE.length][SYMBOLS.size()];
		// TODO Counters should be initialized with zeros.

		baseOutcomes.clear();
		freeOutcomes.clear();
	}

	/**
	 * Calculate all combinations in base game.
	 * 
	 * @return Total number of combinations in the base game.
	 */
	static long baseGameNumberOfCombinations() {
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
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels
	 *            Reels strips.
	 */
	static void nextCombination(int[] reelsStops) {
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
	 * If there is a win do collapse the cells took part in the win.
	 * 
	 * @param view
	 *            Screen view.
	 * @param reels
	 *            Reels used for the symbols replacement.
	 * @param stops
	 *            Positions where reels were stopped.
	 */
	static void collapse(int view[][], int reels[][], int stops[]) {
		/* Clear symbols which was part of the total win. */
		for (int i = 0; i < winners.length; i++) {
			for (int j = 0; j < winners[i].length; j++) {
				if (winners[i][j] == false) {
					continue;
				}

				view[i][j] = Util.NO_SYMBOL.index;
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
				if (view[i][j - 1] != Util.NO_SYMBOL.index
						&& view[i][j] == Util.NO_SYMBOL.index) {
					view[i][j] = view[i][j - 1];
					view[i][j - 1] = Util.NO_SYMBOL.index;
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
				if (view[i][j] != Util.NO_SYMBOL.index) {
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
	 * Initialize an empty line.
	 * 
	 * @param size
	 *            Size of a single line.
	 * 
	 * @return Single line as array with no symbols.
	 */
	static int[] emptyLine(int size) {
		int[] line = new int[size];

		for (int i = 0; i < line.length; i++) {
			line[i] = Util.NO_SYMBOL.index;
		}

		return line;
	}

	/**
	 * Calculate win in particular line.
	 *
	 * @param line
	 *            Single line.
	 *
	 * @return Calculated win.
	 */
	static int[] wildLineWin(int[] line) {
		/* Wild index with counter and win amount. */
		int[][] values = new int[WILDS.size()][];
		for (int i = 0; i < WILDS.size(); i++) {
			values[i] = new int[]{(Integer) (WILDS.toArray()[i]), 0, 0};
		}

		/* If there is no leading wild there is no wild win. */
		if (WILDS.contains(line[0]) == false) {
			return (new int[]{Util.NO_SYMBOL.index, 0, 0});
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
	 * @param statistics
	 *            Statistical information output.
	 * @param index
	 *            Line index from the list of the lines.
	 *
	 * @return Calculated win.
	 */
	static int lineWin(int line[], int statistics[][], int index) {
		/* Scatter can not lead win combination. */
		if (SCATTERS.contains(line[0]) == true) {
			return 0;
		}

		/* Calculate wild win if there is any. */
		int[] wildWin = Simulation.wildLineWin(line);

		/* Keep first symbol in the line. */
		int symbol = line[0];

		/* Wild symbol passing to find first regular symbol. */
		for (int i = 0; i < line.length; i++) {
			if (line[i] == Util.NO_SYMBOL.index) {
				break;
			}

			/* Scatter stops the line. */
			if (SCATTERS.contains(line[i]) == true) {
				break;
			}

			/* First no wild symbol found. */
			if (WILDS.contains(line[i]) == false) {
				if (SCATTERS.contains(line[i]) == false) {
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
			if (SCATTERS.contains(line[i]) == true) {
				continue;
			}

			/* Only wilds are substituted. */
			if (WILDS.contains(line[i]) == false) {
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
			line[i] = Util.NO_SYMBOL.index;
		}

		/* Calculate single line win. */
		int win = singleLineBet * PAYTABLE[number][symbol] * lineMultiplier;

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
	 * Calculate win in all possible lines.
	 *
	 * @param view
	 *            Symbols visible in screen view.
	 * @param statistics
	 *            Statistical information output.
	 *
	 * @return Calculated win.
	 */
	static int linesWin(int[][] view, int statistics[][]) {
		int win = 0;

		/* Check wins in all possible lines. */
		for (int l = 0; l < LINES.size(); l++) {
			/* Initialize an empty line. */
			int[] line = Simulation.emptyLine(LINES.get(l).positions.length);
			int[] reverse = Simulation.emptyLine(LINES.get(l).positions.length);

			/* Prepare line for combination check. */
			for (int i = 0; i < line.length; i++) {
				int index = LINES.get(l).positions[i];
				line[i] = view[i][index];
				reverse[line.length - i - 1] = view[i][index];
			}

			int result = Simulation.lineWin(line, statistics, l);

			/* Mark cells used in win formation only if there is a win. */
			for (int i = 0; result > 0 && i < line.length
					&& line[i] != Util.NO_SYMBOL.index; i++) {
				int index = LINES.get(l).positions[i];
				winners[i][index] = true;
				winnerLines[l] = result;
			}

			/* Accumulate line win. */
			win += result;

			/* Check from right to left. */
			if (extraStars == true) {
				result = Simulation.lineWin(reverse, statistics, l);

				/* Mark cells used in win formation only if there is a win. */
				for (int i = 0; result > 0 && i < reverse.length
						&& reverse[i] != Util.NO_SYMBOL.index; i++) {
					int index = LINES.get(l).positions[line.length - i - 1];
					winners[i][index] = true;
					winnerLines[l] = result;
				}

				/* Accumulate line win. */
				win += result;
			}
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
	static int scatterWin(int[][] view, int statistics[][]) {
		/* Create as many counters as many scatters there in the game. */
		Map<Integer, Integer> numberOfScatters = new HashMap<Integer, Integer>();
		for (Integer scatter : SCATTERS) {
			numberOfScatters.put(scatter, 0);
		}

		/* Count scatters on the screen. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (SCATTERS.contains(view[i][j]) == true) {
					numberOfScatters.put(view[i][j],
							numberOfScatters.get(view[i][j]) + 1);
				}
			}
		}

		int k = 0;
		int win = 0;
		for (Integer scatter : SCATTERS) {
			/* Calculate scatter win. */
			int value = 0;
			if (luckyLadysCharm == true) {
				value = PAYTABLE[numberOfScatters.get(scatter)][scatter]
						* scatterMultiplier;
			} else {
				value = PAYTABLE[numberOfScatters.get(scatter)][scatter]
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
	 * Expand wilds according Burning Hot rules.
	 * 
	 * @param view
	 *            Screen with symbols.
	 */
	static boolean burningHotSubstitution(int[][] view) {
		boolean result = false;

		/* Check wins in all possible lines. */
		int progress = 0;
		start : for (int l = 0; l < LINES.size(); l++) {
			/* Initialize an empty line. */
			int[] line = Simulation.emptyLine(LINES.get(l).positions.length);

			/* Prepare line for combination check. */
			for (int i = 0; i < line.length; i++) {
				int index = LINES.get(l).positions[i];
				line[i] = view[i][index];

				/*
				 * If current symbol is not wild there is no need to check for a
				 * win.
				 */
				int substituent = line[i];
				if (WILDS.contains(line[i]) == false) {
					continue;
				}

				/*
				 * If current symbol is wild, but there is no win no expansion
				 * is done.
				 */
				if (Simulation.lineWin(line, new int[LINES.size()][3],
						l) <= 0) {
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
	static boolean luckyAndWildSubstitution(int[][] original) {
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
		int substituent = WILDS.iterator().next();

		/* Expand wilds. */
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				/* Do nothing if the wild is not extend wild. */
				if (EXTENDS.contains(view[i][j]) == false) {
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
						if (SCATTERS.contains(view[k][l]) == true) {
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
	static boolean twentyHotBlastSubstitution(int[][] original) {
		boolean result = false;

		/* Deep copy of the view. */
		int[][] view = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			view[i] = new int[original[i].length];
			for (int j = 0; j < original[i].length; j++) {
				view[i][j] = original[i][j];
			}
		}

		int substituent = WILDS.iterator().next();

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
		if (Simulation.linesWin(view, new int[LINES.size()][3]) > 0) {
			result = true;

			for (int i = 0; i < view.length; i++) {
				for (int j = 0; j < view[i].length; j++) {
					original[i][j] = view[i][j];
				}
			}
		}

		return result;
	}

	/**
	 * Expand wilds according Extra Stars rules.
	 * 
	 * @param original1
	 *            Screen with symbols.
	 */
	static boolean extraStarsSubstitution(int[][] original) {
		boolean result = false;

		int substituent = WILDS.iterator().next();

		/* Prepare view for wins checking by expanding the wild. */
		for (int i = 0, j, r; i < original.length; i++) {
			for (j = 0, r = 0; j < original[i].length; j++) {
				if (original[i][j] == substituent) {
					r++;
				}
			}

			/* Do substitution only if at least one symbol is not a wild. */
			if (r == 0) {
				continue;
			}
			if (r == original[i].length) {
				continue;
			}

			result = true;

			/* Wild expansion. */
			for (int l = 0; l < original[i].length; l++) {
				original[i][l] = substituent;
			}
		}

		return result;
	}

	/**
	 * Update histogram information when there is a win.
	 * 
	 * @param histogram
	 *            Histogram array.
	 * @param win
	 *            Win value.
	 */
	static void updateHistogram(Map<Integer, Long> histogram, Integer win) {
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
	 * Play single Arabian Nights bonus game.
	 */
	private static void singleArabianNightsBonusGame() {
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
	static int singleCollapseGame(int multiplier, int stops[]) {
		collapse(view, baseReels, stops);

		/* Win accumulated by lines. */
		int[][] linesStatistics = new int[LINES.size()][3];
		int[][] scatterStatistics = new int[SCATTERS.size()][3];
		int win = Simulation.linesWin(view, linesStatistics)
				+ Simulation.scatterWin(view, scatterStatistics);

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
			Simulation.updateHistogram(baseWinsHistogram, win);
		}

		return win;
	}

	/**
	 * Setup parameters for free spins mode.
	 */
	static void freeGamesSetup() {
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
				if (SCATTERS.contains(view[i][j]) == true) {
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
	 * Single reels spin to fill view with symbols.
	 *
	 * @param reels
	 *            Reels strips.
	 * @param stops
	 *            Positions on which reels were stopped.
	 */
	static void spin(int[][] reels, int stops[]) {
		/* Spin all reels. */
		for (int i = 0; i < view.length && i < reels.length; i++) {
			int column[] = new int[view[i].length];

			/* Switch between Brute Force and Monte Carlo. */
			if (bruteForce == true) {
				column[0] = reelsStops[i];
			} else {
				column[0] = stops[i] = Util.PRNG.nextInt(reels[i].length);
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
	 * Play single free spin game.
	 */
	static void singleFreeGame() {
		if (bruteForce == true) {
			return;
		}

		if (freeOff == true) {
			return;
		}

		/* Keep copy of wilds. */
		int[][] old = null;
		if (extraStars == true) {
			/* Deep copy of the view. */
			old = new int[view.length][];
			for (int i = 0; i < view.length; i++) {
				old[i] = new int[view[i].length];
				for (int j = 0; j < view[i].length; j++) {
					old[i][j] = view[i][j];
				}
			}
		}

		/* Spin reels. */
		clear();
		Simulation.spin(freeReels, new int[freeReels.length]);

		/* Do Extra Stars style wilds expansion. */
		if (extraStars == true) {
			/* Recover wilds. */
			for (int i = 0; i < view.length; i++) {
				for (int j = 0; j < view[i].length; j++) {
					if (EXTENDS.contains(old[i][j]) == false) {
						continue;
					}

					/* Copy wild from the old screen. */
					view[i][j] = old[i][j];
				}
			}

			boolean expanded = Simulation.extraStarsSubstitution(view);

			/* If there is expansion add extra free spin. */
			if (expanded == true) {
				freeGamesNumber++;
			}
		}

		/* Win accumulated by lines. */
		int[][] linesStatistics = new int[LINES.size()][3];
		int[][] scatterStatistics = new int[SCATTERS.size()][3];
		int win = Simulation.linesWin(view, linesStatistics)
				+ Simulation.scatterWin(view, scatterStatistics);
		win *= freeGamesMultiplier;
		totalWin += win;

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
			Simulation.updateHistogram(freeWinsHistogram, win);
		}

		/* Check for free games. */
		Simulation.freeGamesSetup();
	}

	/**
	 * Play single base game.
	 */
	static void singleBaseGame() {
		totalNumberOfGames++;

		totalWin = 0;
		lostMoney += totalBet;
		credit -= totalBet;

		/* In brute force mode reels stops are not random. */
		if (bruteForce == true) {
			nextCombination(reelsStops);
		}

		/* Spin is working even in brute force mode. */
		clear();
		int stops[] = new int[baseReels.length];
		Simulation.spin(baseReels, stops);
		// /*DEBUG*/ printView(System.err);
		// /*DEBUG*/ System.err.println();

		/* Do Burning Hot style wilds expansion. */
		if (burningHotWilds == true) {
			Simulation.burningHotSubstitution(view);
		}

		/* Do Lucky & Wild style wilds expansion. */
		if (luckyAndWildWilds == true) {
			Simulation.luckyAndWildSubstitution(view);
		}

		/* Do 20 Hot Blast style wilds expansion. */
		if (twentyHotBlast == true) {
			Simulation.twentyHotBlastSubstitution(view);
		}

		/* Do Extra Stars style wilds expansion. */
		if (extraStars == true) {
			boolean expanded = Simulation.extraStarsSubstitution(view);

			/* If there is expansion add extra free spin. */
			if (expanded == true) {
				freeGamesNumber++;
			}
		}

		/* Win accumulated by lines. */
		int[][] linesStatistics = new int[LINES.size()][3];
		int[][] scatterStatistics = new int[SCATTERS.size()][3];
		int win = Simulation.linesWin(view, linesStatistics)
				+ Simulation.scatterWin(view, scatterStatistics);
		totalWin += win;

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
			Simulation.updateHistogram(baseWinsHistogram, win);
		}

		/* Run extra wins after cells collapse in 20 Hot Blast mode. */
		int counter = 1;
		int multiplier = 2;
		while (twentyHotBlast == true && win > 0) {
			win = Simulation.singleCollapseGame(multiplier, stops);

			/* Each collapse rise the multiplier by one. */
			multiplier++;
			counter++;
		}

		/* Keep track of collapses retriggering. */
		if (counter > maxCollapses) {
			maxCollapses = counter;
		}

		/* Check for free games. */
		Simulation.freeGamesSetup();

		/* Play all free games. */
		int singleRunFreeGames = 0;
		while (freeGamesNumber > 0) {
			totalNumberOfFreeGames++;

			Simulation.singleFreeGame();
			singleRunFreeGames++;

			freeGamesNumber--;
		}
		if (singleRunFreeGames > maxSingleRunFreeGames) {
			maxSingleRunFreeGames = singleRunFreeGames;
		}

		/* At the end of base game credit is taken. */
		credit += totalWin;

		/* Track of the balance should be done after every base game. */
		balance.add(credit);
	}

	static void simulate(long numberOfSimulations,
			long progressPrintOnIteration) {
		/* It it is first game the balance should be written before the game. */
		balance.add(credit);

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

			Simulation.singleBaseGame();
		}

		System.out.println(
				"********************************************************************************");
		Simulation.printStatistics();
		System.out.println(
				"********************************************************************************");
	}

	/**
	 * Print simulation statistics.
	 */
	static void printStatistics() {
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
			System.out.print(SYMBOLS.get(j).name + "\t");
			for (int i = 0; i < baseSymbolMoney.length; i++) {
				System.out.print(
						(double) baseSymbolMoney[i][j] / (double) lostMoney
								+ "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Base Game Symbols Wins Ratio:");
		System.out.print("\t");
		for (int i = 0; i < baseSymbolMoney.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < baseSymbolMoney[0].length; j++) {
			System.out.print(SYMBOLS.get(j).name + "\t");
			for (int i = 0; i < baseSymbolMoney.length; i++) {
				System.out.print(
						(double) baseSymbolMoney[i][j] / (double) baseMoney
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
			System.out.print(SYMBOLS.get(j).name + "\t");
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
			System.out.print(SYMBOLS.get(j).name + "\t");
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
			System.out.print(SYMBOLS.get(j).name + "\t");
			for (int i = 0; i < freeSymbolMoney.length; i++) {
				System.out.print(
						(double) freeSymbolMoney[i][j] / (double) lostMoney
								+ "\t");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Free Games Symbols Wins Ratio:");
		System.out.print("\t");
		for (int i = 0; i < freeSymbolMoney.length; i++) {
			System.out.print("" + i + "of\t");
		}
		System.out.println();
		for (int j = 0; j < freeSymbolMoney[0].length; j++) {
			System.out.print(SYMBOLS.get(j).name + "\t");
			for (int i = 0; i < freeSymbolMoney.length; i++) {
				System.out.print(
						(double) freeSymbolMoney[i][j] / (double) freeMoney
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
			System.out.print(SYMBOLS.get(j).name + "\t");
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
			System.out.print(SYMBOLS.get(j).name + "\t");
			for (int i = 0; i < freeGameSymbolsHitRate.length; i++) {
				System.out.print((double) freeGameSymbolsHitRate[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

}
