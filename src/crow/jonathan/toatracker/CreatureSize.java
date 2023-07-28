package crow.jonathan.toatracker;

public enum CreatureSize
{
    TINY("Tiny", 0.25f, 0.25f),
    SMALL("Small", 1.0f, 1.0f),
    MEDIUM("Medium", 1.0f, 2.0f),
    LARGE("Large", 4.0f, 4.0f),
    HUGE("Huge", 16.0f, 16.0f),
    GARGANTUAN("Gargantuan", 64.0f, 64.0f);
    
    public final String stringVal;
    public final float foodPerDay,
                       waterPerDay;
    
    private CreatureSize(String stringVal, float foodPerDay, float waterPerDay)
    {
        this.stringVal = stringVal;
        this.foodPerDay = foodPerDay;
        this.waterPerDay = waterPerDay;
    }
    
    public static CreatureSize random()
    {
        int idx = (int)(Math.random()*values().length);
        return values()[idx];
    }
}
