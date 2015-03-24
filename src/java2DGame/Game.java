package java2DGame;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java2dgame.graphics.Screen;
import java2dgame.graphics.SpriteSheet;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	
	private static final long serialVersionUID = 7393561960087538163L; //Not sure what this is doing?
	
	public static final int WIDTH = 160, HEIGHT = WIDTH/12 *9;
	public static final int SCALE = 3;
	public static final String NAME = "Game";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_BGR);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	//private SpriteSheet spriteSheet = new SpriteSheet("/spriteSheet.png");
	private Screen screen;
	
	public Game(){
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	
	}
	
	public void init(){
		SpriteSheet newSheet = new SpriteSheet("/spriteSheet.png");
		screen = new Screen(WIDTH, HEIGHT, newSheet);
	}
	
	public synchronized void start(){
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop(){
		running = false;
	}
	
	public void run(){
		long lastTime = System.nanoTime(); //Uses this regement the update time so that certain computers can't run faster
		double nsPerTick = 1000000000D/60D; //How many nanoseconds in the current update
		
		int frames = 0;
		int ticks = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0; //How many unprocessed nano seconds have gone by so far
		
		init();
		
		while (running){
			long now = System.nanoTime();
			delta += (now - lastTime)/nsPerTick;
			lastTime = now;
			boolean shouldRender = true; //Not doing anything at the moment - would change to false to limit frames rendered
			
			while(delta >= 1){
				ticks++;
				tick();
				delta -=1;
				shouldRender = true;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			
			if (shouldRender){
				frames++;
				render();
			}
			
			
			if(System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer += 1000;
				System.out.println("Frames: " + frames + ", Ticks: " + ticks);
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick(){
		tickCount++;
		
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = i % tickCount;
		}
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		screen.render(pixels, 0, WIDTH);
		
		Graphics g = bs.getDrawGraphics();
		
		
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		/*
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		*/
		g.dispose();
		bs.show();
		
	}
	
	public static void main(String[] args){
		new Game().start();
	}

}