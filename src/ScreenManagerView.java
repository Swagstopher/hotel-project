import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.omg.CORBA.Current;

/**
 * @author BlanchyPolangcos
 * @author ChristopherNavy
 * @author JonathanWong
 *
 *A JPanel which switches from either a Month view or Room view.
 *The Month view draws a clickable calendar and shows a list of reserved and vacant rooms
 *for the chosen date. Room view draws a clickable list of all rooms and shows reservation
 *information for a chosen room.
 **/

enum MONTHS
{
	January, February, March, April, May, June, July, August, September, October, November, December;
}

public class ScreenManagerView extends JPanel {

	private HotelView view;
	private JPanel body;
	private JPanel buttons;
	private ManagerSession ms;
	private Dimension textAreaSize;
	private Dimension bodySize;
	MONTHS[] monthArray;
	boolean viewMode; /* false if on room view, true if on month view */
	
	/**
	 * Constructor for ScreenManagerView, initializes at Room view.
	 * @param v the hotelview JFrame within which this object is drawn.
	 **/
	public ScreenManagerView(HotelView v) {
		view = v;
		ms = (ManagerSession) view.getUserSession();
		viewMode = false;
		textAreaSize = new Dimension(200, 350);
		bodySize = new Dimension(200,200);
		
		monthArray = MONTHS.values();
		
		body = new JPanel();
		buttons = new JPanel();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		
		JButton back = new JButton("Go Back");
		JButton toMonth = new JButton("See By Month");
		JButton toRoom = new JButton("See All Rooms");
		
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.changeScreen(new ScreenManager(view));
			}
		});
		
		toMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewMode = true;
				drawMonthView(ms.getCurrentDate());
			}
		});
		
		toRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewMode = false;
				drawRoomView(ms.getCurrentRoom());
			}
		});
		
		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (viewMode) {
					drawMonthView(ms.getCurrentDate());
				}
				else {
					drawRoomView(ms.getCurrentRoom());
				}
				repaint();
			}
		};
		ms.attach(l);
		
		drawRoomView(ms.getCurrentRoom());
		
		add(body);
		buttons.add(toRoom);
		buttons.add(toMonth);
		buttons.add(back);
		add(buttons);
		
	}
	
	/**
	 * draws a Calendar of days and hotel information on the selected date.
	 * @param date the date as an array in the format {MM, DD, YYYY}
	 */
	public void drawMonthView(int[] date) {
		body.removeAll();
		body.repaint();
		JPanel left = drawMonth(date);
		JPanel right = drawMonthInfo(date);
		body.add(left);
		body.add(right);
		body.repaint();
	
		invalidate();
		validate();
		repaint();
	}
	
	/**
	 * draws the room view in which there is a clickable list of rooms
	 * and info for a selected room
	 * @param room the room number selected
	 */
	public void drawRoomView(int room) {
		body.removeAll();
		body.repaint();
		JPanel left = drawRoom(room);
		JPanel right = drawRoomInfo(room);
		body.add(left);
		body.add(right);
		
		body.repaint();
		
		invalidate();
		validate();
		repaint();
	}
	
	/**
	 * shows a clickable calendar, with the selected date highlighted.
	 * calendar can also be advanced by month and by year
	 * @param date the date as an array in format {MM,DD,YYYY}
	 * @return Jpanel containing clickable calendar
	 */
	public JPanel drawMonth(int[] date) {
		Calendar current = new GregorianCalendar(date[2], date[0], date[1]); // [MM, DD, YYYY]
		
		JPanel calendar = new JPanel();
		calendar.setLayout(new BorderLayout());
		calendar.setPreferredSize(new Dimension(this.getWidth()/2, this.getHeight()/2));
		
		/* can navigate by month or year */
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 0));
		JButton backYear = new JButton("<<");
		JButton backMonth = new JButton("<");
		JButton forwardMonth = new JButton(">");
		JButton forwardYear = new JButton(">>");
		
		backYear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current.add(Calendar.YEAR, -1);		
				ms.setCurrentDate(new int[]{current.get(Calendar.MONTH), current.get(Calendar.DATE), current.get(Calendar.YEAR)});
			}
		});
		backMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current.add(Calendar.MONTH, -1);		
				ms.setCurrentDate(new int[]{current.get(Calendar.MONTH), current.get(Calendar.DATE), current.get(Calendar.YEAR)});
			}
		});
		forwardMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current.add(Calendar.MONTH, 1);	
				ms.setCurrentDate(new int[]{current.get(Calendar.MONTH), current.get(Calendar.DATE), current.get(Calendar.YEAR)});
			}
		});
		forwardYear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current.add(Calendar.YEAR, 1);		
				ms.setCurrentDate(new int[]{current.get(Calendar.MONTH), current.get(Calendar.DATE), current.get(Calendar.YEAR)});
			}
		});
		
		/* holds calendar grid and calendar header */
		JPanel calBody = new JPanel();
		calBody.setLayout(new BorderLayout());
		calBody.add(new JLabel(monthArray[current.get(Calendar.MONTH)] + " " + current.get(Calendar.YEAR),
				SwingConstants.CENTER), BorderLayout.NORTH);
		
		/* the calendar grid */
		JPanel calGrid = new JPanel();
		calGrid.setLayout(new GridLayout(0,7));

		String[] boxes = getCalGrid(date);

		JLabel[] labels = new JLabel[boxes.length];
		
		for (int i = 0; i <= boxes.length - 1; i++) { 
			JLabel day = new JLabel(boxes[i]);
//			System.out.println(day.getText());
			try {
				if (Integer.parseInt(day.getText()) == current.get(Calendar.DATE)) {
					day.setForeground(Color.RED);
					day.setFont(new Font(null, Font.BOLD, 12));
				}
				if (Integer.parseInt(day.getText()) <= 31) {
					day.addMouseListener(new MouseListener() {
						@Override
						public void mouseClicked(MouseEvent e) {
							int year = current.get(Calendar.YEAR);
							int month = current.get(Calendar.MONTH);
							int date = Integer.parseInt(day.getText());
//							System.out.println(day.getText() + " " + month + " " + year);
							ms.setCurrentDate(new int[]{month, date, year});
						}
						@Override
						public void mousePressed(MouseEvent e) {}
						@Override
						public void mouseReleased(MouseEvent e) {}
						@Override
						public void mouseEntered(MouseEvent e) {}
						@Override
						public void mouseExited(MouseEvent e) {}
					});
				}
			}
			catch (NumberFormatException n) {
				//do nothing
			}
			labels[i] = day;
		} 
		
		for (int i = 0; i<= labels.length - 1; i++)
		{
			calGrid.add(labels[i]);
		}
		
		backYear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub		
			}
		});
		
		backMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub		
			}
		});
		
		forwardMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub		
			}
		});
		
		forwardYear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub		
			}
		});
		
		calBody.add(calGrid, BorderLayout.CENTER);
		
		buttons.add(backYear);
		buttons.add(backMonth);
		buttons.add(forwardMonth);
		buttons.add(forwardYear);
		
		calendar.add(buttons, BorderLayout.NORTH);
		calendar.add(calBody, SwingConstants.CENTER);
		
		return calendar;
	}
	
	/**
	 * for the selected day of the month, this window shows the reserved rooms
	 * as well as vacant rooms
	 * @param date array, assuming array is in form of {DD, MM, YYYY}
	 * @return JPanel of selected date
	 */
	public JPanel drawMonthInfo(int[] date) {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		
		JTextArea monthInfo = new JTextArea();
		monthInfo.setEditable(false);
		
		String monthInfoText = "Rooms on " + (date[0]+1) + "/" + date[1] + "/" + date[2] + "\n";
		String vacantText = "Vacant on this day: \n";
		String reservedText = "Reserved on this day: \n";
		
		Room[] allRooms = view.getHotel().getRooms();
		
		for (Room r : allRooms) {
			if (r.isRoomAvailable((1+date[0]) + "/" + date[1] + "/" + date[2])) {
				vacantText = vacantText + (r.getRoomNumber()+1) + "\n";
			}
			else {
				String user = "";
				for (Reservation rsvn : r.getAllReservations()) {
					if (!rsvn.isResAvailable((1+date[0]) + "/" + date[1] + "/" + date[2])){ //if reservation is occupied on this date
						user = rsvn.getUserID();
					}
				}
				reservedText = reservedText + (r.getRoomNumber()+1) + " reserved by " + user + "\n";
			}
		}
		
		monthInfo.setText(monthInfoText
				+ vacantText
				+ reservedText);
		
		JScrollPane monthInfoScroll = new JScrollPane(monthInfo, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		monthInfoScroll.setPreferredSize(textAreaSize);
		jp.add(monthInfoScroll);
		
		return jp;
	}
	
	/**
	 * this shows ALL rooms in the hotel. this is also clickable. the current room is in bold.
	 * @param current selected room.
	 * @return JPanel of all rooms
	 */
	public JPanel drawRoom(int room) {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		
		JPanel roomGrid = new JPanel();
		roomGrid.setLayout(new GridLayout(5,4));
		roomGrid.setPreferredSize(bodySize);
		
		int fontSize = 15;
		
		for (int i = 1; i <= 20; i++) {
			JLabel roomLabel = new JLabel(i + "");
			roomLabel.setHorizontalAlignment(SwingConstants.CENTER);
			roomLabel.setFont(new Font(null, Font.PLAIN, fontSize));
			if ((i-1) == room) {
				roomLabel.setFont(new Font(null, Font.BOLD, 17));
			}
			roomLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					ms.setCurrentRoom(Integer.parseInt(roomLabel.getText()) - 1);
				}

				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}

			});
			roomGrid.add(roomLabel);
		}
		jp.add(new JLabel("Select a Room:"), BorderLayout.NORTH);
		jp.add(roomGrid, BorderLayout.CENTER);
		
		return jp;
	}
	
	/**
	 * for the selected room in drawRoom, this window shows the
	 * room number, price, and (if applicable) the reservation info for each reservation (user ID, start and end dates)
	 * @param current selected room.
	 * @return JPanel information of selected room
	 */
	public JPanel drawRoomInfo(int room) {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		JTextArea roomInfo = new JTextArea();
		roomInfo.setEditable(false);
		
		Room r = view.getHotel().getRoom(room);
		ArrayList<Reservation> reservations = r.getAllReservations();
		
		String roomNumber = "Room number: " + (r.getRoomNumber() + 1);
		String price = "Price: $" + r.getPrice();
		
		String reservationsString = "Reservations: \n";
		if (reservations.size() < 1) {
			reservationsString = reservationsString + "None\n";
		}
		else {
			for (Reservation rsvn : reservations) {
				reservationsString = reservationsString + 
						"User " + rsvn.getUserID() + 
						" from " + rsvn.getStartDate() + 
						" - " + rsvn.getEndDate() + "\n";
			}
		}
 			
		roomInfo.setText(
				roomNumber + "\n"
				+ price + "\n"
				+ reservationsString
				);
		
		JScrollPane roomInfoScroll = new JScrollPane(roomInfo, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		roomInfoScroll.setPreferredSize(textAreaSize);
		jp.add(roomInfoScroll);
		
		return jp;
	}
	
	/**
	 * private method to return an array of dates for the particular month
	 * including days of the week, blanks, and day numbers
	 * @param date current date as returned by model
	 * @return array of days
	 */
	private String[] getCalGrid(int[] date) {
		String[] calendarGrid = new String[49];
		
		calendarGrid[0] = "Su";
		calendarGrid[1] = "Mo";
		calendarGrid[2] = "Tu";
		calendarGrid[3] = "We";
		calendarGrid[4] = "Th";
		calendarGrid[5] = "Fr";
		calendarGrid[6] = "Sa";

		GregorianCalendar temp = new GregorianCalendar(date[2], date[0] - 1, 0);
		temp.set(Calendar.DATE, temp.getActualMinimum(Calendar.DAY_OF_MONTH));
		
//		System.out.println(temp.get(Calendar.DAY_OF_WEEK) + "\n" + temp.get(Calendar.MONTH));
		
		int blanks = temp.get(Calendar.DAY_OF_WEEK) - 1;
		
		int i = 7;
		
		for (int x = 0; x <= blanks - 1; i++, x++) {
			calendarGrid[i] = "";
		}

		int days = temp.getActualMaximum(Calendar.DATE);

		
		for (int j = 1; j <= days; i++, j++) {
				calendarGrid[i] = j + "";
		}
		
		return calendarGrid;
	}

}
