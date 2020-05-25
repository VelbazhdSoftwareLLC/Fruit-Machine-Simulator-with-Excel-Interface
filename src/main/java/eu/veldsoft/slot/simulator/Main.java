/*==============================================================================
*                                                                              *
* Fruit Machine Simulator with Excel Interface version 1.0.0                   *
* Copyrights (C) 2017-2020 Velbazhd Software LLC                               *
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

package eu.veldsoft.slot.simulator;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application single entry point class.
 * 
 * @author Todor Balabanov
 */
public class Main extends Application {
	/** Application mode flag. */
	private static enum Mode {
		OTHER, VISUALIZATION, SIMULATION
	};

	/** Application running mode. */
	private static Mode mode = Mode.OTHER;

	/**
	 * Print about information.
	 */
	private static void printAbout() {
		System.out.println(
				"*******************************************************************************");
		System.out.println(
				"* Fruit Machine Simulator with Excel Interface version 0.0.1                  *");
		System.out.println(
				"* Copyrights (C) 2017-2020 Velbazhd Software LLC                              *");
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
	 * Load data structures from Excel file.
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
		Simulation.scatterMultiplier = (int) (sheet.getRow(7).getCell(1)
				.getNumericCellValue());
		Simulation.wildInLineMultiplier = (int) sheet.getRow(8).getCell(1)
				.getNumericCellValue();
		Simulation.freeGamesMultiplier = (int) sheet.getRow(9).getCell(1)
				.getNumericCellValue();

		/* Read all symbols images. */
		List<XSSFPictureData> images = workbook.getAllPictures();

		/* Store all symbol names and mark special like wilds and scatters. */
		Simulation.WILDS.clear();
		Simulation.EXTENDS.clear();
		Simulation.SCATTERS.clear();
		Simulation.FREES.clear();
		Simulation.BONUSES.clear();
		sheet = workbook.getSheet("Symbols");
		for (int s = 1; s <= numberOfSymbols; s++) {
			Symbol symbol = new Symbol();

			symbol.name = sheet.getRow(s).getCell(0).getStringCellValue()
					.toString();
			symbol.index = (int) sheet.getRow(s).getCell(2)
					.getNumericCellValue();
			symbol.type = Symbol.Type.REGULAR;

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Wild") == true) {
				symbol.type = Symbol.Type.WILD;
				Simulation.WILDS.add(symbol);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Extend") == true) {
				symbol.type = Symbol.Type.EXTEND;
				Simulation.WILDS.add(symbol);
				Simulation.EXTENDS.add(symbol);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Scatter") == true) {
				symbol.type = Symbol.Type.SCATTER;
				Simulation.SCATTERS.add(symbol);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Free") == true) {
				symbol.type = Symbol.Type.FREE;
				Simulation.FREES.add(symbol);
			}

			if (sheet.getRow(s).getCell(1).getStringCellValue()
					.contains("Bonus") == true) {
				symbol.type = Symbol.Type.BONUS;
				Simulation.BONUSES.add(symbol);
			}

			symbol.image = new Image(
					new ByteArrayInputStream(images.get(s - 1).getData()));

			Simulation.SYMBOLS.add(symbol);
		}

		/* Load pay table. */
		sheet = workbook.getSheet("Paytable");
		for (int r=1; r<=Simulation.SYMBOLS.size(); r++) {
			int pays[] = new int[numberOfReels+1];
			for (int c = 1; c <= numberOfReels; c++) {
				pays[c] = (int) (sheet.getRow(r)
						.getCell(numberOfReels - c + 1).getNumericCellValue());
			}
			
			for (Symbol symbol : Simulation.SYMBOLS) {
				if(symbol.index != r-1) {
					continue;
				}
				
				symbol.pays = pays;
				Simulation.PAYTABLE.add(symbol);
				break;
			}
		}

