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
        
        UtilityData u = new UtilityData();	// Implicitly reads the rates data

        // First read the historic data
        u.setReadingsFromFile(new File("/home/cmb/misc/Home/StationRoad/Utilities/MeterReadings.dat"));

        // Then read in and add to the same dataset the more recent stuff from Dropbox...
        u.setReadingsFromFile(new File("/home/cmb/Dropbox/Misc/elecgas.txt"));

        u.interpolateReadings();	// Find the "in-between" meter readings
        u.calculateDailyCosts();	// Calculate all the derived values, inc costs

        u.printUtilityCosts();		// Print daily costs to file for plotting
        u.printWeeklyReadings();	// Print weekly data to file for plotting
        u.printPerDayReadings();	// Print per-day summary to file for plotting
        u.printMonthlyReadings();	// Print monthly data to file for plotting
    }
}
