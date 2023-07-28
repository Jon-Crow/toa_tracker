package crow.jonathan.toatracker;

import java.util.ArrayList;

public class Calendar
{
    public static final int MONTHS_PER_YEAR = 12;
    public static final int DAYS_PER_MONTH = 30;
    public static final int DAYS_PER_YEAR = MONTHS_PER_YEAR*DAYS_PER_MONTH;

    private Date date;
    private ArrayList<Event> events;
    
    public Calendar()
    {
        this(new Date());
    }
    public Calendar(Date date)
    {
        events = new ArrayList<>();
        this.date = date;
    }
    public Date getDate()
    {
        return date;
    }
    public int getEventCount()
    {
        return events.size();
    }
    public Event getEvent(int idx)
    {
        return events.get(idx);
    }
    public Event addEvent(String name, String desc, Date start, Date end)
    {
        Event event = new Event(name, desc, start, end);
        events.add(event);
        return event;
    }
    public void removeEvent(Event event)
    {
        events.remove(event);
    }
    public void removeEvent(int idx)
    {
        events.remove(idx);
    }
    public ArrayList<Event> getEvents(Date date)
    {
        ArrayList<Event> eventList = new ArrayList<>();
        for(Event event : events)
        {
            if(event.includes(date))
                eventList.add(event);
        }
        return eventList;
    }
    public ArrayList<Event> getEvents(Date start, Date end)
    {
        if(start.equals(end))
            return getEvents(start);
        
        ArrayList<Event> eventList = new ArrayList<>();
        if(start.isAfter(end))
            return eventList;
        
        Date date = start;
        while(true)
        {
            eventList.addAll(getEvents(date));
            
            if(date.equals(end))
                return eventList;
            
            date = date.getNextDay();
        }
    }
    
    public static final class Event
    {
        private String name,
                       desc;
        private Date start,
                     end;
        
        private Event(String name, String desc, Date start, Date end)
        {
            this.name = name;
            this.desc = desc;
            this.start = start;
            this.end = end;
        }
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public String getDescription()
        {
            return desc;
        }
        public void setDescription(String desc)
        {
            this.desc = desc;
        }
        public Date getStartDate()
        {
            return start;
        }
        public Date getEndDate()
        {
            return end;
        }
        public boolean isSingleDay()
        {
            return start.equals(end);
        }
        public boolean includes(Date date)
        {
            return start.equals(date) ||
                   end.equals(date) ||
                   (start.isBefore(date) && end.isAfter(date));
        }
    }
    public static final class Date
    {
        private int day,
                    year;
    
        public Date()
        {
            this(0, 0);
        }
        public Date(int year, int day)
        {
            setDayOfYear(day);
            this.year = year;
        }
        public Date(int year, Month month, int day)
        {
            this.year = year;
            setMonthAndDay(month.monthOfYear, day);
        }
        public int getYear()
        {
            return year;
        }
        public void setYear(int year)
        {
            this.year = year;
        }
        public final void setDayOfYear(int day)
        {
            this.day = Math.abs(day)%DAYS_PER_YEAR;
        }
        public int getDayOfYear()
        {
            return day;
        }
        public int getDayOfMonth()
        {
            return day%DAYS_PER_MONTH;
        }
        public int getAbsoluteDay()
        {
            return year*DAYS_PER_YEAR+day;
        }
        public Month getMonth()
        {
            int monthIdx = getDayOfYear()/DAYS_PER_MONTH;
            return Month.values()[monthIdx];
        }
        public void setMonthAndDay(int month, int day)
        {
            setDayOfYear(month*DAYS_PER_MONTH + day);
        }
        public Date getNextDay()
        {
            Date next = new Date(year, day+1);
            if(next.day == 0)
                next.year++;
            return next;
        }
        public boolean isBefore(Date date)
        {
            return getAbsoluteDay() < date.getAbsoluteDay();
        }
        public boolean isAfter(Date date)
        {
            return getAbsoluteDay() > date.getAbsoluteDay();
        }
        public void set(Date date)
        {
            this.day = date.day;
            this.year = date.year;
        }
        @Override
        public boolean equals(Object obj)
        {
            if(obj instanceof Date)
                return getAbsoluteDay() == ((Date)obj).getAbsoluteDay();
            return false;
        }
        @Override
        public String toString()
        {
            return String.format("%d of %s, %dDR", getDayOfMonth()+1, getMonth().stringVal, getYear());
        }
    }
    public static enum Month
    {
        HAMMER("Hammer",0),
        ALTURIAK("Alturiak",1),
        CHES("Ches",2),
        TARSAKH("Tarsakh",3),
        MIRTUL("Mirtul",4),
        KYTHORN("Kythorn",5),
        FLAMERULE("Flamerule",6),
        ELEASIS("Eleasis",7),
        ELEINT("Eleint",8),
        MARPENOTH("Marpenoth",9),
        UKTAR("Uktar",10),
        NIGHTAL("Nightal",11);
        
        public final String stringVal;
        public final int monthOfYear;
        
        private Month(String stringVal, int monthOfYear)
        {
            this.stringVal = stringVal;
            this.monthOfYear = monthOfYear;
        }
        public static Month getNext(Month month)
        {
            switch(month)
            {
                case HAMMER:
                    return ALTURIAK;
                case ALTURIAK:
                    return CHES;
                case CHES:
                    return TARSAKH;
                case TARSAKH:
                    return MIRTUL;
                case MIRTUL:
                    return KYTHORN;
                case KYTHORN:
                    return FLAMERULE;
                case FLAMERULE:
                    return ELEASIS;
                case ELEASIS:
                    return ELEINT;
                case ELEINT:
                    return MARPENOTH;
                case MARPENOTH:
                    return UKTAR;
                case UKTAR:
                    return NIGHTAL;
                case NIGHTAL:
                default:
                    return HAMMER;
            }
        }
        public static Month getPrevious(Month month)
        {
            switch(month)
            {
                case HAMMER:
                    return NIGHTAL;
                case ALTURIAK:
                    return HAMMER;
                case CHES:
                    return ALTURIAK;
                case TARSAKH:
                    return CHES;
                case MIRTUL:
                    return TARSAKH;
                case KYTHORN:
                    return MIRTUL;
                case FLAMERULE:
                    return KYTHORN;
                case ELEASIS:
                    return FLAMERULE;
                case ELEINT:
                    return ELEASIS;
                case MARPENOTH:
                    return ELEINT;
                case UKTAR:
                    return MARPENOTH;
                case NIGHTAL:
                    return UKTAR;
                default:
                    return HAMMER;
            }
        }
    }
}