		/* Load lines. */
		sheet = workbook.getSheet("Lines");
		Simulation.winnerLines = new int[numberOfLines];
		for (int l = 0; l < numberOfLines; l++) {
			Line line = new Line();
			line.positions = new int[numberOfReels];
			line.pattern = new boolean[numberOfReels][numberOfRows];
			Simulation.LINES.add(line);

			/* Load line color. */
			byte[] rgb = sheet.getRow(l * (numberOfRows + 1)).getCell(0)
					.getCellStyle().getFillBackgroundXSSFColor().getRGB();
			Simulation.LINES.get(l).color = new Color(rgb[0] & 0xFF,
					rgb[1] & 0xFF, rgb[2] & 0xFF);

			/* Load line mask. */
			for (int r = 0; r < numberOfRows; r++) {
				for (int c = 0; c < numberOfReels; c++) {
					if (sheet.getRow(l * (numberOfRows + 1) + r).getCell(c)
							.getStringCellValue().contains("*") == true) {
						Simulation.LINES.get(l).positions[c] = r;
						Simulation.LINES.get(l).pattern[c][r] = true;
					} else if (sheet.getRow(l * (numberOfRows + 1) + r)
							.getCell(c).getStringCellValue()
							.contains("O") == true) {
						Simulation.LINES.get(l).pattern[c][r] = false;
					}
				}
			}
		}

		/* Load base game reels. */
		sheet = workbook.getSheet(baseReelsSheetName);
		Simulation.baseStrips = new String[numberOfReels][];
		for (int c = 0; c < Simulation.baseStrips.length; c++) {
			/* Calculate length of the reel. */
			int length = 0;
			for (int r = 0; true; r++) {
				try {
					/* Check for valid symbol values. */
					String value = sheet.getRow(r).getCell(c)
							.getStringCellValue();

					boolean found = false;
					for (Symbol symbol : Simulation.SYMBOLS) {
						if (symbol.name.equals(value) == true) {
							found = true;
						}
					}

					if (found == false) {
						break;
					}
				} catch (Exception e) {
					break;
				}

				length++;
			}

			/* Read the reel itself. */
			Simulation.baseStrips[c] = new String[length];
			for (int r = 0; r < Simulation.baseStrips[c].length; r++) {
				Simulation.baseStrips[c][r] = sheet.getRow(r).getCell(c)
						.getStringCellValue();
			}
		}

