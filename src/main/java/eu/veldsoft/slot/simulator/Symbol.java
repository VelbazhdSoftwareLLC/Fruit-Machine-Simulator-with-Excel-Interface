package eu.veldsoft.slot.simulator;

import javafx.scene.image.Image;

/**
 * Describe single symbol from the paytable.
 * 
 * @author Todor Balabanov
 */
final class Symbol {
	enum Type {
		NONE, REGULAR, SCATTER, WILD, EXTEND
	};

	int index;
	String name;
	Type type;
	Image image;
}
