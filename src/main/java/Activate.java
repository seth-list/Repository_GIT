import javax.swing.*;

/**
 * Class Activate - set Look and Feel
 * Create GitForm
 * @author seth-list
 * @see GitForm
 */
public class Activate
{
    public static void main(String[] args)
    {


        //set Nimbus style for project
        try
        {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        new GitForm();
    }
}