		/* Load free spins reels. */
		sheet = workbook.getSheet(freeReelsSheetName);
		Simulation.freeStrips = new String[numberOfReels][];
		for (int c = 0; c < Simulation.freeStrips.length; c++) {
			/* Calculate length of the reel. */
			int length = 0;
			for (int r = 0; true; r++) {
				try {
					/* Check for valid symbol values. */
					String value = sheet.getRow(r).getCell(c)
							.getStringCellValue();

					boolean found = false;
					for (Symbol symbol : Simulation.SYMBOLS) {
						if (symbol.name.equals(value) == true) {
							found = true;
						}
					}

					if (found == false) {
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
			Simulation.freeStrips[c] = new String[length];
			for (int r = 0; r < Simulation.freeStrips[c].length; r++) {
				Simulation.freeStrips[c][r] = sheet.getRow(r).getCell(c)
						.getStringCellValue();
			}
		}

		/* Load bills list. */
		sheet = workbook.getSheet("Bills Loading");
		for (int r = 1; true; r++) {
			try {
				/* Check for valid number values. */
				int bill = (int) sheet.getRow(r).getCell(0)
						.getNumericCellValue();
				int amount = (int) sheet.getRow(r).getCell(1)
						.getNumericCellValue();

				/* Bills have 100 coins. */
				for (int i = 0; i < amount; i++) {
					Simulation.coins.add(bill * 100);
				}
			} catch (Exception e) {
				break;
			}
		}

		Simulation.view = new Symbol[numberOfReels][numberOfRows];
		Simulation.winners = new boolean[numberOfReels][numberOfRows];
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
		double values[] = new double[Simulation.SYMBOLS.size()];
		for (int symbol = 0; symbol < values.length; symbol++) {
			values[symbol] = 0D;
		}

		/* Sum win coefficients for each symbol. */
		double total = 0;
		for (Symbol symbol : Simulation.PAYTABLE) {
			for (int value : symbol.pays) {
				values[symbol.index] += value;
				total += value;
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
		String strips[][] = new String[Simulation.view.length][(int) total];
		for (int symbol = 0, level = 0; symbol < values.length; symbol++) {
			for (int counter = 0; counter < values[symbol]; counter++) {
				for (int reel = 0; reel < strips.length; reel++) {
					strips[reel][level] = Simulation.SYMBOLS.get(symbol).name;
				}
				level++;
			}
		}

		return strips;
	}

	/** Graphic interface run. */
	private static void visualize(String[] args) {
		Application.launch(args);
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

		options.addOption(new Option("help", false, "Help screen."));

		options.addOption(new Option("gui", false, "Run GUI visualization."));

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
				.desc("Shuffle loaded reels with symbols stacked by number (default 1 - no stacking), when it is 0 shuffling is done by groups.")
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
		options.addOption(new Option("extrastars", false,
				"Extra Stars rules of simulation."));
		options.addOption(new Option("arabiannights", false,
				"Arabian Nights rules of simulation."));

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

		/* Setup visualization mode. */
		if (commands.hasOption("gui") == true) {
			mode = Mode.VISUALIZATION;
		} else {
			mode = Mode.SIMULATION;
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
			Simulation.initialBin = Integer
					.valueOf(commands.getOptionValue("binsize"));
		}

		/* Number of bins used in the wins histogram. */
		if (commands.hasOption("binincrement") == true) {
			Simulation.binIncrement = Integer
					.valueOf(commands.getOptionValue("binincrement"));
		}

		/* Reading of input file and reels data sheet. */
		loadGameStructure(inputFileName, baseReelsSheetName,
				freeReelsSheetName);
		Simulation.initialize();

		/* Generate initial reels according pay table values. */
		if (commands.hasOption("initial") == true) {
			Simulation.baseStrips = initialReels(
					Integer.valueOf(commands.getOptionValue("initial")));
			Simulation.freeStrips = initialReels(
					Integer.valueOf(commands.getOptionValue("initial")));
			Simulation.initialize();
			Modeling.printDataStructures();
			System.exit(0);
		}

		/* Keep number of allowed stacks repeats. */
		if (commands.hasOption("repeats") == true) {
			Modeling.numberOfAllowedStackRepeats = Integer
					.valueOf(commands.getOptionValue("repeats"));
		}

		/* Shuffle loaded reels with stacked size value. */
		if (commands.hasOption("shuffle") == true) {
			Modeling.shuffle(Simulation.baseStrips,
					Integer.valueOf(commands.getOptionValue("shuffle")),
					Modeling.numberOfAllowedStackRepeats);
			Modeling.shuffle(Simulation.freeStrips,
					Integer.valueOf(commands.getOptionValue("shuffle")),
					Modeling.numberOfAllowedStackRepeats);
			Simulation.initialize();
			Modeling.printDataStructures();
			System.exit(0);
		}

		/* Verification of the data structures. */
		if (commands.hasOption("verify") == true) {
			Modeling.printDataStructures();
			System.exit(0);
		}

		/* Switch off free spins. */
		if (commands.hasOption("freeoff") == true) {
			Simulation.freeOff = true;
		}

		/* Switch off wilds substitution. */
		if (commands.hasOption("wildsoff") == true) {
			Simulation.wildsOff = true;
		}

		/* Switch on Burning Hot wilds expansion. */
		if (commands.hasOption("burninghot") == true) {
			Simulation.burningHotWilds = true;
		}

		/* Switch on Lucky & Wild wilds expansion. */
		if (commands.hasOption("luckywild") == true) {
			Simulation.luckyAndWildWilds = true;
		}

		/* Switch on Lucky Lady's Charm rules for the simulation. */
		if (commands.hasOption("luckyladyscharm") == true) {
			Simulation.luckyLadysCharm = true;
		}

		/* Switch on Age of Troy rules for the simulation. */
		if (commands.hasOption("ageoftroy") == true) {
			Simulation.ageOfTroy = true;
		}

		/* Switch on 20 Hot Blast rules for the simulation. */
		if (commands.hasOption("twentyhotblast") == true) {
			Simulation.twentyHotBlast = true;
		}

		/* Switch on Extra Stars rules for the simulation. */
		if (commands.hasOption("extrastars") == true) {
			Simulation.extraStars = true;
		}

		/* Switch on Arabian Nights rules for the simulation. */
		if (commands.hasOption("arabiannights") == true) {
			Simulation.arabianNights = true;
		}

		/* Run brute force instead of Monte Carlo simulation. */
		if (commands.hasOption("bruteforce") == true) {
			Simulation.bruteForce = true;
		}

		/* Print calculation progress. */
		if (commands.hasOption("verbose") == true) {
			Simulation.verboseOutput = true;
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
				Simulation.verboseOutput = true;
			} catch (Exception e) {
			}
		}

		/* Calculate all combinations in base game. */
		if (Simulation.bruteForce == true) {
			numberOfSimulations = Simulation.baseGameNumberOfCombinations();
		}

		if (mode == Mode.SIMULATION) {
			Simulation.simulate(numberOfSimulations, progressPrintOnIteration);
			System.exit(0);
		}

		if (mode == Mode.VISUALIZATION) {
			visualize(args);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		GridPane screenGrid = new GridPane();

		/* Setup initial screen. */
		final HBox[][] symbolsBorders = (HBox[][]) new HBox[Simulation.view.length][];
		final ImageView[][] symbolsViews = (ImageView[][]) new ImageView[Simulation.view.length][];
		for (int i = 0, k = 0; i < Simulation.view.length; i++) {
			symbolsBorders[i] = new HBox[Simulation.view[i].length];
			symbolsViews[i] = new ImageView[Simulation.view[i].length];
			for (int j = 0; j < Simulation.view[i].length; j++, k++) {
				symbolsViews[i][j] = new ImageView();
				symbolsBorders[i][j] = new HBox(symbolsViews[i][j]);

				symbolsBorders[i][j].setStyle(
						"-fx-border-color: black; -fx-border-width: 5;");

				screenGrid.add(symbolsBorders[i][j], i, j);

				symbolsViews[i][j].setImage(Simulation.SYMBOLS
						.get(k % Simulation.SYMBOLS.size()).image);
			}
		}

		/* Game indicators. */
		TextField creditText = new TextField();
		TextField totalBetText = new TextField();
		TextField singleWinText = new TextField();
		TextField totalWinText = new TextField();
		creditText.setPrefWidth(80);
		creditText.setMaxWidth(80);
		totalBetText.setPrefWidth(80);
		totalBetText.setMaxWidth(80);
		singleWinText.setPrefWidth(80);
		singleWinText.setMaxWidth(80);
		totalWinText.setPrefWidth(80);
		totalWinText.setMaxWidth(80);

		/* Define series which will be visualized. */
		XYChart.Series<Number, Number> loadSeries = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> clearSeries = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> balanceSeries = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> zeroSeries = new XYChart.Series<Number, Number>();

		/* Run single game. */
		Button spinButton = new Button("SPIN");
		spinButton.setOnAction(value -> {
			/* Check for available balance. */
			if (Simulation.totalBet > Simulation.credit) {
				(new Alert(AlertType.INFORMATION, "Insufficient credit!"))
						.show();
				return;
			}

			Simulation.singleBaseGame();

			/* Update financial information. */
			creditText.setText("" + Simulation.credit);
			totalBetText.setText("" + Simulation.totalBet);
			singleWinText.setText("0");
			totalWinText.setText("" + Simulation.totalWin);

			balanceSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames, Simulation.credit));
			zeroSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames, 0));

			/* Clear winning lines information. */
			for (int i = 0; i < symbolsBorders.length; i++) {
				for (int j = 0; j < symbolsBorders[i].length; j++) {
					symbolsBorders[i][j].setStyle(
							"-fx-border-color: black; -fx-border-width: 5;");
				}
			}

			/* Visualize symbols on the screen. */
			for (int i = 0; i < Simulation.view.length; i++) {
				for (int j = 0; j < Simulation.view[i].length; j++) {
					if (Simulation.view[i][j] == Util.NO_SYMBOL) {
						continue;
					}

					/* Find symbol by its index. */
					for (Symbol symbol : Simulation.SYMBOLS) {
						if (Simulation.view[i][j] == symbol) {
							symbolsViews[i][j].setImage(symbol.image);
						}
					}
				}
			}
		});

		creditText.setEditable(false);
		totalBetText.setEditable(false);
		singleWinText.setEditable(false);
		totalWinText.setEditable(false);

		/* Setup chart visual component. */
		ScatterChart<Number, Number> creditChart;
		LineChart<Number, Number> balanceChart;
		NumberAxis xAxis;
		NumberAxis yAxis;
		creditChart = new ScatterChart<Number, Number>(xAxis = new NumberAxis(),
				yAxis = new NumberAxis());
		creditChart.setLegendVisible(false);
		creditChart.setTitle("Game Balance");
		creditChart.getData().add(loadSeries);
		creditChart.getData().add(clearSeries);
		balanceChart = new LineChart<Number, Number>(xAxis, yAxis);
		balanceChart.setLegendVisible(false);
		balanceChart.setTitle("Game Balance");
		balanceChart.setCreateSymbols(false);
		xAxis.setLabel("Number of Games");
		yAxis.setLabel("Credit");
		loadSeries.setName("Load Credit");
		clearSeries.setName("Clear Credit");
		balanceSeries.setName("Credit Balance");
		zeroSeries.setName("Zero Level");
		balanceChart.getData().add(balanceSeries);
		balanceChart.getData().add(loadSeries);
		balanceChart.getData().add(clearSeries);
		balanceChart.getData().add(zeroSeries);
		zeroSeries.getData().add(new XYChart.Data<Number, Number>(0, 0));

		/* Adjust chart colors. */
		creditChart.lookup(".chart-plot-background")
				.setStyle("-fx-background-color: transparent");
		balanceChart.lookup(".chart-plot-background")
				.setStyle("-fx-background-color: transparent");
		balanceChart.lookup(".default-color0.chart-series-line")
				.setStyle("-fx-stroke: blue");
		balanceChart.lookup(".default-color1.chart-series-line")
				.setStyle("-fx-stroke: green");
		balanceChart.lookup(".default-color2.chart-series-line")
				.setStyle("-fx-stroke: red");
		balanceChart.lookup(".default-color3.chart-series-line")
				.setStyle("-fx-stroke: black");

		/* Flag for game screen visibility. */
		CheckBox gameScreenVisibility = new CheckBox("Show Game Screen");
		gameScreenVisibility.setSelected(true);
		gameScreenVisibility.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean before, Boolean after) {
						screenGrid.setVisible(after);
					}
				});

		/* Auto run setup. */
		TextField autoRunText = new TextField();
		autoRunText.setPrefWidth(80);
		autoRunText.setMaxWidth(80);

		/* Load credit setup. */
		TextField loadCreditText = new TextField();
		loadCreditText.setPrefWidth(80);
		loadCreditText.setMaxWidth(80);

		/* Limit value to number. */
		autoRunText.setText("10");
		autoRunText.textProperty().addListener((observable, before, after) -> {
			if (after.matches("\\d*")) {
				return;
			}

			loadCreditText.setText(before.replaceAll("[^\\d]", ""));
		});

		/* Load credit action. */
		Button autoRunButton = new Button("Auto Run");
		autoRunButton.setOnAction(value -> {
			/* Number of auto run games. */
			int runs = Integer.valueOf(autoRunText.getText());

			/* Play many games. */
			for (int i = 0; i < runs
					&& Simulation.credit > Simulation.totalBet; i++) {
				Simulation.singleBaseGame();

				/* Update financial information. */
				creditText.setText("" + Simulation.credit);
				totalBetText.setText("" + Simulation.totalBet);
				singleWinText.setText("0");
				totalWinText.setText("" + Simulation.totalWin);

				balanceSeries.getData().add(new XYChart.Data<Number, Number>(
						Simulation.totalNumberOfGames, Simulation.credit));
				zeroSeries.getData().add(new XYChart.Data<Number, Number>(
						Simulation.totalNumberOfGames, 0));
			}
		});

		/* Limit value to number. */
		loadCreditText.setText("1000");
		loadCreditText.textProperty()
				.addListener((observable, before, after) -> {
					if (after.matches("\\d*")) {
						return;
					}

					loadCreditText.setText(before.replaceAll("[^\\d]", ""));
				});

		/* Load credit action. */
		Button loadCreditButton = new Button("Load Credit");
		loadCreditButton.setOnAction(value -> {
			loadSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames + 1, 0));
			loadSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames + 1, Simulation.credit));

			Simulation.credit += Integer.valueOf(loadCreditText.getText());
			Simulation.balance.add(Simulation.credit);
			creditText.setText("" + Simulation.credit);

			loadSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames + 1, Simulation.credit));
			loadSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames + 1, 0));

			zeroSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames + 1, 0));
		});

		/* Do bills loading simulation. */
		Button simulateBillsButton = new Button("Simulate Bills");
		simulateBillsButton.setOnAction(value -> {
			creditText.setText("");
			totalBetText.setText("");
			singleWinText.setText("");
			totalWinText.setText("");

			for (int load : Simulation.coins) {
				Simulation.credit += load;
				Simulation.balance.add(Simulation.credit);

				/* Play loaded bill. */
				while (Simulation.credit > Simulation.totalBet) {
					Simulation.singleBaseGame();
					balanceSeries.getData()
							.add(new XYChart.Data<Number, Number>(
									Simulation.totalNumberOfGames,
									Simulation.credit));
				}
			}

			Simulation.clear();
		});

		/* Load credit action. */
		Button clearCreditButton = new Button("Clear Credit");
		clearCreditButton.setOnAction(value -> {
			clearSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames - 1, 0));
			clearSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames - 1, Simulation.credit));

			Simulation.credit = 0;
			Simulation.balance.add(Simulation.credit);
			creditText.setText("" + Simulation.credit);

			clearSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames - 1, Simulation.credit));
			clearSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames - 1, 0));

			zeroSeries.getData().add(new XYChart.Data<Number, Number>(
					Simulation.totalNumberOfGames - 1, 0));
		});

		Button clearChartButton = new Button("Clear Chart");
		clearChartButton.setOnAction(value -> {
			Simulation.totalNumberOfGames = 0;
			Simulation.balance.clear();
			balanceSeries.getData().clear();
			loadSeries.getData().clear();
			clearSeries.getData().clear();
			zeroSeries.getData().clear();

			zeroSeries.getData().add(new XYChart.Data<Number, Number>(0, 0));
		});

		/* Assemble visual controls layout. */
		VBox vbox = new VBox(screenGrid,
				new BorderPane(null, null, new HBox(new Label("Credit:"),
						creditText, new Label("Total Bet:"), totalBetText,
						new Label("Single Win:"), singleWinText,
						new Label("Total Win:"), totalWinText, spinButton),
						null, null),
				new BorderPane(null, null, new HBox(/* gameScreenVisibility, */
						autoRunButton, autoRunText, loadCreditButton,
						loadCreditText, clearCreditButton,
						simulateBillsButton /* ,clearChartButton */), null,
						null));

		/* Show statistics scene. */
		Stage statistics = new Stage();
		statistics.setTitle("Game Statistics");
		statistics.setScene(
				new Scene(new StackPane(/* creditChart, */ balanceChart)));
		statistics.setX(0);
		statistics.setY(0);
		statistics.show();

		/* Cascade close of windows. */
		stage.setOnCloseRequest(event -> {
			statistics.close();
		});

		/* Show main scene. */
		stage.setScene(new Scene(vbox));
		stage.setTitle(
				"Fruit Machine Simulator with Excel Interface version 1.0.0 Copyrights (C) 2017-2020 Velbazhd Software LLC");
		stage.setX(200);
		stage.setY(200);
		stage.show();

		/* Loop over winning lines. */
		(new Timer(true)).scheduleAtFixedRate(new TimerTask() {
			private void showLine(int numberOfWinningLines) {
				int show = -1;
				int current = (int) ((System.currentTimeMillis() / 1000)
						% numberOfWinningLines);
				for (int l = 0, stop = -1; l < Simulation.LINES.size(); l++) {
					/* If it is not a winning line do nothing. */
					if (Simulation.winnerLines[l] == 0) {
						continue;
					}

					/* Increment winning lines counter. */
					stop++;

					if (current == stop) {
						show = l;
						break;
					}
				}

				/* Show the win from the current line. */
				singleWinText.setText("" + Simulation.winnerLines[show]);

				int red = Simulation.LINES.get(show).color.getRed();
				int green = Simulation.LINES.get(show).color.getGreen();
				int blue = Simulation.LINES.get(show).color.getBlue();
				for (int i = 0; i < Simulation.winners.length; i++) {
					int j = Simulation.LINES.get(show).positions[i];
					symbolsBorders[i][j].setStyle("-fx-border-color: #"
							+ String.format("%02X%02X%02X", red, green, blue)
							+ "; -fx-border-width: 5;");
				}
			}

			@Override
			public void run() {
				for (int i = 0; i < symbolsBorders.length; i++) {
					for (int j = 0; j < symbolsBorders[i].length; j++) {
						symbolsBorders[i][j].setStyle(
								"-fx-border-color: black; -fx-border-width: 5;");
					}
				}

				/* Count the number of winning lines. */
				int counter = 0;
				for (int l = 0; l < Simulation.LINES.size(); l++) {
					if (Simulation.winnerLines[l] > 0) {
						counter++;
					}
				}

				/* Show one of winning lines. */
				if (counter > 0) {
					showLine(counter);
				}

				/* Show scatter win. */
				for (int i = 0; i < Simulation.winners.length; i++) {
					for (int j = 0; j < Simulation.winners[i].length; j++) {
						if (Simulation.winners[i][j] == false) {
							continue;
						}

						boolean isScatter = false;
						for (Symbol scatter : Simulation.SCATTERS) {
							if (Simulation.view[i][j] == scatter) {
								isScatter = true;
							}
						}

						if (isScatter == false) {
							continue;
						}

						symbolsBorders[i][j].setStyle(
								"-fx-border-color: white; -fx-border-width: 5;");
					}
				}
			}
		}, 1000l, 1000l);
	}
}
