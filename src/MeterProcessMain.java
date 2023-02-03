/**************************************************************************
 * Class MeterProcessMain.java to serve as entry point for MeterProcess 
 * program
 *
 * @author Colin Brough
 * @version $Id$
 */

import java.time.*;
import java.io.*;

public class MeterProcessMain
{
    /**********************************************************************
     * main method - program entry point
     *
     * @param array of strings, program arguments
     */

    public static void main(String[] args)
    {
        LocalDate d1 = LocalDate.of(2023,1, 1);
        LocalDate d2 = LocalDate.of(2023,1,31);
        LocalDate d3 = LocalDate.of(2023,2, 1);
        LocalDate d4 = LocalDate.of(2023,3, 1);
        
        RatesData r = new RatesData(1.0,  2.0,  3.0,  4.0, d2);
        r.addRate(5.0,  6.0,  7.0,  8.0, d3);
        r.addRate(9.0, 10.0, 11.0, 12.0, d4);

        System.out.printf("d1 = %s : %f\nd2 = %s : %f\nd3 = %s : %f\nd4 = %s : %f\n",
                          d1.toString(), r.getGasStanding(d1),
                          d2.toString(), r.getGasStanding(d2),
                          d3.toString(), r.getGasStanding(d3),
                          d4.toString(), r.getGasStanding(d4));
        RatesData r2 = new RatesData(new File("/home/cmb/misc/Home/StationRoad/Utilities/Rates.dat"));
        System.out.printf("d1 = %s : %f\nd2 = %s : %f\nd3 = %s : %f\nd4 = %s : %f\n",
                          d1.toString(), r2.getGasStanding(d1),
                          d2.toString(), r2.getGasStanding(d2),
                          d3.toString(), r2.getGasStanding(d3),
                          d4.toString(), r2.getGasStanding(d4));

    }

    
}
