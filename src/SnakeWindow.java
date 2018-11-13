import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Choice;


public class SnakeWindow {

	private JFrame frame;
	private JButton btnNewGame;
	private JPanel panel;

	private Choice speedChoice;
	private int speed;
	private JTextField sizeTextField;
	private static int fieldSize = 5;
	private final static int gap = 1;
	
	private Map<Integer,Map<Integer,JPanel>> fields;	
	
	private Timer timer;

	private ArrayList<Point> snake;
	private Point food;
	private Direction direction;
	private Boolean gameover = false;

	public static void main(String[] args) {
		new SnakeWindow();
	}

	/**
	 * Create the application.
	 */
	public SnakeWindow() {
		initialize();
		frame.setVisible(true);
	}	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusable(true);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(fieldSize, fieldSize, gap, gap));
		panel.setPreferredSize(new Dimension(50*fieldSize,50*fieldSize));
		frame.pack();

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		menuBar.setFocusable(false);

		btnNewGame = new JButton("New Game");
		menuBar.add(btnNewGame);
		btnNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();	}
		});

		JTextField speedNameTextField = new JTextField("Speed: ");
		speedNameTextField.setEditable(false);
		speedNameTextField.setFocusable(false);
		menuBar.add(speedNameTextField);
		
		speedChoice = new Choice();
		speedChoice.add("1");
		speedChoice.add("2");
		speedChoice.add("3");
		menuBar.add(speedChoice);
		

		JTextField sizeNameTextField = new JTextField("Size: ");
		sizeNameTextField.setEditable(false);
		sizeNameTextField.setFocusable(false);
		menuBar.add(sizeNameTextField);

		sizeTextField = new JTextField("5");
		menuBar.add(sizeTextField);


		KeyListener arrowListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP){
					direction = Direction.UP;
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					direction = Direction.DOWN;
				} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					direction = Direction.RIGHT;
				} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					direction = Direction.LEFT;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		};

		frame.addKeyListener(arrowListener);
		panel.addKeyListener(arrowListener);
		menuBar.addKeyListener(arrowListener);
		btnNewGame.addKeyListener(arrowListener);
		sizeTextField.addKeyListener(arrowListener);

	}

	private void newGame(){
		try {
			fieldSize = Integer.parseInt(sizeTextField.getText());
			if(fieldSize < 3 || fieldSize > 9) { throw new NumberFormatException("Field size must be at least 3"); }
			speed = Integer.parseInt(speedChoice.getSelectedItem());
			timer = new Timer();
			initializeField();
			initializeSnake();
			putRandomFood();
			panel.revalidate();
			direction = Direction.RIGHT;
			gameover = false;
			
			// Start thread to automatically move
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					moveSnake();
				}
			}, 0, 1000/speed);
			
		} catch (NumberFormatException e){
			JOptionPane.showMessageDialog(frame, "Size is not a valid number.\nSize must be between 3 and 9 including.", "Error!", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	Runnable helloRunnable = new Runnable() {
	    public void run() {
	        System.out.println("Hello world");
	        moveSnake();
	    }
	};

	private void initializeField(){
		panel.removeAll();
		panel.setLayout(new GridLayout(fieldSize, fieldSize, gap, gap));
		frame.repaint();
		fields = new HashMap<Integer,Map<Integer,JPanel>>();
		for(int i = 1; i<=fieldSize; i++){
			HashMap<Integer,JPanel> tempMap = new HashMap<Integer,JPanel>();
			for(int j = 1; j<=fieldSize; j++){
				JPanel tempPanel = new JPanel();
				tempPanel.setName(i + " " + j);
				tempPanel.setBackground(Color.white);
				panel.add(tempPanel);
				tempMap.put(j, tempPanel);
			}
			fields.put(i, tempMap);
		}
	}

	private void initializeSnake(){
		snake = new ArrayList<Point>();
		int row = (int) Math.ceil(fieldSize/2.0);
		HashMap<Integer,JPanel> rowMap = (HashMap<Integer, JPanel>) fields.get(row);
		int col = (int) Math.ceil(fieldSize/2.0);
		if (col < 3) { col = 3; }
		for(int i=col-2; i<=col; i++){
			JPanel tempPanel = rowMap.get(i);
			tempPanel.setBackground(Color.black);
			snake.add(new Point(row, i));
		}
	}

	private void putRandomFood(){
		Random rand = new Random();
		while(true){
			int row = rand.nextInt(fieldSize) + 1;
			int col = rand.nextInt(fieldSize) + 1;
			JPanel tempPanel = fields.get(row).get(col);
			if(tempPanel.getBackground() == Color.WHITE){
				tempPanel.setBackground(Color.GREEN);
				frame.revalidate();
				food = new Point(row, col);
				return;
			}
		}		
	}

	private void moveSnake(){
		if(gameover){return;}
		Point head = snake.get(snake.size()-1);
		int headX = (int) head.getX();
		int headY = (int) head.getY();
		switch(direction){
		case RIGHT: headY = headY + 1; break;
		case LEFT: headY = headY - 1; break;
		case UP: headX = headX - 1; break;
		case DOWN: headX = headX + 1; break;
		}
		Point newHead = new Point(headX,headY);
		if( headX == 0 || headY == 0 || headX > fieldSize || headY > fieldSize || 
				fields.get(headX).get(headY).getBackground() == Color.BLACK){
			// Crash
			gameover = true;
			timer.cancel();
			timer.purge();
			JOptionPane.showMessageDialog(frame, "Game Over! ", "Game Over!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(newHead.equals(food)){
			// New Food & Don't move tail
			putRandomFood();
		} else {
			// Remove old tail
			Point tail = snake.remove(0);
			fields.get((int) tail.getX()).get((int) tail.getY()).setBackground(Color.WHITE);
		}
		// Add new head
		snake.add(newHead);
		fields.get(headX).get(headY).setBackground(Color.BLACK);
		// Refresh
		frame.revalidate();
	}


}
