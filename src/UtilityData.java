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
    // Multiplication factor representing additional cost of VAT on gas and electricity costs
    static final double VAT = 1.05;
    
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
     * Getter for UtilityField data at a given date
     *
     * @param The date for which data should be returned
     * @return UtilityField object, if present, null if none
     */

    public UtilityField getGasData(LocalDate d)
    {
        for (int i = 0; i < utilityReadings.size(); i++)
        {
            if (utilityReadings.get(i).date == d)
            {
                return utilityReadings.get(i);
            }
            if (utilityReadings.get(i).date.compareTo(d) > 0)
            {
                return null;
            }
        }
        return null;
    }
    
    /**********************************************************************
     * Getter for UtilityField data at a given date, plus an increment (0..6)
     *
     * @param d, the date for which data should be returned
     * @param inc, the increment in days beyond given date
     * @return UtilityField object, if present, null if none
     */

    public UtilityField getGasData(LocalDate d, int inc)
    {
        if ((inc < 0) || (utilityReadings.size() == 0))
        {
            return null;
        }
        
        long days = ChronoUnit.DAYS.between(utilityReadings.get(0).date, d);
        System.out.printf("Days between %s and %s = %d\n", d, utilityReadings.get(0).date, days);
        return null;
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
     * to the standard output
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
     * Print out all of the computed costs for data held in this object to
     * file from which these can be plotted
     */

    public void printUtilityCosts()
    {
        try
        {
            PrintStream stream = new PrintStream("/home/cmb/misc/Home/StationRoad/Utilities/GeneratedFiles/Daily.dat");

            stream.printf("#   Date          Gas Electric  Total\n" +
                          "#------------------------------------------\n");
            
            for (int i = 0; i < utilityReadings.size(); i++)
            {
                UtilityField uf = utilityReadings.get(i);
                stream.printf("%3d %s %6.2f %8.2f %6.2f\n", i,
                              uf.date, uf.gascost, uf.eleccost, uf.totalcost);
            }
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            // Print an error message, but otherwise do nothing
            System.out.println("Unable to open 'Daily.dat' for writing");
        }
    }

    /**********************************************************************
     * Output meter data (usage, cost) for whole weeks. Any initial partial
     * week is ignored. Full weeks are output, and the last (possibly 
     * partial) week is output. This depends on every day in the ArrayList
     * being filled in (ie after successful interpolation). Output goes to
     * file.
     */

    public void printWeeklyReadings()
    {
        try
        {
            PrintStream stream = new PrintStream("/home/cmb/misc/Home/StationRoad/Utilities/GeneratedFiles/Weekly.dat");

            if (utilityReadings.size() == 0)
            {
                System.out.println("WARNING: no utility data present\n");
                return;
            }
            UtilityField uf0 = utilityReadings.get(0);
            int DoW = uf0.date.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

            int FirstFullWeek = 7 - (DoW - 1);	// Index in ArrayList where first full week starts
            int WeekCount = 0;
            stream.printf("# Wk  Date    S     Gas Use    £Gas   Elec Use    £Elec    £Total\n" +
                          "#-------------------------------------------------------------------\n");
            for (int i = FirstFullWeek; i < utilityReadings.size(); i += 7)
            {
                int span = 7;
                if ((i + 7) >= utilityReadings.size())
                {
                    span = utilityReadings.size() - i;
                }
                double gcost = 0.0, ecost = 0.0, tcost = 0.0, gused = 0.0, eused = 0.0;
                for (int j = 0; j < span; j++)
                {
                    gcost += utilityReadings.get(i+j).gascost;
                    ecost += utilityReadings.get(i+j).eleccost;
                    tcost += utilityReadings.get(i+j).totalcost;
                    gused += utilityReadings.get(i+j).gasUsed;
                    eused += utilityReadings.get(i+j).elecUsed;
                }
                
                stream.printf("%2d %s %d %10.3f %8.3f %10.3f %8.3f %9.3f\n",
                              WeekCount++, utilityReadings.get(i).date, span,
                              gused, gcost, eused, ecost, tcost);
            }
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            // Print an error message, but otherwise do nothing
            System.out.println("Unable to open 'Weekly.dat' for writing");
        }
    }

    /**********************************************************************
     * Print to file the per day (Mon/Tue/Wed/...) daily usage and costs,
     * so can plot on which days we use most/least energy. Need to calculate
     * the information before we can output it.
     *
     */

    public void printPerDayReadings()
    {
	// Usage on each day of week (1..7); not all fields populated
        UtilityField DailyUsage[] = new UtilityField[8];
        // Number of days of data collected for each day of week (1..7)
        int NumDays[] = new int[8];
        for (int i = 1; i <=7; i++)
        {
            DailyUsage[i] = new UtilityField();
            DailyUsage[i].gasUsed   = 0.0;	// Other unused fields
            DailyUsage[i].elecUsed  = 0.0;	// left unset...
            DailyUsage[i].gascost   = 0.0;
            DailyUsage[i].eleccost  = 0.0;
            DailyUsage[i].totalcost = 0.0;
            NumDays[i] = 0;
        }
        
	// Around when we moved in, after dehumidifiers stopped, so electricity usage
        // should be coming down. 
        LocalDate d = LocalDate.of(2022, 8, 1);
        for (int i = 0; i < utilityReadings.size(); i++)
        {
            UtilityField uf = utilityReadings.get(i);
            if (uf.date.compareTo(d) > 0)	// After 1st August, so more normal usage!
            {
                int DoW = uf.date.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
                
                NumDays[DoW]++;
                DailyUsage[DoW].gasUsed   += uf.gasUsed;
                DailyUsage[DoW].elecUsed  += uf.elecUsed;
                DailyUsage[DoW].gascost   += uf.gascost;
                DailyUsage[DoW].eleccost  += uf.eleccost;
                DailyUsage[DoW].totalcost += uf.totalcost;
            }
        }
        try
        {
            PrintStream stream = new PrintStream("/home/cmb/misc/Home/StationRoad/Utilities/GeneratedFiles/DaysOfWeek.dat");

            stream.printf("#   Gas Used  Elec Used     £Gas    £Elec   £Total\n" +
                          "#------------------------------------------------------\n");
            for (int i = 1; i <=7; i++)
            {
                stream.printf("%d %10.3f %10.3f %8.2f %8.2f %8.2f\n", i,
                              DailyUsage[i].gasUsed   / NumDays[i],
                              DailyUsage[i].elecUsed  / NumDays[i],
                              DailyUsage[i].gascost   / NumDays[i],
                              DailyUsage[i].eleccost  / NumDays[i],
                              DailyUsage[i].totalcost / NumDays[i]);
            }
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            // Print an error message, but otherwise do nothing
            System.out.println("Unable to open 'DaysOfWeek.dat' for writing");
        }
    }

    /**********************************************************************
     * Print to file the monthly usage and costs data, so we can plot. Have
     * to compute them on the fly
     */

    public void printMonthlyReadings()
    {
        int year, month;
        double gasUsed = 0.0, elecUsed = 0.0, gasCost = 0.0, elecCost = 0.0, totalCost = 0.0;
        UtilityField uf = null;

        try
        {
            PrintStream stream = new PrintStream("/home/cmb/misc/Home/StationRoad/Utilities/GeneratedFiles/Monthly.dat");
            stream.printf("# Month   Gas Used  Elec Used    £Gas   £Elec  £Total\n" +
                          "#-------------------------------------------------------\n");
            
            for (int i = 0; i < utilityReadings.size(); i++)
            {
                uf = utilityReadings.get(i);
                if ((uf.date.getDayOfMonth() == 1) && (i > 0))
                {
                    int m = uf.date.getMonthValue() - 1;
                    int y = uf.date.getYear();
                    if (m == 0)
                    {
                        y--;
                        m = 12;
                    }
                    
                    stream.printf("%02d-%04d %10.3f %10.3f %7.2f %7.2f %7.2f\n", m, y,
                                  gasUsed, elecUsed, gasCost, elecCost, totalCost);
                    gasUsed   = 0.0;
                    elecUsed  = 0.0;
                    gasCost   = 0.0;
                    elecCost  = 0.0;
                    totalCost = 0.0;
                }
                gasUsed   += uf.gasUsed;
                elecUsed  += uf.elecUsed;
                gasCost   += uf.gascost;
                elecCost  += uf.eleccost;
                totalCost += uf.totalcost;
            }
            stream.printf("%02d-%04d %10.3f %10.3f %7.2f %7.2f %7.2f\n",
                          uf.date.getMonthValue(), uf.date.getYear(),
                          gasUsed, elecUsed, gasCost, elecCost, totalCost);
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            // Print an error message, but otherwise do nothing
            System.out.println("Unable to open 'Monthly.dat' for writing");
        }
    }

    /**********************************************************************
     * Print to file for plotting the per-month costs so can plot in
     * which months we use most/least energy. Need to calculate the
     * information before we can output it.
     */

    public void printPerMonthReadings()
    {
	// Usage on each month of the year (1..12); not all fields populated
        UtilityField MonthlyUsage[] = new UtilityField[13];
        // Number of months of data collected for each month (1..12)
        int NumMonths[] = new int[13];
        for (int i = 1; i <=7; i++)
        {
            MonthlyUsage[i] = new UtilityField();
            MonthlyUsage[i].gasUsed   = 0.0;	// Other unused fields
            MonthlyUsage[i].elecUsed  = 0.0;	// left unset...
            MonthlyUsage[i].gascost   = 0.0;
            MonthlyUsage[i].eleccost  = 0.0;
            MonthlyUsage[i].totalcost = 0.0;
            NumMonths[i] = 0;
        }
        
	// Around when we moved in, after dehumidifiers stopped, so electricity usage
        // should be coming down...
        LocalDate d = LocalDate.of(2022, 8, 1);
        for (int i = 0; i < utilityReadings.size(); i++)
        {
            UtilityField uf = utilityReadings.get(i);
            if (uf.date.compareTo(d) > 0)	// After 1st August, so more normal usage!
            {
                int month = uf.date.getMonthValue(); // 1..12
                
                NumMonths[month]++;
                MonthlyUsage[month].gasUsed   += uf.gasUsed;
                MonthlyUsage[month].elecUsed  += uf.elecUsed;
                MonthlyUsage[month].gascost   += uf.gascost;
                MonthlyUsage[month].eleccost  += uf.eleccost;
                MonthlyUsage[month].totalcost += uf.totalcost;
            }
        }
        try
        {
            PrintStream stream = new PrintStream("/home/cmb/misc/Home/StationRoad/Utilities/GeneratedFiles/DaysOfWeek.dat");

            stream.printf("#   Gas Used  Elec Used     £Gas    £Elec   £Total\n" +
                          "#------------------------------------------------------\n");
            for (int i = 1; i <=7; i++)
            {
                stream.printf("%d %10.3f %10.3f %8.2f %8.2f %8.2f\n", i,
                              MonthlyUsage[i].gasUsed   / NumMonths[i],
                              MonthlyUsage[i].elecUsed  / NumMonths[i],
                              MonthlyUsage[i].gascost   / NumMonths[i],
                              MonthlyUsage[i].eleccost  / NumMonths[i],
                              MonthlyUsage[i].totalcost / NumMonths[i]);
            }
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            // Print an error message, but otherwise do nothing
            System.out.println("Unable to open 'DaysOfWeek.dat' for writing");
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

            current.gascost  = VAT * ( current.gasstanding  + current.gasUsed  * current.gasunitrate * GASCONVERSION );
            current.eleccost = VAT * ( current.elecstanding + current.elecUsed * current.elecunitrate );
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
