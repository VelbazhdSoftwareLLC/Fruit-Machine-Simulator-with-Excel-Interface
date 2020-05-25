package eu.veldsoft.slot.simulator;

import javafx.scene.image.Image;

/**
 * Describe single symbol from the paytable.
 * 
 * @author Todor Balabanov
 */
final class Symbol {
	enum Type {
		NONE, REGULAR, SCATTER, WILD, EXTEND, FREE, BONUS
	};

	int index;
	String name;
	Type type;
	int pays[];
	Image image;
}
