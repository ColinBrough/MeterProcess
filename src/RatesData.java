/**************************************************************************
 * Class RatesData.java to store and process Rates data - date from which
 * rates apply, standing charges for gas and electric, and daily rates for
 * gas and electric.
 *
 * @author Colin Brough
 * @version $Id$
 */

import java.time.LocalDate;
import java.util.ArrayList;

public class RatesData
{
    //----------------------------------------------------------------------
    // Instance variable - ArrayList holding rates, implicitly ordered by
    // date, rates applying most recently added last to the end of the list.

    private ArrayList<RateField> rates;
    
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
        RateField rateE = new RateField();
        rateE.date = d;
        rateE.gasstanding  = gasStanding;
        rateE.gasunitrate  = gasUnit;
        rateE.elecstanding = elecStanding;
        rateE.elecunitrate = elecUnit;
        rates.add(rateE);
    }

    /**********************************************************************
     * Private method to return the RateField that applies to the date
     * passed in. Start at most recent (last in 'rates' ArrayList), and
     * work down till the date passed isn't less than the one in the
     * RateField. Used by the getters for applicable gas/electric rates.
     */

    private RateField getRate(LocalDate d)
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
        RateField r = this.getRate(d);
        
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
        RateField r = this.getRate(d);
        
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
        RateField r = this.getRate(d);
        
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
        RateField r = this.getRate(d);
        
        if (r == null)
        {
            return 0;
        }
        return r.elecunitrate;
    }
}
