/**************************************************************************
 * Class UtilityData.java hold and process meter readings, gas and electric
 * and calculate derived values from those - including weekly, monthly and
 * annual values.
 *
 * @author Colin Brough
 * @version Dev_01
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class UtilityData
{
    // Magic number to convert from units (m3) to kWh (which is what tariff figure is)
    static final double GASCONVERSION = 10.8237;

    // The filename of the rates file.
    static final String RatesFilename = "/home/cmb/misc/Home/StationRoad/Utilities/Rates.dat";
    
    //----------------------------------------------------------------------
    // Instance variable - ArrayList holding rates, implicitly ordered by
    // date, rates applying most recently added last to the end of the list.
    // And 

    private ArrayList<UtilityField> utilityReadings;
    private RatesData ratesData;
    
    /**********************************************************************
     * Constructor for  objects of class UtilityDate - just creates a new
     * ArrayList internally to hold readings
     */

    public UtilityData()   // Constructor
    {
        utilityReadings = new ArrayList<>();
        ratesData = new RatesData(new File(RatesFilename));
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

        Collections.sort(utilityReadings);	// Make sure the entries are date sorted
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
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                if (line.startsWith("#"))
                {
                    continue;	// Skip past comment lines
                }
                String dateString = line.substring(0,10);
                LocalDate d = LocalDate.parse(dateString, formatter);
                try (Scanner scl = new Scanner(line.substring(10)))
                {
                    this.addUtilityReading(d, scl.nextDouble(),scl.nextDouble());
                    scl.close();
                }
                catch (NoSuchElementException e)
                {
                    // Do nothing - just skip over
                }
            }
            sc.close();
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
            UtilityField uf = utilityReadings.get(i);
            System.out.printf("%3d %s %8.3f %8.3f\n", i,
                              uf.date, uf.gasMeter, uf.elecMeter);
        }
    }

    /**********************************************************************
     * Print out all of the computed costs for data held in this object
     */

    public void printUtilityCosts()
    {
        for (int i = 0; i < utilityReadings.size(); i++)
        {
            UtilityField uf = utilityReadings.get(i);
            System.out.printf("%3d %s £%6.2f £%6.2f | £%6.2f\n", i,
                              uf.date, uf.gascost, uf.eleccost, uf.totalcost);
        }
    }

    /**********************************************************************
     * Given a populated set of meter readings, run through and add 
     * interpolated readings where there are any gaps.
     */

    public void interpolateReadings()
    {
        if (utilityReadings.size() < 2)
        {
            return;	// Bail if not enough meter reading entries
        }
        int size = utilityReadings.size();
        for (int i = 1; i < size; i++)
        {
            UtilityField uf1, uf2;
            
            uf1 = utilityReadings.get(i-1);
            uf2 = utilityReadings.get(i);
            LocalDate d1 = uf1.date;
            LocalDate d2 = uf2.date;
            long gap = ChronoUnit.DAYS.between(d1, d2) - 1;
            if (gap > 0)
            {
                for (int j = 1; j <= gap; j++)
                {
                    UtilityField uf3 = new UtilityField();
                    uf3.date = d1.plusDays(j);
                    uf3.gasMeter  = uf1.gasMeter  + ((uf2.gasMeter  - uf1.gasMeter)/(gap+1))  * j;
                    uf3.elecMeter = uf1.elecMeter + ((uf2.elecMeter - uf1.elecMeter)/(gap+1)) * j;
                    utilityReadings.add(uf3);
                }
            }
        }
        Collections.sort(utilityReadings);	// Make sure the entries are date sorted
    }

    /**********************************************************************
     * Once we have a populated and interpolated set of meter readings, run
     * through and calculate the derived values - pulling in the relevant
     * rates data, and calculating daily usage and costs.
     */

    public void calculateDailyCosts()
    {
        if (utilityReadings.size() == 0)
        {
            return;
        }

        //------------------------------------------------------------------
        // First calculate the daily usage - difference between "today"'s
        // readings and those from "yesterday"....
        
        UtilityField prev = utilityReadings.get(0);
        prev.gasUsed  = 0.0;
        prev.elecUsed = 0.0;
        
        for (int i = 1; i < utilityReadings.size(); i++)
        {
            UtilityField current = utilityReadings.get(i);

            current.gasUsed  = current.gasMeter  - prev.gasMeter;
            current.elecUsed = current.elecMeter - prev.elecMeter;
            prev = current;
        }

        //------------------------------------------------------------------
        // Next populate the rates fields and the costs

        for (int i = 0; i < utilityReadings.size(); i++)
        {
            UtilityField current = utilityReadings.get(i);

            current.gasstanding  = ratesData.getGasStanding(current.date);
            current.gasunitrate  = ratesData.getGasUnit(current.date);
            current.elecstanding = ratesData.getElecStanding(current.date);
            current.elecunitrate = ratesData.getElecUnit(current.date);

            current.gascost  = current.gasstanding  + current.gasUsed  * current.gasunitrate * GASCONVERSION;
            current.eleccost = current.elecstanding + current.elecUsed * current.elecunitrate;
            current.totalcost = current.gascost + current.eleccost;
        }
    }
    
    /**********************************************************************
     * Find the index in meter readings, given date. Inefficient linear search!
     *
     * @param A date to look for the index in ArrayList where readings etc stored
     * @return A positive integer if an index found, and -1 if not
     */

    public int indexFromDate(LocalDate d)
    {
        for (int i = 0; i < utilityReadings.size(); i++)
        {
            if (d.isEqual(utilityReadings.get(i).date))
            {
                return i;
            }
            if (d.isBefore(utilityReadings.get(i).date))
            {
                return -1;
            }
        }
        return -1;
    }
}
