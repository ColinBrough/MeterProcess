/**************************************************************************
 * Class MeterProcessMain.java to serve as entry point for MeterProcess 
 * program
 *
 * @author Colin Brough
 * @version Dev_01
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
        RatesData r = new RatesData(new File("/home/cmb/misc/Home/StationRoad/Utilities/Rates.dat"));

        UtilityData u = new UtilityData();
        u.setReadingsFromFile(new File("/home/cmb/misc/Home/StationRoad/Utilities/elecgas.txt"));
    }
    
}
