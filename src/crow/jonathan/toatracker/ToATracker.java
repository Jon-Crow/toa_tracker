package crow.jonathan.toatracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crow.jonathan.toatracker.Calendar.Date;
import crow.jonathan.toatracker.Calendar.Month;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class ToATracker
{
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File SAVE_FILE = new File("game_data.json");
    
    private static GameState state;
    
    public static void main(String[] args)
    {
        /*
        try
        {
            NapkinLookAndFeel laf = new NapkinLookAndFeel();
            UIManager.setLookAndFeel(laf);
        }
        catch (Exception ex)
        {
            LOGGER.log(Level.SEVERE, "Failed to set look and feel.", ex);
        }
        */
        state = loadGameState(SAVE_FILE);
        /*
        for(int i = 0; i < 5; i++)
        {
            Player plyr = new Player(String.format("Player %d", i+1), 
                                     CreatureSize.random(), 
                                     (float)(Math.random()*10+5), 
                                     (float)(Math.random()*10+5),
                                     (int)(Math.random()*7),
                                     i == 0,
                                     Math.random() < 0.5);
            state.addPlayer(plyr);
        }
        */
        /*
        Calendar cal = state.getCalendar();
        for(int i = 0; i < 10; i++)
        {
            cal.addEvent("Event " + (i+1), "", new Date(1500, Month.KYTHORN, 0), new Date(1500, Month.KYTHORN, i));
        }
        */
        
        JFrame frame = new JFrame("ToA Tracker");
        frame.setContentPane(new JScrollPane(new CrawlPanel(state)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(STATE_SAVER);
        frame.setVisible(true);
    }
    private static GameState loadGameState(String path)
    {
        return loadGameState(new File(path));
    }
    private static GameState loadGameState(File f)
    {
        try
        {
            String json = Files.readString(f.toPath());
            return GSON.fromJson(json, GameState.class);
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Failed to load GameState from File: " + f.getAbsolutePath(), ex);
        }
        return new GameState();
    }
    public static boolean saveGameState()
    {
        return saveGameState(state, SAVE_FILE);
    }
    public static boolean saveGameState(GameState state, File f)
    {
        try
        {
            String json = GSON.toJson(state);
            Files.writeString(f.toPath(), json);
            return true;
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Failed to save GameState to File: " + f.getAbsolutePath(), ex);
        }
        return false;
    }
    
    private static final WindowAdapter STATE_SAVER = new WindowAdapter()
    {
        @Override
        public void windowClosing(WindowEvent event)
        {
            saveGameState(state, SAVE_FILE);
            Sound.close();
        }
    };
}
