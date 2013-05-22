package com.eugenkiss.conexp2.gui;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class LatticeView extends DocumentView {
	private static final long serialVersionUID = 1660117627650529212L;

	public LatticeView() {
		view = new JLabel("Hier Verbanddiagramm");
		settings = new JLabel("Lattice Settings");
		init();
	}

	public String getToolTip() {
		return "Build Lattice";
	}

	public String getTabName() {
		return "Lattice line diagram";
	}

	public JComponent getSettings() {
		return new JLabel("Lattice Settings");
	}

	public Icon getIcon() {
		return null;
	}

	public DocumentView getDocument() {

		return new LatticeView();
	}

}