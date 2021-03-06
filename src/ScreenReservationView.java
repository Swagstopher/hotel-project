import javax.swing.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author BlanchyPolangcos
 * @author ChristopherNavy
 * @author JonathanWong
 *
 * Formats the reservations of a particular guest to display
 * on the GUI.
 **/


public class ScreenReservationView extends JPanel{

    /**
     * creates a Panel that shows Reservation information.
     * @param rn Room Number of the Room
     * @param dt Dates that the room is booked.
     */
    ScreenReservationView(String rn, String dt){
        setLayout(new GridLayout(0,1));

        setPreferredSize(new Dimension(400,200));

        add(new JLabel("Room Number: " + rn));
        add(new JLabel("Dates: " + dt));

    }


}

