package crow.jonathan.toatracker;

import crow.jonathan.toatracker.Calendar.Date;
import crow.jonathan.toatracker.Calendar.Event;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;

public class CalendarPanel extends JPanel
{
    private static final int ROWS = 3;
    private static final int COLS = 10;
    
    private Calendar cal;
    private Calendar.Month month;
    private int year;
    private DayArea days[];
    
    public CalendarPanel(Calendar cal)
    {
        this.cal = cal;
        Date date = cal.getDate();
        month = date.getMonth();
        year = date.getYear();
        days = new DayArea[ROWS*COLS];
        for(int i = 0; i < days.length; i++)
            days[i] = new DayArea(new Rectangle(), "");
        
        setFocusable(true);
        addMouseMotionListener(mouse);
    }
    public Calendar getCalendar()
    {
        return cal;
    }
    public boolean inCurrentMonth()
    {
        Date date = cal.getDate();
        return month == date.getMonth() && year == date.getYear();
    }
    public int getYear()
    {
        return year;
    }
    public void setYear(int year)
    {
        this.year = year;
    }
    public Calendar.Month getMonth()
    {
        return month;
    }
    public void setMonth(Calendar.Month month)
    {
        this.month = month;
    }
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(getForeground());
        
        int dayW = getWidth()/COLS,
            dayH = getHeight()/ROWS;
        
        g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
        for(int i = 1; i < COLS; i++)
            g2d.drawLine(i*dayW, 0, i*dayW, getHeight());
        for(int i = 1; i < ROWS; i++)
            g2d.drawLine(0, i*dayH, getWidth(), i*dayH);
        
        g2d.setFont(getFont());
        for(int y = 0; y < ROWS; y++)
        {
            for(int x = 0; x < COLS; x++)
            {
                int dayIdx = y*COLS+x;
                days[dayIdx].rect.setBounds(x*dayW, y*dayH, dayW, dayH);
                
                Date date = new Date(getYear(), getMonth(), y*COLS+x);
                g2d.drawString(date.getDayOfMonth()+1+"", x*dayW+1, (y+1)*dayH-1);
                
                ArrayList<Event> events = cal.getEvents(date);
                String eventStr = "";
                days[dayIdx].tooltip = "";
                for(Event event : events)
                {
                    days[dayIdx].tooltip += event.getName() + "<br>";
                    eventStr += "|";
                }
                
                g2d.drawString(eventStr, x*dayW+1, y*dayH-1 + g2d.getFontMetrics().getHeight());
            }
        }
        
        if(inCurrentMonth())
        {
            int day = cal.getDate().getDayOfMonth(),
                dayY = day/COLS,
                dayX = day%COLS;
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(dayX*dayW, dayY*dayH, dayW, dayH);
        }
    }
    
    private static final class DayArea
    {
        private Rectangle rect;
        private String tooltip;
        
        private DayArea(Rectangle rect, String tooptip)
        {
            this.rect = rect;
            this.tooltip = tooltip;
        }
    }
    
    private final MouseAdapter mouse = new MouseAdapter()
    {
        @Override
        public void mouseMoved(MouseEvent event)
        {
            for(DayArea day : days)
            {
                if(day.rect.contains(event.getX(), event.getY()))
                {
                    setToolTipText("<html>" + day.tooltip + "</html>");
                    return;
                }
            }
        }
    };
}
