package crow.jonathan.toatracker;

public interface PlayerEvent
{
    public void execute(GameState state, Player plyr);
    
    public static final PlayerEvent EAT = new PlayerEvent()
    {
        private static final String EAT_SOUND = "eat";
        
        @Override
        public void execute(GameState state, Player plyr)
        {
            Sound.play(EAT_SOUND);
            plyr.eat(state.getWeather().getTemperature() == Weather.Temperature.EXTREME_HEAT);
        }
    };
    public static final PlayerEvent DRINK = new PlayerEvent()
    {
        private static final String DRINK_SOUND = "drink";
        
        @Override
        public void execute(GameState state, Player plyr)
        {
            Sound.play(DRINK_SOUND);
            plyr.drink(state.getWeather().getTemperature() == Weather.Temperature.EXTREME_HEAT);
        }
    };
    public static final PlayerEvent COLLECT = new PlayerEvent()
    {
        private static final String SPLASH_SOUND = "splash";
        
        @Override
        public void execute(GameState state, Player plyr)
        {
            Sound.play(SPLASH_SOUND);
            plyr.collectWater(state.getWeather());
        }
    };
}
