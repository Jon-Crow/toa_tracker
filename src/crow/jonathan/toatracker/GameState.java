package crow.jonathan.toatracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameState
{
    private static final Logger LOGGER = Logger.getGlobal();
    
    private Weather weather;
    private Travel travel;
    private Calendar cal;
    private ArrayList<Player> plyrs;
    
    public GameState()
    {
        plyrs = new ArrayList<>();
        weather = Weather.roll();
        travel = new Travel();
        cal = new Calendar();
    }
    public Weather getWeather()
    {
        return weather;
    }
    public void setWeather(Weather weather)
    {
        this.weather = weather;
    }
    public Travel getTravel()
    {
        return travel;
    }
    public Calendar getCalendar()
    {
        return cal;
    }
    public int getPlayerCount()
    {
        return plyrs.size();
    }
    public Player getPlayer(int idx)
    {
        if(idx < 0 || idx >= getPlayerCount())
            return null;
        return plyrs.get(idx);
    }
    public Player addPlayer()
    {
        Player plyr = new Player();
        addPlayer(plyr);
        return plyr;
    }
    public void addPlayer(Player plyr)
    {
        plyrs.add(plyr);
    }
    public void removePlayer(Player plyr)
    {
        plyrs.remove(plyr);
    }
    public void removePlayers(int[] idx)
    {
        Arrays.sort(idx);
        for(int i = idx.length-1; i >= 0; i--)
        {
            Player plyr = plyrs.remove(idx[i]);
            if(plyr != null)
                LOGGER.log(Level.INFO, "Removed player \"" + plyr.getName() + "\" from the game.");
        }
    }
    public void setNavigator(int idx)
    {
        for(int i = 0; i < getPlayerCount(); i++)
            plyrs.get(i).setNavigator(i == idx);
    }
    public Player getNavigator()
    {
        for(Player plyr : plyrs)
        {
            if(plyr.isNavigator())
                return plyr;
        }
        return null;
    }
    public int getNavigatorWisdom()
    {
        Player nav = getNavigator();
        if(nav == null)
            return 0;
        return nav.getWisdomModifier();
    }
    public float getPartyWater()
    {
        float water = 0.0f;
        for(Player plyr : plyrs)
            water += plyr.getWater();
        return water;
    }
    public float getPartyFood()
    {
        float food = 0.0f;
        for(Player plyr : plyrs)
            food += plyr.getFood();
        return food;
    }
    public void triggerPlayerEvent(PlayerEvent event, int[] plyrs)
    {
        for(int plyrIdx : plyrs)
        {
            Player plyr = getPlayer(plyrIdx);
            if(plyr != null)
                event.execute(this, plyr);
        }
    }
    public void triggerPlayerEvent(PlayerEvent event)
    {
        for(Player plyr : plyrs)
            event.execute(this, plyr);
    }
}
