/**************************************************************************
 * Class RatesData.java to store and process Rates data - date from which
 * rates apply, standing charges for gas and electric, and daily rates for
 * gas and electric.
 *
 * @author Colin Brough
 * @version Dev_01
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class RatesData
{
    //----------------------------------------------------------------------
    // Instance variable - ArrayList holding rates, implicitly ordered by
    // date, rates applying most recently added last to the end of the list.

    private ArrayList<RatesField> rates;
    
    /**********************************************************************
     * Constructor for objects of class RatesData, no arguments
     */

    public RatesData()
    {
        rates = new ArrayList<>();
    }

    /**********************************************************************
     * Constructor for objects of class RatesData, with an inital set of
     * rates data passed in as arguments
     *
     * @param double gas daily standing charge
     * @param double gas unit price
     * @param double electric daily standing charge
     * @param double electric unit price
     * @param LocalDate date these rates apply from
     */
    
    public RatesData(double gasStanding, double gasUnit, double elecStanding,
                     double elecUnit, LocalDate d)
    {
        rates = new ArrayList<>();
        this.addRate(gasStanding, gasUnit, elecStanding, elecUnit, d);
    }

    /**********************************************************************
     * Constructor method for rates data - pulls information in from a file,
     * whose name is passed in as argument
     *
     * @param a file to read rates data from
     */

    public RatesData(File f)
    {
        rates = new ArrayList<>();

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
                Scanner scl = new Scanner(line.substring(10));
                
                this.addRate(scl.nextDouble(), // Gas standing charge
                             scl.nextDouble(), // Gas unit price
                             scl.nextDouble(), // Electric standing charge
                             scl.nextDouble(), // Electric unit price
                             d);               // Date
                scl.close();
            }
            sc.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Rates.dat file not found");
            System.exit(0);
        }
    }

    /**********************************************************************
     * Add a set of rates data - standing charges and daily rates as they
     * apply from the given dates. Assumption is that rates are added in
     * date order, so most recent date is added last - and ends up on the
     * end of the 'rates' ArrayList.
     *
     * @param double gas daily standing charge
     * @param double gas unit price
     * @param double electric daily standing charge
     * @param double electric unit price
     * @param LocalDate date these rates apply from
     */

    public void addRate(double gasStanding, double gasUnit, double elecStanding,
                        double elecUnit, LocalDate d)
    {
        RatesField rateE = new RatesField();
        rateE.date = d;
        rateE.gasstanding  = gasStanding;
        rateE.gasunitrate  = gasUnit;
        rateE.elecstanding = elecStanding;
        rateE.elecunitrate = elecUnit;
        rates.add(rateE);
        Collections.sort(rates);	// Make sure entries are date sorted
    }

    /**********************************************************************
     * Private method to return the RatesField that applies to the date
     * passed in. Start at most recent (last in 'rates' ArrayList), and
     * work down till the date passed isn't less than the one in the
     * RatesField. Used by the getters for applicable gas/electric rates.
     */

    public RatesField getRate(LocalDate d)
    {
        if (rates.size() == 0)
        {
            return null;
        }
        for (int i = rates.size()-1; i >= 0; i--)
        {
            if (! d.isBefore(rates.get(i).date))
            {
                return rates.get(i);
            }
        }
        return null;	// Return null if no valid rate found
    }
    
    /**********************************************************************
     * Returns the gas standing charge that applies on the date passed in
     *
     * @param date for which gas standing charge is returned
     * @return double indicating gas standing charge applying on this date
     */

    public double getGasStanding(LocalDate d)
    {
        RatesField r = this.getRate(d);
        
        if (r == null)
        {
            return 0;
        }
        return r.gasstanding;
    }
    
    /**********************************************************************
     * Returns the gas unit price that applies on the date passed in
     *
     * @param date for which gas unit price is returned
     * @return double indicating gas unit price applying on this date
     */

    public double getGasUnit(LocalDate d)
    {
        RatesField r = this.getRate(d);
        
        if (r == null)
        {
            return 0;
        }
        return r.gasunitrate;
    }
    
    /**********************************************************************
     * Returns the electricity standing charge that applies on the date passed in
     *
     * @param date for which electric standing charge is returned
     * @return double indicating electric standing charge applying on this date
     */

    public double getElecStanding(LocalDate d)
    {
        RatesField r = this.getRate(d);
        
        if (r == null)
        {
            return 0;
        }
        return r.elecstanding;
    }
    
    /**********************************************************************
     * Returns the electricity unit price that applies on the date passed in
     *
     * @param date for which electric unit price is returned
     * @return double indicating electric unit price applying on this date
     */

    public double getElecUnit(LocalDate d)
    {
        RatesField r = this.getRate(d);
        
        if (r == null)
        {
            return 0;
        }
        return r.elecunitrate;
    }
}
