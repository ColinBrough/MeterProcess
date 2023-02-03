/**************************************************************************
 * Class cmbDate.java to store and work on simple dates; exercise in trying
 * things out - java.time.LocalDate does the same job!
 *
 * @author Colin Brough
 * @version Dev_01
 */

public class cmbDate implements Comparable<cmbDate>
{
    //----------------------------------------------------------------------
    // Instance variables - day, month and year
    
    private int day;
    private int month;
    private int year;
    
    /**********************************************************************
     * Constructor for objects of class cmbDate, checking for validity
     *
     * @param nYear the year of this object
     * @param nMonth the month of this object
     * @param nDay the day of this object
     */

    public cmbDate(int nYear, int nMonth, int nDay) throws IllegalArgumentException
    {
        if (cmbDate.isDate(nYear, nMonth, nDay))
        {
            year  = nYear;
            month = nMonth;
            day   = nDay;
        }
        else
        {
            throw new IllegalArgumentException("Invalid date");
        }
    }

    /**********************************************************************
     * Get the year
     *
     * @return the year, as integer
     */

    public int getYear()
    {
        return year;
    }

    /**********************************************************************
     * Get the month
     *
     * @return the month, as integer
     */

    public int getMonth()
    {
        return month;
    }

    /**********************************************************************
     * Get the day
     *
     * @return the day, as integer
     */

    public int getDay()
    {
        return day;
    }

    /**********************************************************************
     * DaysInMonth	Routine to return the number of days in a month, 
     *	                given month and year
     *
     * @return integer number of days in month, zero if month out of range
     */

    static int DaysInMonth(int y, int m)
    {
        int days_in_month[]={ 0,31,28,31,30,31,30,31,31,30,31,30,31 };

        if ((m < 1) || (m > 12))
        {
            return 0; // zero indicates month was out of range
        }
        // Check for leap years...
        if (((y % 4 == 0) && !((y % 100 == 0) && (y % 400 != 0))) && (m == 2))
        {
            return 29;	// February has 29 days in a leap year
        }
        return(days_in_month[m]);
    }

    /**********************************************************************
     * isDate is an internal method within the class that returns true if
     * the passed in date (integers, year/month/day) represents a valid 
     * date
     */

    private static boolean isDate(int y, int m, int d)
    {
        if ((y < 1900) || (y > 2099))
        {
            return false;
        }
        if ((m < 1) || (m > 12))
        {
            return false;
        }
        if ((d < 1) || (d > DaysInMonth(y, m)))
        {
            return false;
        }
        return true;
    }

    /**********************************************************************
     * incDate - increment the date by a certain number of days. Without
     * argument, increment by one day.
     */

    public void incDate()
    {
        int d, m, y;
        d = day + 1;
        m = month;
        y = year;
        if (d > DaysInMonth(y, m))
        {
            d = 1;
            m++;
            if (m > 12)
            {
                m = 1;
                y++;
            }
        }
        year  = y;
        month = m;
        day   = d;
    }
    
    /**********************************************************************
     * incDate - increment the date by a the number of days passed in as
     * argument.
     *
     * @param integer number of days to increment date by
     */

    public void incDate(int days)
    {
        int d, m, y, inc = 1;
        if (days == 0)	// Return immediately if increment is zero
        {
            return;
        }
        if (days < 0)	// If argument is negative, set increment to -1
        {		// and make the count of days to the absolute
            inc = -1;	// value...
            days = java.lang.Math.abs(days);
        }
        d = day;
        m = month;
        y = year;
        for (int i = 0; i < days; i++)
        {
            d+= inc;
            if (d > DaysInMonth(y, m))
            {
                d = 1;
                m++;
                if (m > 12)
                {
                    m = 1;
                    y++;
                }
            }
        }
        year  = y;
        month = m;
        day   = d;
    }

    /**********************************************************************
     * Method to compare two dates; fulfils requirements of Comparable 
     * interface
     *
     * @param date to compare this object's date to
     * @return 0 for equal, -1 for less, +1 for more
     */

    @Override
    public int compareTo(cmbDate d2)
    {
        if (year > d2.getYear())
        {
            return 1;
        }
        if (year < d2.getYear())
        {
            return -1;
        }
        if (month > d2.getMonth())
        {
            return 1;
        }
        if (month < d2.getMonth())
        {
            return -1;
        }
        if (day > d2.getDay())
        {
            return 1;
        }
        if (day < d2.getDay())
        {
            return -1;
        }
        return 0;
    }

    /**********************************************************************
     * Checks if this cmbDate date is equal to another date. 
     *
     * @param the object to test, NULL returns false
     * @return true if this is equal to the other date
     */

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true; // Reference equality
        }
        if (obj instanceof cmbDate)
        {
            cmbDate d = (cmbDate) obj;
            return day   == d.getDay()   &&
                   month == d.getMonth() &&
                   year  == d.getYear();
        }
        return false;
    }
    
      
}
