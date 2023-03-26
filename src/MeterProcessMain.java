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
        u.setReadingsFromFile(new File("/home/cmb/misc/Home/StationRoad/Utilities/elecgas.txt"));
        // Then read in and add to the same dataset the more recent stuff from Dropbox...
        u.setReadingsFromFile(new File("/home/cmb/Dropbox/Misc/elecgas.txt"));

        System.out.printf("\n\n----------------------------------------------------------------------\n\n");
        u.interpolateReadings();	// Find the "in-between" meter readings
        u.calculateDailyCosts();	// Calculate all the derived values, inc costs
        u.printUtilityReadings();	// Print daily readings/usage
        u.printUtilityCosts();		// Print daily costs
        u.printWeeklyReadings();	// Print out the weekly data
        
        System.out.printf("Index of %s is %d\n", d, u.indexFromDate(d));

        UtilityField uf = u.getGasData(d, 1);
        
        //        System.out.printf("\n\n---- Code here tests calling of external program ---------------------\n\n");

        //        try
        //{
        //    ProcessBuilder pb = new ProcessBuilder("/usr/bin/cp",
        //                                           "/home/cmb/tmp/proportion.dat",
        //                                           "/home/cmb/tmp/proportion2.dat");
        //    pb.directory(new File("/home/cmb/tmp"));
        //    Process p = pb.start();
        //    System.out.println("External program execution succeeded");
        //}
        //catch (IOException e)
        //{
        //    System.out.println("IO Exception on running external program");
        //}
    }
}
