package fcatools.conexpng.gui.lattice;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.radiobutton.WebRadioButton;

import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.model.FormalContext;

public class AccordionMenu extends WebAccordion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3981827958628799515L;
	private static Conf state;
	private static FormalContext context;
	private static List<JCheckBox> attributeCheckBoxes;
	private static List<JCheckBox> objectCheckBoxes;

	public AccordionMenu(Conf state) {
		this.state = state;
		this.context = state.context;
		this.attributeCheckBoxes = new ArrayList<>();
		this.objectCheckBoxes = new ArrayList<>();
		this.addPane(0, "Lattice", getLatticePanel());
		this.addPane(1, "Objects", getObjectPanel());
		this.addPane(2, "Attributes", getAttributePanel());
	}

	private static JPanel getLatticePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;

		panel.add(getLatticeObjectPanel(), gbc);

		gbc.gridx = 1;

		panel.add(getLatticeAttrPanel(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(new JLabel("Edges:"), gbc);
		final WebRadioButton noneEdges = new WebRadioButton("none");
		gbc.gridy++;
		final WebRadioButton showEdges = new WebRadioButton("show");
		showEdges.setSelected(true);
		noneEdges.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				state.showEdges = false;
				noneEdges.setSelected(true);
				showEdges.setSelected(false);
				state.showLabelsChanged();
			}
		});
		showEdges.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				state.showEdges = true;
				showEdges.setSelected(true);
				noneEdges.setSelected(false);
				state.showLabelsChanged();
			}
		});

		panel.add(noneEdges, gbc);
		gbc.gridx = 1;
		panel.add(showEdges, gbc);

		return panel;
	}

	//this methode places object's radiobuttons
	private static JPanel getLatticeObjectPanel() {
		JPanel panelObjects = new JPanel(new BorderLayout());
		panelObjects.setLayout(new GridBagLayout());
		GridBagConstraints gbo = new GridBagConstraints();
		gbo.anchor = GridBagConstraints.WEST;
		gbo.gridx = 0;
		gbo.gridy = 1;
		panelObjects.add(new JLabel("Objects:"), gbo);
		gbo.gridy = 2;
		final WebRadioButton noneObjects = new WebRadioButton();
		noneObjects.setText("none");
		noneObjects.setSelected(true);

		final WebRadioButton labelsObjects = new WebRadioButton();
		labelsObjects.setText("labels");

		noneObjects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.showObjectLabel = false;
				noneObjects.setSelected(true);
				labelsObjects.setSelected(false);
				state.showLabelsChanged();
			}
		});
		labelsObjects.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				state.showObjectLabel = true;
				labelsObjects.setSelected(true);
				noneObjects.setSelected(false);
				state.showLabelsChanged();
			}
		});
		panelObjects.add(noneObjects, gbo);
		gbo.gridy = 3;
		panelObjects.add(labelsObjects, gbo);
		return panelObjects;
	}

	//this methode places attributes' radiobuttons
	private static JPanel getLatticeAttrPanel() {
		JPanel panelAttributes = new JPanel(new BorderLayout());
		panelAttributes.setLayout(new GridBagLayout());
		GridBagConstraints gba = new GridBagConstraints();
		gba.anchor = GridBagConstraints.WEST;
		gba.gridx = 0;
		gba.gridy = 1;
		panelAttributes.add(new JLabel("Attributes:"), gba);
		gba.gridy = 2;
		final WebRadioButton noneAttributes = new WebRadioButton();
		noneAttributes.setText("none");
		noneAttributes.setSelected(true);
		final WebRadioButton labelsAttributes = new WebRadioButton();
		labelsAttributes.setText("labels");

		noneAttributes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.showAttributLabel = false;
				noneAttributes.setSelected(true);
				labelsAttributes.setSelected(false);
				state.showLabelsChanged();
			}
		});
		labelsAttributes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				state.showAttributLabel = true;
				labelsAttributes.setSelected(true);
				noneAttributes.setSelected(false);
				state.showLabelsChanged();
			}
		});
		panelAttributes.add(noneAttributes, gba);
		gba.gridy = 3;
		panelAttributes.add(labelsAttributes, gba);
		return panelAttributes;
	}

	//creates a jpanel including checkbox for each attribute
	private static JPanel getAttributePanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
		gbc.gridy = 0;
		for(String s : context.getAttributes()){
			gbc.gridy++;
			final WebCheckBox box = new WebCheckBox(s);
			box.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(!box.isSelected()){
						state.context.dontConsiderAttribute(box.getText());		
					}else{
						state.context.considerAttribute(box.getText());
					}
					state.temporaryContextChanged();
				}
			});
			
			box.setSelected(true);
			panel.add(box, gbc);
			attributeCheckBoxes.add(box);
		}
		return panel;
	}
	
	//creates a jpanel including checkbox for each object
	private static JPanel getObjectPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		for(FullObject<String, String> s : context.getObjects()){
			gbc.gridy++;
			final WebCheckBox box = new WebCheckBox(s.getIdentifier());
			final FullObject<String, String> temp = s;
			box.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(!box.isSelected()){
						state.context.dontConsiderObject(temp);		
					}else{
						state.context.considerObject(temp);
					}
					state.temporaryContextChanged();			
				}
			});
			box.setSelected(true);
			panel.add(box, gbc);
			objectCheckBoxes.add(box);
		}
		return panel;
	}
	
	public void update(){
		this.removePane(1);
		objectCheckBoxes.clear();
		this.addPane(1, "Objects", getObjectPanel());
		this.removePane(2);
		attributeCheckBoxes.clear();
		this.addPane(2, "Attributes", getAttributePanel());

	}
}
