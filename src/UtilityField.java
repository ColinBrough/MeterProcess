/**************************************************************************
 * Class UtilityField.java to provide a 'struct' like multi-element datum
 * for use by the UtilityData class
 *
 * @author Colin Brough
 * @version Dev_01
 */

import java.time.LocalDate;

public class UtilityField
{
    //----------------------------------------------------------------------
    // Each meter reading entry holds a date and gas/electric meter readings;
    // all other values are computable from these - I'll either make this a
    // class with methods, some of which compute costs etc, and do the data
    // hiding thing, or put those methods in the UtilityData class...
    
    public LocalDate date;	// Date of these readings
    public double gasMeter;	// Gas meter reading
    public double elecMeter;	// Electric meter reading
}
