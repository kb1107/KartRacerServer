import javax.swing.*;

public class Kart {
    private String kartType;
    private ImageIcon imageIcon;
    private ImageIcon[] kartImages; // holds the 16 rotations of the kart image
    private double locationX; // x coordinate
    private double locationY; // y coordinate
    private double targetX; // target x coordinate
    private double targetY; // target y coordinate
    private int speed; // current speed of kart: 0 - 100;
    private int direction;
    private int lapsLeft; // holds the number of remaining laps for the kart
    private boolean halfLapFlag; // true when a kart has crossed half-lap marker - used to stop cheating and laps being counted incorrectly
    private boolean crashFlag; // true when kart has collided with the other kart

    public Kart(String colour) {
        direction = 4; // ensure kart is facing the right way at start
        speed = 0; // ensure kart is stationary at the start
        lapsLeft = 3; // Races are 3 laps long
        halfLapFlag = false;
        crashFlag = false;
        kartImages = new ImageIcon[16];


        if (colour.equals("red")) {
            this.kartType = colour;
            for (int i=0; i < kartImages.length; i++){
                kartImages[i] = new ImageIcon(getClass().getResource("/kartPics/redKart" + i + ".png"));
            }
            imageIcon = kartImages[direction];
            // set Kart on start line on creation
            targetX = 375;
            locationX = 375;
            targetY = 500;
            locationY = 500;

        }
        // Player 2
        else {
            this.kartType = colour;
            for (int i=0; i < kartImages.length; i++) {
                kartImages[i] = new ImageIcon(getClass().getResource("/kartPics/blueKart" + i + ".png"));
            }
            imageIcon = kartImages[direction];
            // set kart on start line on creation
            targetX = 375;
            locationX = 375;
            targetY = 550;
            locationY = 550;
        }
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public double getLocationX() {
        return locationX;
    }

    public void setLocationX(double locationX) { this.locationX = locationX; }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) { this.locationY = locationY; }

    public int getDirection() {
        return direction;
    }

    public int getSpeed() { return speed; }

    public int getLapsLeft() { return lapsLeft; }

    public void setLapsLeft(int lapsLeft) { this.lapsLeft = lapsLeft; }

    public boolean getCrashFlag() { return crashFlag; }

    public void setCrashFlag(boolean crash) { crashFlag = crash; }

    public void updateLocation() {
        // L - R
        if (direction == 4) {
            targetX = locationX + 2 * (speed / 10);
        }
        else if (direction == 3) {
            targetX = locationX + 2 * (speed / 10);
            targetY = locationY - 1 * (speed / 10);
        }
        else if (direction == 2) {
            targetX = locationX + 2 * (speed / 10);
            targetY = locationY - 2 * (speed / 10);
        }
        else if (direction == 1) {
            targetX = locationX + 1 * (speed / 10);
            targetY = locationY - 2 * (speed / 10);
        }
        else if (direction == 0) {
            targetY = locationY - 2 * (speed / 10);
        }
        else if (direction == 15) {
            targetX = locationX - 1 * (speed / 10);
            targetY = locationY - 2 * (speed / 10);
        }
        else if (direction == 14) {
            targetX = locationX - 2 * (speed / 10);
            targetY = locationY - 2 * (speed / 10);
        }
        else if (direction == 13) {
            targetX = locationX - 2 * (speed / 10);
            targetY = locationY - 1 * (speed / 10);
        }
        else if (direction == 12) {
            targetX = locationX - 2 * (speed / 10);
        }
        else if (direction == 11) {
            targetX = locationX - 2 * (speed / 10);
            targetY = locationY + 1 * (speed / 10);
        }
        else if (direction == 10) {
            targetX = locationX - 2 * (speed / 10);
            targetY = locationY + 2 * (speed / 10);
        }
        else if (direction == 9) {
            targetX = locationX - 1 * (speed / 10);
            targetY = locationY + 2 * (speed / 10);
        }
        else if (direction == 8) {
            targetY = locationY + 2 * (speed / 10);
        }
        else if (direction == 7) {
            targetX = locationX + 1 * (speed / 10);
            targetY = locationY + 2 * (speed / 10);
        }
        else if (direction == 6) {
            targetX = locationX + 2 * (speed / 10);
            targetY = locationY + 2 * (speed / 10);
        }
        else if (direction == 5) {
            targetX = locationX + 2 * (speed / 10);
            targetY = locationY + 1 * (speed / 10);
        }

        // Stop Kart leaving right or left side of track
        if (targetX > 750 || targetX < 50) {
            targetX = locationX;
            speed = 0; // Scrub off all speed as kart has crashed
        }
        // Stop Kart leaving top or bottom of track
        if (targetY < 100 || targetY > 550) {
            targetY = locationY;
            speed = 0;
        }
        // stop kart entering middle from right
        if (locationX >= 700 && targetX < 700 && locationY < 500 && locationY > 200) {
            targetX = locationX;
            speed = 0;
        }
        // stop kart entering middle from top
        if (locationY <= 150 && targetY > 150 && locationX > 100 && locationX < 700) {
            targetY = locationY;
            speed = 0;
        }
        // stop kart entering middle from left
        if (locationX <= 100 && targetX > 100 && locationY < 500 && locationY > 200) {
            targetX = locationX;
            speed = 0;
        }
        // stop kart entering middle from bottom
        if (locationY >= 500 && targetY < 500 && locationX > 100 && locationX < 700) {
            targetY = locationY;
            speed = 0;
        }

        // Move kart
        if (locationX != targetX) {
            locationX = targetX;
        }
        if (locationY != targetY) {
            locationY = targetY;
        }
    }

    public void updateSpeed(int dspeed) {
        speed += dspeed;

        if (speed > 100) {
            speed = 100;
        }
        else if (speed < 0){
            speed = 0;
        }
    }

    public void updateDirection(int newDirection) {
        direction = newDirection;

        if (speed > 10) {
            speed -= 10; // if moving quicker than minimum speed, scrub off some speed
        }

        imageIcon = kartImages[direction];
    }

    public void checkLapCounter() {
        // check finish line
        if (halfLapFlag && (locationX < 435) && (locationX + 50 > 425) && (locationY < 610) && (locationY + 50 > 500)) {
            halfLapFlag = false;
            lapsLeft--;
        }
        // check half lap marker
        else if ((locationX < 435) && (locationX + 50 > 425) && (locationY < 210) && (locationY + 50 > 100)) {
            halfLapFlag = true; // this prevents laps being counted when karts cross the finish line the wrong way
        }
    }
}
