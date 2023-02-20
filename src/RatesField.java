/**************************************************************************
 * Class RatesField.java to provide a 'struct' like multi-element datum for
 * use by the RatesData class
 *
 * @author Colin Brough
 * @version Dev_01
 */

import java.time.LocalDate;

public class RatesField implements Comparable<RatesField>
{
    public LocalDate date;	// Date from which these rates apply
    public double gasstanding;	// Daily standing charge, gas
    public double gasunitrate;	// Unit rate, gas
    public double elecstanding;	// Daily standing charge, electric
    public double elecunitrate;	// Unit rate, electric

    //------------------------------------------------------------------
    // Equality method
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof RatesField)
        {
            RatesField rf = (RatesField) obj;
            return date.equals(rf.date) &&
                gasstanding  == rf.gasstanding  &&
                gasunitrate  == rf.gasunitrate  &&
                elecstanding == rf.elecstanding &&
                elecunitrate == rf.elecunitrate;
        }
        return false;
    }
    
    //------------------------------------------------------------------
    // Comparison function for Comparable interface

    @Override
    public int compareTo(RatesField otherRates)
    {
        return date.compareTo(otherRates.date);
    }
}
