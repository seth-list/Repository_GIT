import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * helper class for working with dates
 * @author seth-list
 * daysBetween - get amount of days between two dates
 * addDays - get date from current on N days
 * dateBetweenDates - check if date between dates
 * objectToDate - Object to Date parser
 * approxDate - if Date around another Date approx. 1 day
 */
public class dateWork
{
    //дней между датами
    public static int daysBetween(Date d1, Date d2)
    {
        return (int)((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    //добавить дней
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    //дата между датами - проверка
    public static boolean dateBetweenDates(Date d, Date min, Date max)
    {
        int t1 = d.compareTo(min);
        int t2 = d.compareTo(max);

        if(t1==0||t2==0)
            return true;

        boolean d1 = d.after(min);
        boolean d2 = d.before(max);

        return d1 && d2;
    }

    public static java.sql.Date objectToDate2(Object dateOld)
    {
        Date date = (Date)dateOld;
        return new java.sql.Date(date.getTime());
    }


    //проверка на то что дата в пределах дня для другой даты
    public static boolean approxDate(Date thisDate, Date clickDate)
    {
        Date begDate = addDays(thisDate, -1);
        Date endDate = addDays(thisDate, 1);
        return dateBetweenDates(clickDate, begDate, endDate);
    }


    public static boolean compareDate(Object date1, Object date2)
    {
        if(date1!=null&&date2!=null)
        {
            java.sql.Date dateBeg=new java.sql.Date(java.sql.Date.valueOf(date1.toString()).getTime());
            java.sql.Date dateEnd=new java.sql.Date(java.sql.Date.valueOf(date2.toString()).getTime());
            if(dateEnd.before(dateBeg))
            {
                JOptionPane.showMessageDialog(new JPanel(), "Дата создания больше чем дата завершения!");
                return false;
            }
        }
        return true;
    }



    public static java.sql.Date objectToDate(Object dateOld)
    {
        //две даты
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //две даты
        Date date = new Date();
        try
        {
            date = simpleDateFormat.parse(dateOld.toString());
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
        return new java.sql.Date(date.getTime());
    }


}
