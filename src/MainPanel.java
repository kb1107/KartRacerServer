import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainPanel extends JPanel implements ActionListener, KeyListener {
    private JLabel ownKartLabel;
    private JLabel foreignKartLabel;

    private JLabel crashLabel;
    private JLabel winnerLabel;
    private JLabel loserLabel;
    private JLabel searchingLabel;
    private JLabel opponentExitLabel;

    private JButton playAgainButton;
    private JButton exitButton;

    private Timer timer;

    public MainPanel() {
        setLayout(null); // suppress panel layout features

        ownKartLabel = new JLabel(Client.getOwnKart().getImageIcon());
        ownKartLabel.setBounds((int) Client.getOwnKart().getLocationX(), (int) Client.getOwnKart().getLocationY(), 50, 50); // start just behind start line - image is 50x50px
        add(ownKartLabel);

        foreignKartLabel = new JLabel();
        add(foreignKartLabel);

        crashLabel = new JLabel("CRASH!!!  GAME OVER");
        crashLabel.setBounds(200, 350, 400, 50);

        winnerLabel = new JLabel("You won, congratulations!");
        winnerLabel.setBounds(200, 350, 400, 50);

        loserLabel = new JLabel("You lost, better luck next time!");
        loserLabel.setBounds(200, 350, 400, 50);

        searchingLabel = new JLabel("Searching for opponent...");
        searchingLabel.setBounds(350, 300, 400, 50);

        opponentExitLabel = new JLabel("Lost connection to opponent...");
        opponentExitLabel.setBounds(350, 200, 400, 50);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(200, 400, 150, 50);
        playAgainButton.addActionListener(this);

        exitButton = new JButton("Exit Game");
        exitButton.setBounds(500, 400, 150, 50);
        exitButton.addActionListener(this);
        add(exitButton);

        timer = new Timer(25, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw track
        Color c2 = Color.black;
        g.setColor( c2 );
        g.fillRect( 50, 100, 750, 500 ); // outer edge
        g.drawRect( 150, 200, 550, 300 ); // inner edge
        Color c1 = Color.green;
        g.setColor( c1 );
        g.fillRect( 150, 200, 550, 300 ); // grass

        Color c3 = Color.yellow;
        g.setColor( c3 );
        g.drawRect( 100, 150, 650, 400 ); // mid-lane marker
        Color c4 = Color.white;
        g.setColor( c4 );
        g.fillRect(425, 501, 10, 99); // start/finish line

        // Draw karts
        ownKartLabel.setIcon(Client.getOwnKart().getImageIcon());
        ownKartLabel.setBounds((int) Client.getOwnKart().getLocationX(), (int) Client.getOwnKart().getLocationY(), 50, 50);

        if (Client.getForeignKart() != null) {
            foreignKartLabel.setIcon(Client.getForeignKart().getImageIcon());
            foreignKartLabel.setBounds((int) Client.getForeignKart().getLocationX(), (int) Client.getForeignKart().getLocationY(), 50, 50);

            // Draw opponent game information
            g.setColor(c2); // black
            g.drawString("Opponent laps: " + Client.getForeignKart().getLapsLeft(), 600, 255);
            remove(searchingLabel);
        }
        else {
            add(searchingLabel);
        }

        // Draw player game information
        g.setColor(c2); // black
        g.drawString("You are " + Client.getKartType(), 155, 215);
        g.drawString("Speed: " + Client.getOwnKart().getSpeed(), 155, 235);
        g.drawString("Laps: " + Client.getOwnKart().getLapsLeft(), 155, 255);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            Client.getOwnKart().updateLocation();
            Client.getOwnKart().checkLapCounter();

            if (Client.getForeignKart() != null) {

                // Check for crashes
                Client.detectKartCollision();

                if (Client.getOwnKart().getLapsLeft() == 0) {
                    add(winnerLabel);
                    add(playAgainButton);
                    timer.stop();
                }
                if (Client.getForeignKart().getLapsLeft() == 0) {
                    add(loserLabel);
                    add(playAgainButton);
                    timer.stop();
                }

                if (Client.getOwnKart().getCrashFlag() || Client.getForeignKart().getCrashFlag()) {
                    add(crashLabel);
                    add(playAgainButton);
                    timer.stop();
                }
            }

            Client.sendOwnKart();
        }

        if (e.getSource() == playAgainButton) {

            timer.stop();
            Client.resetKarts();

            // Dispose of Window
            ((JFrame) MainPanel.this.getTopLevelAncestor()).dispose();
        }

        if (e.getSource() == exitButton) {

            timer.stop();

            Client.shutdownClient();

            // Dispose of window
            ((JFrame) MainPanel.this.getTopLevelAncestor()).dispose();
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {

        // Disable game controls until all players are connected
        if (Client.getForeignKart() == null) {
            return;
        }

        // Get the pressed key
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT) {
            int direction = Client.getOwnKart().getDirection();

            // L -> R
            if (direction == 4) {
                Client.getOwnKart().updateSpeed(10); // accelerate
            }
            else if (direction == 12) {
                Client.getOwnKart().updateSpeed(-10); // brake
            }
            else if (direction == 15) {
                Client.getOwnKart().updateDirection(0);
            }
            else if (direction > 12 || direction < 4) {
                Client.getOwnKart().updateDirection(direction + 1);
            }
            else if (direction < 12 && direction > 4) {
                Client.getOwnKart().updateDirection(direction - 1);
            }
        }

        if (key == KeyEvent.VK_UP) {
            int direction = Client.getOwnKart().getDirection();

            if (direction == 0) {
                Client.getOwnKart().updateSpeed(10); // accelerate
            }
            else if (direction == 8) {
                Client.getOwnKart().updateSpeed(-10); // brake
            }
            else if (direction == 15) {
                Client.getOwnKart().updateDirection(0);
            }
            else if (direction > 8 && direction < 15) {
                Client.getOwnKart().updateDirection(direction + 1);
            }
            else if (direction < 8 && direction > 0) {
                Client.getOwnKart().updateDirection(direction - 1);
            }
        }

        if (key == KeyEvent.VK_LEFT) {
            int direction = Client.getOwnKart().getDirection();

            if (direction == 12) {
                Client.getOwnKart().updateSpeed(10); // accelerate
            }
            else if (direction == 4) {
                Client.getOwnKart().updateSpeed(-10); // brake
            }
            else if (direction == 0) {
                Client.getOwnKart().updateDirection(15);
            }
            else if (direction > 12 || direction < 4) {
                Client.getOwnKart().updateDirection(direction - 1);
            }
            else if (direction < 12 && direction > 4) {
                Client.getOwnKart().updateDirection(direction + 1);
            }
        }

        if (key == KeyEvent.VK_DOWN) {
            int direction = Client.getOwnKart().getDirection();

            if (direction == 8) {
                Client.getOwnKart().updateSpeed(10); // accelerate
            }
            else if (direction == 0) {
                Client.getOwnKart().updateSpeed(-10); // brake
            }
            else if (direction > 0 && direction < 8) {
                Client.getOwnKart().updateDirection(direction + 1);
            }
            else if (direction > 8) {
                Client.getOwnKart().updateDirection(direction - 1);
            }
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void notifyOpponentExit() {
        searchingLabel.setVisible(false);
        add(opponentExitLabel);
    }
}
