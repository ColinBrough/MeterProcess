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
        LocalDate d = LocalDate.of(2023, 5, 1);
        
        UtilityData u = new UtilityData();
        // u.setReadingsFromFile(new File("/home/cmb/misc/Home/StationRoad/Utilities/elecgas.txt"));
        u.setReadingsFromFile(new File("/home/cmb/Dropbox/Misc/elecgas.txt"));
        System.out.printf("\n\n----------------------------------------------------------------------\n\n");
        u.interpolateReadings();	// Find the "in-between" meter readings
        u.calculateDailyCosts();	// Calculate all the derived values, inc costs
        u.printUtilityReadings();
        u.printUtilityCosts();
        System.out.printf("Index of %s is %d\n", d, u.indexFromDate(d));
        
    }
}
