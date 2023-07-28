package crow.jonathan.toatracker;

/*
On the map of Chult, each hex measures 10 miles across. Characters moving at a 
normal pace can travel 1 hex per day on foot through coastal, jungle, mountain, 
swamp, or wasteland terrain. They can travel 2 hexes per day if they’re 
traveling by canoe on a river or lake. The rate of travel up or down river is 
the same; the rivers are so sluggish that current is almost imperceptible. 
Without canoes, the normal rate of travel along a river is the same as through 
the surrounding terrain. Canoes move 1 hex per day through swamp.

If characters move at a fast pace they have a 50% chance of advancing 1 
additional hex that day.

If characters move at a slow pace they a have a 50% chance of advancing 1 fewer 
hex that day (in other words, 1 hex by canoe or none by foot).

At the start of each new travel day, the DM makes a Wisdom (Survival) check on 
behalf of the navigator. The result of the check determines whether or not the 
party becomes lost over the course of the day. The DC of the check is based on 
the day’s most common terrain: DC 10 for coasts and lakes, or DC 15 for jungles, 
mountains, rivers, swamps, and wastelands. The app will need to be able to track 
the wisdom modifier of the navigator and see if the check passes depending on 
the terrain

If the check fails, the party becomes lost. Each hex on the map is surrounded by 
six other hexes; whenever a lost party moves 1 hex, roll a d6 to randomly 
determine which neighboring hex the party enters (N, NW, NE, S, SW, SE)

Rolls 3d20's every day to see if there is an encounter. Encounters occur on a 16 
or higher. Should be able to determine when the encounter happens between the 3 
rolls (eg: whether it is morning, midday, or night. 1st d20 is for morning, 2nd 
is for midday. 3rd is for night)
*/
public class Travel
{
    private Terrain terrain;
    private Pace pace;
    private boolean canoe,
                    lost;
    
    public Travel()
    {
        this(Terrain.JUNGLE, Pace.NORMAL);
    }
    public Travel(Terrain terrain, Pace pace)
    {
        this.terrain = terrain;
        this.pace = pace;
    }
    public Terrain getTerrain()
    {
        return terrain;
    }
    public void setTerrain(Terrain terrain)
    {
        this.terrain = terrain;
    }
    public Pace getPace()
    {
        return pace;
    }
    public void setPace(Pace pace)
    {
        this.pace = pace;
    }
    public boolean hasCanoe()
    {
        return canoe;
    }
    public void setHasCanoe(boolean canoe)
    {
        this.canoe = canoe;
    }
    public boolean isLost()
    {
        return lost;
    }
    public void setLost(boolean lost)
    {
        this.lost = lost;
    }
    
    public static enum Terrain
    {
        COAST("Coast", 10, false),
        LAKE("Lake", 10, true),
        JUNGLE("Jungle", 15, false),
        MOUNTAIN("Mountains", 15, false),
        RIVER("River", 15, true),
        SWAMP("Swamp", 15, false),
        WASTELAND("Wastelands", 15, false);

        public final String stringVal;
        public final int navDc;
        public final boolean fasterByBoat;

        private Terrain(String stringVal, int navDc, boolean fasterByBoat)
        {
            this.stringVal = stringVal;
            this.navDc = navDc;
            this.fasterByBoat = fasterByBoat;
        }
    }
    public static enum Pace
    {
        SLOW("Slow"),
        NORMAL("Normal"),
        FAST("Fast");
        
        public final String stringVal;
        
        private Pace(String stringVal)
        {
            this.stringVal = stringVal;
        }
    }
    public static enum Direction
    {
        NORTH("North"),
        NORTH_EAST("North East"),
        SOUTH_EAST("South East"),
        SOUTH("South"),
        SOUTH_WEST("South West"),
        NORTH_WEST("North West");
        
        public final String stringVal;
        
        private Direction(String stringVal)
        {
            this.stringVal = stringVal;
        }
        public static Direction getRandom()
        {
            int idx = (int)(Math.random()*values().length);
            return values()[idx];
        }
    }
}
