import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

// Notify opponent player has closed the client
class WindowEventHandler extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
        Client.shutdownClient();
    }
}

public class Window extends JFrame implements WindowListener {
    private MainPanel panel;

    public Window() {
        setTitle("Gran Turismo 8");
        setBounds(100, 100, 850, 650);  // Set window size to 850x650 pixels
        addWindowListener(new WindowEventHandler());      // Notify opponent player has closed the client
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Container cp = getContentPane();
        cp.setLayout(null);  // suppress default layout

        panel = new MainPanel();
        panel.setBounds(0,0,850,650);  // Location within JFrame
        panel.addKeyListener(panel);
        panel.setBackground(Color.GREEN);
        panel.setFocusable(true); // Allows for response to focus related events
        cp.add(panel);
    }

    public MainPanel getPanel() { return panel; }

    public void windowClosed(WindowEvent e) {

    }

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {

    }

    public void windowIconified(WindowEvent e) {

    }

    public void windowDeiconified(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }
}
