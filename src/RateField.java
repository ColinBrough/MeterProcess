/**************************************************************************
 * Class RateField.java to provide a 'struct' like multi-element datum for
 * use by the RatesData class
 *
 * @author Colin Brough
 * @version $Id$
 */

import java.time.LocalDate;

public class RateField
{
    public LocalDate date;	// Date from which these rates apply
    public float gasstanding;	// Daily standing charge, gas
    public float gasunitrate;	// Unit rate, gas
    public float elecstanding;	// Daily standing charge, electric
    public float elecunitrate;	// Unit rate, electric
}
