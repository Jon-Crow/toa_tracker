package crow.jonathan.toatracker;

import java.io.InputStream;
import java.util.HashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

public class Sound
{
    private static HashMap<String, Clip> clips = new HashMap<>();
    
    private static String nameToPath(String name)
    {
        return "res/sound/" + name + ".wav";
    }
    private static Clip getClip(String name)
    {
        if(clips.containsKey(name))
            return clips.get(name);
        
        System.out.println("Creating clip for " + name);
        
        try
        {
            InputStream stream = Sound.class.getClassLoader().getResourceAsStream(nameToPath(name));
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(stream);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioStream.getFormat());
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clips.put(name, clip);
            return clip;
        }
        catch(Exception err)
        {
            err.printStackTrace();
        }
        return null;
    }
    public static void play(String name)
    {
        Clip clip = getClip(name);
        if(clip == null)
            return;
        
        if(clip.isRunning())
            return;
        
        clip.setFramePosition(0);
        clip.start();
    }
    public static void close()
    {
        for(Clip clip : clips.values())
        {
            clip.close();
        }
    }
    
    private Sound()
    {}
}
