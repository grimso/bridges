package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class NewGameDialog extends JDialog implements ActionListener {
	ControllerInterface controller;
	
	private final JRadioButton[] radioButtons = new JRadioButton[2];
	// text labels
	private final JLabel widthLabel = new JLabel("Width: ");
	private final JLabel heightLabel = new JLabel("Hight: ");
	private final JLabel islandLabel = new JLabel("Islands: ");
	// text fields
	private final JTextField widthField = new JTextField(2);
	private final JTextField heigthField = new JTextField(2);
	private final JTextField islandField = new JTextField(2);
	// check box
	private final JCheckBox islandchecker = new JCheckBox("Number of islands");
	// buttons
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("cancel");

	public NewGameDialog(Frame aFrame,ControllerInterface controller) {
		super(aFrame, true);
		this.controller=controller;
		setTitle("New puzzle");

		JPanel panel = createSimpleDialogBox();
		this.add(panel);
		this.pack();
	}

	private JPanel createSimpleDialogBox() {
		final ButtonGroup group = new ButtonGroup();
		radioButtons[0] = new JRadioButton("Auto size and nr of islands");

		radioButtons[0].addActionListener(this);

		radioButtons[1] = new JRadioButton("Define size and nr of islands");
		radioButtons[1].addActionListener(this);

		for (JRadioButton button : this.radioButtons) {
			group.add(button);
		}
		radioButtons[0].setSelected(true);
		widthField.setName("Width");
		heigthField.setName("Hight");
		islandField.setName("Islands");
		widthLabel.setLabelFor(widthField);
		heightLabel.setLabelFor(heigthField);
		islandLabel.setLabelFor(islandField);

		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);

		addLabelTextRows(widthLabel, widthField, c, textControlsPane);
		addLabelTextRows(heightLabel, heigthField, c, textControlsPane);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.EAST;
		c.weightx = 0.0;
		islandchecker.addActionListener(this);
		textControlsPane.add(islandchecker, c);
		addLabelTextRows(islandLabel, islandField, c, textControlsPane);

		this.enabledSectionLogic(false);

		JPanel buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel box = new JPanel();

		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));

		for (JRadioButton button : this.radioButtons) {
			box.add(button);
		}
		// box.add(textControlsPane);
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(box, BorderLayout.PAGE_START);
		pane.add(textControlsPane, BorderLayout.CENTER);
		pane.add(buttonPane, BorderLayout.PAGE_END);
		return pane;
	}

	private void addLabelTextRows(JComponent label, JComponent textField, GridBagConstraints c, Container container) {
		c.anchor = GridBagConstraints.EAST;

		c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
		c.fill = GridBagConstraints.NONE; // reset to default
		c.weightx = 0.0; // reset to default
		container.add(label, c);

		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		container.add(textField, c);
	}

	private void enabledSectionLogic(boolean enabled) {
		this.heightLabel.setEnabled(enabled);
		this.widthLabel.setEnabled(enabled);
		this.heigthField.setEnabled(enabled);
		this.widthField.setEnabled(enabled);
		if (enabled) {
			this.islandchecker.setEnabled(enabled);
			if (this.islandchecker.isSelected()) {
				this.islandField.setEnabled(true);
				this.islandLabel.setEnabled(true);
			} else {
				this.islandField.setEnabled(false);
				this.islandLabel.setEnabled(false);
			}
		} else {
			this.islandchecker.setEnabled(enabled);
			this.islandField.setEnabled(false);
			this.islandLabel.setEnabled(false);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.radioButtons[0]) {
			this.enabledSectionLogic(false);

		} else if (e.getSource() == this.radioButtons[1]) {
			this.enabledSectionLogic(true);
		} else if (e.getSource() == this.islandchecker) {
			this.enabledSectionLogic(this.radioButtons[1].isSelected());
		} else if (e.getSource() == this.cancelButton) {
			this.setVisible(false);
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else if (e.getSource() == this.okButton) {
			if (this.radioButtons[0].isSelected()) {
				// new game is generated completely random
				boolean success=this.controller.createNewGameRandomly();
				if(success) {
					this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				}

			} else {
				// custom game specification
				// width and height need to be specified by user
				
				try {
					int widthNumber = parseTextField(widthField);
					int heightNumber = parseTextField(heigthField);
					// islands can by configured in addition
					// if islandchecker is selected , number of islands needs to be specified by
					// user
					
					if (this.islandchecker.isSelected()) {
						// islands will be taken as specified by user
						int islandNumber = parseTextField(islandField);
						boolean success=this.controller.createNewGameHightWidthIslands(heightNumber, widthNumber,islandNumber);
						if(success) {
							this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
						}
						
					} else {
						boolean success=this.controller.createNewGameHightWidth(heightNumber, widthNumber);
						if(success) {
							this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
						}
					}

				} catch (IllegalArgumentException e2) {
					JOptionPane.showMessageDialog(this, e2.getMessage());
				}

			}
		}

	}

	private int parseTextField(JTextField textField) {
		int number;
		String text = textField.getText();
		if (text.isEmpty()) {
			throw new IllegalArgumentException(String.format("No value given in TextField: %s", textField.getName()));
		}
		try {
			number = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					String.format("Only natural numbers are allowed in TextField: %s", textField.getName()));
		}
		return number;

	}

}
