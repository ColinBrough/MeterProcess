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
    static final String DIRECTORY = "/home/cmb/misc/Home/StationRoad/Utilities/";

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
        u.setReadingsFromFile(new File(DIRECTORY + "MeterReadings.dat"));

        // Then read in and add to the same dataset the more recent stuff from Dropbox...
        u.setReadingsFromFile(new File("/home/cmb/Dropbox/Misc/elecgas.txt"));

        u.interpolateReadings();	// Find the "in-between" meter readings
        u.calculateDailyCosts();	// Calculate all the derived values, inc costs

        // Print daily costs to file for plotting;     argument is output filename
        u.printUtilityCosts("Daily.dat");
        
	// Print weekly data to file for plotting;     argument is output filename
        u.printWeeklyReadings("Weekly.dat");
        
	// Print per-day summary to file for plotting; argument is output filename
        u.printPerDayReadings("DaysOfWeek.dat");
        
	// Print monthly data to file for plotting;    argument is output filename
        u.printMonthlyReadings("Monthly.dat");

        // Print daily cost and usage data to file for each year separately, so they
        // can be compared
        u.printYearOnYearCosts();
        
        //------------------------------------------------------------------
        // Calculate smoothed out data - a new daily value is average of
        // previous 30 days values...
        
        UtilityData uSmooth = new UtilityData();
        uSmooth.setReadingsFromExisting(u);	// Calculates smoothed out values

        uSmooth.printUtilityCosts("SmoothDaily.dat");
        uSmooth.printWeeklyReadings("SmoothWeekly.dat");
        uSmooth.printMonthlyReadings("SmoothMonthly.dat");
        
    }
}
