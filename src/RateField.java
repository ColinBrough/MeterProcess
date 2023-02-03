/**************************************************************************
 * Class RateField.java to provide a 'struct' like multi-element datum for
 * use by the RatesData class
 *
 * @author Colin Brough
 * @version Dev_01
 */

import java.time.LocalDate;

public class RateField
{
    public LocalDate date;	// Date from which these rates apply
    public double gasstanding;	// Daily standing charge, gas
    public double gasunitrate;	// Unit rate, gas
    public double elecstanding;	// Daily standing charge, electric
    public double elecunitrate;	// Unit rate, electric
}
