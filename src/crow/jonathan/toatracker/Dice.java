package crow.jonathan.toatracker;

public class Dice
{
    public static int roll(int sides, int mod)
    {
        return (int)(Math.random()*sides)+1+mod;
    }
    public static int d20()
    {
        return d20(0);
    }
    public static int d20(int mod)
    {
        return roll(20, mod);
    }
    
    private Dice()
    {}
}
