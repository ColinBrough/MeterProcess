/**************************************************************************
 * Class UtilityData.java to...
 *
 * @author Colin Brough
 * @version $Id$
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class UtilityData
{
    //----------------------------------------------------------------------
    // Instance variable - ArrayList holding rates, implicitly ordered by
    // date, rates applying most recently added last to the end of the list.

    private ArrayList<UtilityField> utilityReadings;

    /**********************************************************************
     * Constructor for  objects of class UtilityDate - just creates a new
     * ArrayList internally to hold readings
     */

    public UtilityData()   // Constructor
    {
        utilityReadings = new ArrayList<>();
    }

    /**********************************************************************
     * Add a gas/electricity meter reading for given date; first cut just
     * appends this entry on the end of the array. Better would be to order
     * by date...
     *
     * @param LocalDate this reading on
     * @param double electricity meter reading
     * @param double gas meter reading
     */

    public void addUtilityReading(LocalDate d, double elecMeter, double gasMeter)
    {
        UtilityField uf = new UtilityField();
        uf.date = d;
        uf.gasMeter = gasMeter;
        uf.elecMeter = elecMeter;
        utilityReadings.add(uf);
    }
    
    /**********************************************************************
     * Set one or more readings to be stored in the internal  utilityReadings 
     * ArrayList, potentially integrating those with already present - so
     * storing in date order, and doing some kind of sanity checking if two
     * readings with the same date are read in.
     *
     * @param A file object from which to read data
     */

    public void setReadingsFromFile(File f)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (Scanner sc = new Scanner(f))
        {
            System.out.println("Reading from meter reading file " + f.getName()
                               + " here....");
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                if (line.startsWith("#"))
                {
                    continue;	// Skip past comment lines
                }
                String dateString = line.substring(0,10);
                LocalDate d = LocalDate.parse(dateString, formatter);
                System.out.printf("Util: Date: %s\n", d.toString());
                Scanner scl = new Scanner(line.substring(10));

                this.addUtilityReading(d, scl.nextDouble(),scl.nextDouble());
                scl.close();
            }
            sc.close();

            Collections.sort(utilityReadings);	// Make sure the entries are date sorted
        }
        catch (FileNotFoundException e)
        {
            System.out.println(f.getName() + " file not found");
            System.exit(0);
        }
    }

    /**********************************************************************
     * Print out all of the meter readings currently held in this object
     */

    public void printUtilityReadings()
    {
        for (int i = 0; i < utilityReadings.size(); i++)
        {
            UtilityField ud = utilityReadings.get(i);
            System.out.printf("%3d %s %8.3f %8.3f\n", i,
                              ud.date, ud.gasMeter, ud.elecMeter);
        }
    }
}
