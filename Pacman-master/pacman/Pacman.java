// Pacman klassen starter et vindu med Model.java

package pacman;

import javax.swing.JFrame;

public class Pacman extends JFrame{

	public Pacman() {
		add(new Model());
	}
	
	
	public static void main(String[] args) {
		Pacman pac = new Pacman();
		pac.setVisible(true);
		pac.setTitle("Pacman");
		pac.setSize(800,800); // 380 && 420
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pac.setLocationRelativeTo(null);
		
	}

}
