package edacc.experiment.plots;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author simon
 */
public class ScaleSelector extends JPanel {
    private ButtonGroup xButtons, yButtons;
    private JRadioButton xLog, xLin, yLog, yLin;

    public ScaleSelector() {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        xLog = new JRadioButton("logarithmic");
        yLog = new JRadioButton("logarithmic");
        xLin = new JRadioButton("linear");
        yLin = new JRadioButton("linear");
        xButtons = new ButtonGroup();
        yButtons = new ButtonGroup();
        xButtons.add(xLog);
        xButtons.add(xLin);
        yButtons.add(yLog);
        yButtons.add(yLin);
        
        c.anchor = GridBagConstraints.NORTHEAST;
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.01;
        add(new JLabel("x-Axis:"), c);

        c.weightx = 0.99;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridheight = 1;
        add(xLin, c);

        c.gridy = 1;
        add(xLog, c);

        c.gridheight = 2;
        c.gridy = 2;
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.01;
        add(new JLabel("y-Axis:"), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.99;
        c.gridheight = 1;
        c.gridx = 1;
        add(yLin, c);
        
        c.gridy = 3;
        add(yLog, c);

        xLin.setSelected(true);
        yLin.setSelected(true);
    }

    public boolean isXScaleLog() {
        return xLog.isSelected();
    }

    public boolean isYScaleLog() {
        return yLog.isSelected();
    }
}
