package fcatools.conexpng;

import com.alee.laf.WebLookAndFeel;
import com.google.common.collect.Sets;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.gui.MainFrame;
import fcatools.conexpng.io.BurmeisterReader;
import fcatools.conexpng.io.CEXReader;
import fcatools.conexpng.model.FormalContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Properties;

/**
 *
 * The code for (re)storing the window location is from: <href
 * a="http://stackoverflow.com/a/7778332/283607">What is the best practice for
 * setting JFrame locations in Java?</a>
 *
 */
public class Main {

    public static final String settingsDirName = ".conexp-ng";
    public static final String optionsFileName = new File(getSettingsDirectory(), "options.prop").getPath();
    private static Rectangle r;
    private static Exception exception = null;
    private static boolean fileOpened=false;

    public static void main(String... args) {
        // Disabled until there is a fix for #98
        // WebLookAndFeel.install();

        // Disable border around focused cells as it does not fit into the
        // context editor concept
        UIManager.put("Table.focusCellHighlightBorder", new EmptyBorder(0, 0, 0, 0));
        // Disable changing foreground color of cells as it does not fit into
        // the context editor concept
        UIManager.put("Table.focusCellForeground", Color.black);

        final Conf state = new Conf();

        boolean firstStart = false;
        File optionsFile = new File(optionsFileName);
        if (optionsFile.exists()) {
            try {
                restoreOptions(state);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            showExample(state);
            state.filePath = System.getProperty("user.home");
            firstStart = true;
        }

        // Create main window and take care of correctly saving and restoring
        // the last window location
        final MainFrame f = new MainFrame(state);
        f.setMinimumSize(new Dimension(1000, 660));
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    storeOptions(f, state);
                    f.new CloseAction().actionPerformed(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
        if (exception != null) {
            Util.handleIOExceptions(f, exception, state.filePath);
        }
        if (firstStart) {
            f.setSize(1000, 660);
            f.setLocationByPlatform(true);
        } else
            f.setBounds(r);
        f.setVisible(true);

        // Force various GUI components to update
        if (fileOpened)
            state.loadedFile();
        else
            state.contextChanged();
    }

    private static void showExample(Conf state) {
        state.filePath = "untitled.cex";
        state.context = new FormalContext();
        state.context.addAttribute("female");
        state.context.addAttribute("juvenile");
        state.context.addAttribute("adult");
        state.context.addAttribute("male");
        try {
            state.context.addObject(new FullObject<>("girl", Sets.newHashSet("female", "juvenile")));
            state.context.addObject(new FullObject<>("woman", Sets.newHashSet("female", "adult")));
            state.context.addObject(new FullObject<>("boy", Sets.newHashSet("male", "juvenile")));
            state.context.addObject(new FullObject<>("man", Sets.newHashSet("male", "adult")));
        } catch (IllegalObjectException e1) {
            e1.printStackTrace();
        }
    }

    // Store location & size of UI & dir that was last opened from
    private static void storeOptions(Frame f, Conf state) throws Exception {
        File file = new File(optionsFileName);
        Properties p = new Properties();
        // restore the frame from 'full screen' first!
        f.setExtendedState(Frame.NORMAL);
        Rectangle r = f.getBounds();
        int x = (int) r.getX();
        int y = (int) r.getY();
        int w = (int) r.getWidth();
        int h = (int) r.getHeight();

        p.setProperty("x", "" + x);
        p.setProperty("y", "" + y);
        p.setProperty("w", "" + w);
        p.setProperty("h", "" + h);
        p.setProperty("lastOpened", state.filePath);
        for (int i = 0; i < 5; i++) {
            if (state.lastOpened.size() > i)
                p.setProperty("lastOpened" + i, state.lastOpened.get(i));
            else
                p.setProperty("lastOpened" + i, "");
        }
        BufferedWriter br = new BufferedWriter(new FileWriter(file));
        p.store(br, "Properties of the user frame");
    }

    // Restore location & size of UI & dir that was last opened from
    private static void restoreOptions(Conf state) throws IOException {
        File file = new File(optionsFileName);
        Properties p = new Properties();
        BufferedReader br = new BufferedReader(new FileReader(file));
        p.load(br);

        int x = Integer.parseInt(p.getProperty("x"));
        int y = Integer.parseInt(p.getProperty("y"));
        int w = Integer.parseInt(p.getProperty("w"));
        int h = Integer.parseInt(p.getProperty("h"));
        String lastOpened = p.getProperty("lastOpened");
        if (p.containsKey("lastOpened0")) {
            for (int i = 0; i < 5; i++) {
                if (p.getProperty("lastOpened" + i).isEmpty())
                    break;
                state.lastOpened.add(p.getProperty("lastOpened" + i));
            }
        }
        if (new File(lastOpened).isFile()) {
            state.filePath = lastOpened;
            try {
                if (lastOpened.endsWith("cex"))

                    new CEXReader(state);

                else

                    new BurmeisterReader(state);
                fileOpened = true;
            } catch (Exception e) {
                exception = e;
            }
        } else {
            showExample(state);
            state.filePath = System.getProperty("user.home");
        }
        r = new Rectangle(x, y, w, h);
    }

    // http://stackoverflow.com/a/193987/283607
    private static File getSettingsDirectory() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            throw new IllegalStateException("user.home==null");
        }
        File home = new File(userHome);
        File settingsDirectory = new File(home, settingsDirName);
        if (!settingsDirectory.exists()) {
            if (!settingsDirectory.mkdir()) {
                throw new IllegalStateException(settingsDirectory.toString());
            }
        }
        return settingsDirectory;
    }

}
