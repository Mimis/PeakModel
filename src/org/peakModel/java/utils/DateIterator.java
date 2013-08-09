package org.peakModel.java.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class DateIterator
   implements Iterator<Date>, Iterable<Date>
{

    private Calendar end = Calendar.getInstance();
    private Calendar current = Calendar.getInstance();

    public DateIterator(Date start, Date end)
    {
        this.end.setTime(end);
        this.end.add(Calendar.DATE, -1);
        this.current.setTime(start);
        this.current.add(Calendar.DATE, -1);
    }

    public boolean hasNext()
    {
        return !current.after(end);
    }

    public Date next()
    {
        current.add(Calendar.DATE, 1);
        return current.getTime();
    }

    public void remove()
    {
        throw new UnsupportedOperationException(
           "Cannot remove");
    }

    public Iterator<Date> iterator()
    {
        return this;
    }

    public static List<String> getDatesForGivenInterval(String startDate,String endDate) throws ParseException{
    	List<String> dateList = new ArrayList<String>();
    	dateList.add(startDate);
        GregorianCalendar gcal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);
        gcal.setTime(start);
        while (gcal.getTime().before(end)) {
            gcal.add(Calendar.DAY_OF_YEAR, 1);
            String year = String.valueOf(gcal.get(Calendar.YEAR));
            String month = String.valueOf(gcal.get(Calendar.MONTH)+1);
            String day = String.valueOf(gcal.get(Calendar.DAY_OF_MONTH));
            
            month = month.length() == 2 ? month : "0"+month;
            day = day.length() == 2 ? day : "0"+day;
            String currentDate = year+"-"+month+"-"+day;
            dateList.add(currentDate);
        }
        return dateList;
    }
    
    
    public static void main(String[] args) throws ParseException
    {
    	List<String> dateList = getDatesForGivenInterval("1981-01-01", "1981-12-31");
    	for(String d: dateList)
    		System.out.println(d);
    }
} 