package crow.jonathan.toatracker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Player
{
    private static final Logger LOGGER = Logger.getGlobal();
    
    private String name;
    private CreatureSize size;
    private float food,
                  water;
    private int wisMod;
    private boolean nav,
                    collector;

    public Player()
    {
        this("", CreatureSize.TINY, 0.0f, 0.0f, 0, false, false);
    }
    public Player(String name, CreatureSize size, float food, float water, int wisMod, boolean nav, boolean collector)
    {
        this.name = name;
        this.size = size;
        this.food = food;
        this.water = water;
        this.wisMod = wisMod;
        this.nav = nav;
        this.collector = collector;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public CreatureSize getSize()
    {
        return size;
    }
    public void setSize(CreatureSize size)
    {
        this.size = size;
    }
    public float getFood()
    {
        return food;
    }
    public void setFood(float food)
    {
        this.food = Math.max(food, 0.0f);
    }
    public float getWater()
    {
        return water;
    }
    public void setWater(float water)
    {
        this.water = Math.max(water, 0.0f);
    }
    public int getWisdomModifier()
    {
        return wisMod;
    }
    public void setWisdomModifier(int wisMod)
    {
        this.wisMod = wisMod;
    }
    public boolean isNavigator()
    {
        return nav;
    }
    public void setNavigator(boolean nav)
    {
        this.nav = nav;
    }
    public boolean hasCollector()
    {
        return collector;
    }
    public void setCollector(boolean collector)
    {
        this.collector = collector;
    }
    public float getRequiredFood(boolean extreme)
    {
        float req = getSize().foodPerDay;
        if(extreme)
            return req*2.0f;
        return req;
    }
    public boolean hasEnoughFood(boolean extreme)
    {
        return getFood() >= getRequiredFood(extreme);
    }
    public void eat(boolean extreme)
    {
        float amnt = getRequiredFood(extreme);
        setFood(getFood()-amnt);
        LOGGER.log(Level.INFO, String.format("Player %s consumed %.2f pounds of food. New food value: %.2f", getName(), amnt, getFood()));
    }
    public float getRequiredWater(boolean extreme)
    {
        float req = getSize().waterPerDay;
        if(extreme)
            return req*2.0f;
        return req;
    }
    public boolean hasEnoughWater(boolean extreme)
    {
        return getWater() >= getRequiredWater(extreme);
    }
    public void drink(boolean extreme)
    {
        float amnt = getRequiredWater(extreme);
        setWater(getWater()-amnt);
        LOGGER.log(Level.INFO, String.format("Player \"%s\" consumed %.2f gallons of water. New water value: %.2f", getName(), amnt, getWater()));
    }
    public void collectWater(Weather weather)
    {
        collectWater(weather.getRain());
    }
    public void collectWater(Weather.Rain rain)
    {
        collectWater(rain.collect);
    }
    public void collectWater(float water)
    {
        if(hasCollector())
        {
            setWater(getWater()+water);
            LOGGER.log(Level.INFO, String.format("Player \"%s\" collected %.1f gallons of water. New water value: %.2f", getName(), water, getWater()));
        }
        else
            LOGGER.log(Level.INFO, "Player \"" + getName() + "\" does not have a collector, and did not collect any water today.");
    }
}
