package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    public static final double CONCERT_A = 440.0;
    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final int NUM_STRINGS = 37;
    private static final String[] NOTE_NAMES = {
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };
    public static void main(String[] args) {
        /* create an array of 37 guitar strings */
        GuitarString[] guitarStrings = new GuitarString[NUM_STRINGS];
        for (int i = 0; i < NUM_STRINGS; i++) {
            var frequency = getFrequency(i);
            guitarStrings[i] = new GuitarString(frequency);
        }

        String displayText = "Press a key to play!";
        String noteInfo = "";
        
        // Enable double buffering to reduce flicker
        StdDraw.enableDoubleBuffering();
        
        int frameCounter = 0;
        final int FRAMES_PER_UPDATE = 1000; // Update display every 1000 frames (~44Hz)

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                
                // Only pluck if the key is in our keyboard string
                if (index >= 0) {
                    guitarStrings[index].pluck();
                    String noteName = getNoteName(index);
                    double frequency = getFrequency(index);
                    displayText = String.format("Playing: '%c'", key);
                    noteInfo = String.format("%s (%.2f Hz)", noteName, frequency);
                } else {
                    displayText = String.format("Invalid key: '%c'", key);
                    noteInfo = "";
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (int i = 0; i < NUM_STRINGS; i++) {
                sample += guitarStrings[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < NUM_STRINGS; i++) {
                guitarStrings[i].tic();
            }

            // Only update display every FRAMES_PER_UPDATE frames
            if (frameCounter % FRAMES_PER_UPDATE == 0) {
                StdDraw.clear(StdDraw.WHITE);
                
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
                StdDraw.text(0.5, 0.8, "Guitar Hero");
                
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
                StdDraw.text(0.5, 0.6, displayText);
                
                if (!noteInfo.isEmpty()) {
                    StdDraw.setPenColor(StdDraw.DARK_GRAY);
                    StdDraw.text(0.5, 0.5, noteInfo);
                }
                
                StdDraw.setPenColor(StdDraw.GRAY);
                StdDraw.setFont(new java.awt.Font("Monospace", java.awt.Font.PLAIN, 12));
                StdDraw.text(0.5, 0.3, "Keyboard:");
                StdDraw.text(0.5, 0.25, KEYBOARD);
                
                StdDraw.show();
            }
            
            frameCounter++;
        }
    }

    /**
     * The ith character of the string keyboard corresponds to a frequency of 440⋅2(i−24)/12
     * , so that the character 'q' is 110Hz, 'i' is 220Hz, 'v' is 440Hz, and ' ' is 880Hz.
     */
    private static double getFrequency(int index) {
        return CONCERT_A * Math.pow(2, (index - 24.0) / 12);
    }

    private static String getNoteName(int index) {
        // A4 is 440Hz, index 24
        // Calculate the number of semitones from C0
        int semitonesFromC0 = index - 24 + 9; // A is the 9th semitone after C
        int octave = semitonesFromC0 / 12;
        int noteIndex = semitonesFromC0 % 12;
        if (noteIndex < 0) {
            noteIndex += 12;
            octave--;
        }
        return NOTE_NAMES[noteIndex] + octave;
    }
}

