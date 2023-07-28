package crow.jonathan.toatracker;

public class Weather
{
    private int tempRoll,
                windRoll,
                rainRoll;
    private Temperature temp;
    private Wind wind;
    private Rain rain;
    
    private Weather(int tempRoll, int windRoll, int rainRoll)
    {
        this.tempRoll = tempRoll;
        this.windRoll = windRoll;
        this.rainRoll = rainRoll;
        temp = Temperature.getFromRoll(tempRoll);
        wind = Wind.getFromRoll(windRoll);
        rain = Rain.getFromRoll(rainRoll);
    }
    public int getTemperatureRoll()
    {
        return tempRoll;
    }
    public int getWindRoll()
    {
        return windRoll;
    }
    public int getRainRoll()
    {
        return rainRoll;
    }
    public Temperature getTemperature()
    {
        return temp;
    }
    public Wind getWind()
    {
        return wind;
    }
    public Rain getRain()
    {
        return rain;
    }
    
    public static Weather roll()
    {
        return new Weather(Dice.d20(), Dice.d20(), Dice.d20());
    }
    
    public static enum Temperature
    {
        EXTREME_HEAT("Extreme Heat"),
        NORMAL("Normal for the Season"),
        TEMPERATE("Temperate");
        
        public final String stringVal;
        
        private Temperature(String stringVal)
        {
            this.stringVal = stringVal;
        }

        public static Temperature getFromRoll(int roll)
        {
            if(roll <= 5)
                return EXTREME_HEAT;
            if(roll <= 15)
                return NORMAL;
            return TEMPERATE;
        }
    }
    public static enum Wind
    {
        NONE("None"),
        LIGHT("Light"),
        STRONG("Strong");
        
        public final String stringVal;
        
        private Wind(String stringVal)
        {
            this.stringVal = stringVal;
        }
        
        public static Wind getFromRoll(int roll)
        {
            if(roll <= 12)
                return NONE;
            if(roll <= 17)
                return LIGHT;
            return STRONG;
        }
    }
    public static enum Rain
    {
        NONE("None", 0.0f, 0.0f),
        LIGHT("Light", 0.016f, 0.5f),
        MEDIUM("Medium", 0.03f, 1.0f),
        HEAVY("Heavy", 0.047f, 1.5f),
        STORM("Tropical Storm", 0.094f, 3.0f);
        
        public final String stringVal;
        public final float rainfall,
                           collect;
        
        private Rain(String stringVal, float rainfall, float collect)
        {
            this.stringVal = stringVal;
            this.rainfall = rainfall;
            this.collect = collect;
        }
        
        public static Rain getFromRoll(int roll)
        {
            if(roll <= 5)
                return NONE;
            if(roll <= 10)
                return LIGHT;
            if(roll <= 14)
                return MEDIUM;
            if(roll <= 17)
                return HEAVY;
            return STORM;
        }
    }
}
