import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class ScreenMakeRes extends JPanel {

	private HotelView view;
	
	public ScreenMakeRes(HotelView v) {
		view = v;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel instructions = new JLabel("Note: Reservations may not exceed 60 days");
		
		JLabel checkIn = new JLabel("Check in (MM/DD/YYYY):");
		JTextField checkInDate = new JTextField();
		
		JLabel checkOut = new JLabel("Check out (MM/DD/YYYY):");
		JTextField checkOutDate = new JTextField();
		
		JButton seeRooms = new JButton("See Rooms");
		
		seeRooms.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				view.changeScreen(new ScreenVacancies(view));

			}

		});
		
		add(instructions);
		add(checkIn);
		add(checkInDate);
		add(checkOut);
		add(checkOutDate);
		add(seeRooms);
	}

}
