/**************************************************************************
 * Class MeterProcessMain.java to serve as entry point for MeterProcess 
 * program
 *
 * @author Colin Brough
 * @version $Id$
 */

public class MeterProcessMain
{
    /**********************************************************************
     * main method - program entry point
     *
     * @param array of strings, program arguments
     */

    public static void main(String[] args)
    {
        char Grtr[] = { '<', '=', '>' };
                        
        cmbDate date1, date2;
        date1 = new cmbDate(2023, 1, 30);
        date2 = new cmbDate(2023, 1, 30);
        date1.incDate(2);
        date2.incDate(-2);
        
        System.out.printf("Date1 is %02d/%02d/%d\n",
                          date1.getDay(), date1.getMonth(), date1.getYear());
        System.out.printf("Date2 is %02d/%02d/%d\n",
                          date2.getDay(), date2.getMonth(), date2.getYear());
        System.out.printf("Date1 is %c than Date2\n", Grtr[date1.compareTo(date2) + 1]);
    }

    
}
