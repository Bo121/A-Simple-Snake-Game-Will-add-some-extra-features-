import javax.swing.JFrame;


public class Msnake {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		/*
		 * 10: x-coordinate 
		 * 10: y-coordinate
		 */
		frame.setBounds(10,10,900,720); // 700 + 20 because of the bar
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); //make in the center of the screen
		frame.add(new MPanel());
		
		frame.setVisible(true);

	}

}
