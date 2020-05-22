package eu.veldsoft.slot.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Game modeling logic.
 * 
 * @author Todor Balabanov
 */
class Modeling {

	/** Number of allowed stack repeats in the shuffling process. */
	static int numberOfAllowedStackRepeats = 1;

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
	static void shuffle(String[][] strips, int stackSize, int repeats) {
		/*
		 * Stack of symbols can not be zero, but if it is zero group by sorting
		 * is done.
		 */
		if (stackSize == 0) {
			Modeling.shuffleByGroups(strips, repeats);

			return;
		}

		/* Stack of symbols can not be negative. */
		if (stackSize < 0) {
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
						int index2 = Util.PRNG.nextInt(stacks.size());
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
	 * Shuffle loaded reals by keeping group of symbols.
	 * 
	 * @param strips
	 *            Symbol names as array.
	 * 
	 * @param strips
	 */
	static void shuffleByGroups(String[][] strips, int repeats) {
		/* Handle each reel by itself. */
		for (int reel = 0; reel < strips.length; reel++) {
			List<List<String>> groups = new ArrayList<List<String>>();

			/* Empty strip can not be shuffled. */
			if (strips[reel].length <= 0) {
				continue;
			}

			/* Do shuffling. */ {
				String current = strips[reel][0];
				List<String> group = new ArrayList<String>();

				/* Form groups. */
				for (String symbol : strips[reel]) {
					if (current.equals(symbol) == false) {
						groups.add(group);
						group = new ArrayList<String>();
						current = symbol;
					}

					group.add(symbol);
				}
				groups.add(group);

				/*
				 * Shuffle groups by checking first and last symbol for
				 * identity.
				 */
				int counter = 0;
				do {
					counter = 0;
					Collections.shuffle(groups);

					for (int i = 0; i < groups.size(); i++) {
						if (groups.get(i).get(0).equals(groups
								.get((i + 1) % groups.size()).get(0)) == true) {
							counter++;
						}
					}
				} while (counter > repeats);
			}

			/* Store shuffled strip. */
			for (int i = 0; i < strips[reel].length;) {
				for (List<String> group : groups) {
					for (String symbol : group) {
						strips[reel][i] = symbol;
						i++;
					}
				}
			}
		}
	}

	/**
	 * Print all game input data structures.
	 */
	static void printDataStructures() {
		System.out.println("Symbols:");
		System.out.println("Name\tIndex\tType");
		for (int i = 0; i < Simulation.SYMBOLS.size(); i++) {
			System.out.print(Simulation.SYMBOLS.get(i).name + "\t");
			System.out.print(Simulation.SYMBOLS.get(i).index + "\t");

			if (Simulation.SCATTERS
					.contains(Simulation.SYMBOLS.get(i).index) == true) {
				System.out.print("Scatter");
			} else if (Simulation.EXTENDS
					.contains(Simulation.SYMBOLS.get(i).index) == true) {
				System.out.print("Extended");
			} else if (Simulation.WILDS
					.contains(Simulation.SYMBOLS.get(i).index) == true) {
				System.out.print("Wild");
			} else {
				System.out.print("Regular");
			}

			System.out.println();
		}
		System.out.println();

		System.out.println("Paytable:");
		for (int i = 0; i < Simulation.PAYTABLE.length; i++) {
			System.out.print("\t" + i + " of");
		}
		System.out.println();
		for (int j = 0; j < Simulation.PAYTABLE[0].length; j++) {
			System.out.print(Simulation.SYMBOLS.get(j).name + "\t");
			for (int i = 0; i < Simulation.PAYTABLE.length; i++) {
				System.out.print(Simulation.PAYTABLE[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();

		/* Visualize with stars and O letter. */
		System.out.println("Lines:");
		for (int j = 0; j < Simulation.view[0].length; j++) {
			for (int l = 0; l < Simulation.LINES.size(); l++) {
				for (int i = 0; i < Simulation.LINES
						.get(l).positions.length; i++) {
					if (j == Simulation.LINES.get(l).positions[i]) {
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
			for (int i = 0; Simulation.baseReels != null
					&& i < Simulation.baseReels.length; i++) {
				if (max < Simulation.baseReels[i].length) {
					max = Simulation.baseReels[i].length;
				}
			}
			System.out.println("Base Game Reels:");
			for (int j = 0; Simulation.baseReels != null && j < max; j++) {
				for (int i = 0; i < Simulation.baseReels.length; i++) {
					if (j < Simulation.baseReels[i].length) {
						System.out.print(Simulation.SYMBOLS
								.get(Simulation.baseReels[i][j]).name);
					}
					System.out.print("\t");
				}
				System.out.print("\t");
				for (int i = 0; i < Simulation.baseReels.length; i++) {
					if (j < Simulation.baseReels[i].length) {
						System.out.print(Simulation.SYMBOLS
								.get(Simulation.baseReels[i][j]).index);
					}
					System.out.print("\t");
				}
				System.out.println();
			}
			System.out.println();
		}

		/* Vertical print of the reels. */ {
			int max = 0;
			for (int i = 0; Simulation.freeReels != null
					&& i < Simulation.freeReels.length; i++) {
				if (max < Simulation.freeReels[i].length) {
					max = Simulation.freeReels[i].length;
				}
			}
			System.out.println("Free Games Reels:");
			for (int j = 0; Simulation.freeReels != null && j < max; j++) {
				for (int i = 0; i < Simulation.freeReels.length; i++) {
					if (j < Simulation.freeReels[i].length) {
						System.out.print(Simulation.SYMBOLS
								.get(Simulation.freeReels[i][j]).name);
					}
					System.out.print("\t");
				}
				System.out.print("\t");
				for (int i = 0; i < Simulation.freeReels.length; i++) {
					if (j < Simulation.freeReels[i].length) {
						System.out.print(Simulation.SYMBOLS
								.get(Simulation.freeReels[i][j]).index);
					}
					System.out.print("\t");
				}
				System.out.println();
			}
			System.out.println();
		}

		System.out.println("Base Game Reels:");
		/* Count symbols in reels. */ {
			int[][] counters = new int[Simulation.PAYTABLE.length
					- 1][Simulation.SYMBOLS.size()];
			// TODO Counters should be initialized with zeros.
			for (int i = 0; Simulation.baseReels != null
					&& i < Simulation.baseReels.length; i++) {
				for (int j = 0; j < Simulation.baseReels[i].length; j++) {
					counters[i][Simulation.baseReels[i][j]]++;
				}
			}
			for (int i = 0; Simulation.baseReels != null
					&& i < Simulation.baseReels.length; i++) {
				System.out.print("\tReel " + (i + 1));
			}
			System.out.println();
			for (int j = 0; j < Simulation.SYMBOLS.size(); j++) {
				System.out.print(Simulation.SYMBOLS.get(j).name + "\t");
				for (int i = 0; i < counters.length; i++) {
					System.out.print(counters[i][j] + "\t");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
			System.out.print("Total:\t");
			long combinations = (Simulation.baseReels == null) ? 0L : 1L;
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
			int[][] counters = new int[Simulation.PAYTABLE.length
					- 1][Simulation.SYMBOLS.size()];
			// TODO Counters should be initialized with zeros.
			for (int i = 0; Simulation.freeReels != null
					&& i < Simulation.freeReels.length; i++) {
				for (int j = 0; j < Simulation.freeReels[i].length; j++) {
					counters[i][Simulation.freeReels[i][j]]++;
				}
			}
			for (int i = 0; Simulation.freeReels != null
					&& i < Simulation.freeReels.length; i++) {
				System.out.print("\tReel " + (i + 1));
			}
			System.out.println();
			for (int j = 0; j < Simulation.SYMBOLS.size(); j++) {
				System.out.print(Simulation.SYMBOLS.get(j).name + "\t");
				for (int i = 0; i < counters.length; i++) {
					System.out.print(counters[i][j] + "\t");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
			System.out.print("Total:\t");
			long combinations = (Simulation.freeReels == null) ? 0L : 1L;
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

}
