import javax.swing.*;

/**
 * Wrapper class for DefaultComboBoxModel and stat
 * @author seth-list
 */
public class BClass
{
   public DefaultComboBoxModel CB;
   public int stat;

    BClass(DefaultComboBoxModel comboBoxModel, int num)
    {
        CB = comboBoxModel;
        stat = num;
    }
}
