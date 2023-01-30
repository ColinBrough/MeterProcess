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
        cmbDate date;
        date = new cmbDate(2023, 1, 30);
        date.incDate(-2);
        
        System.out.printf("Date is %02d/%02d/%d\n",
                          date.getDay(), date.getMonth(), date.getYear());
    }

    
}
