import processing.core.*;
import ddf.minim.*;
import java.util.*;
import oscP5.*;
import netP5.*;
import controlP5.*;

public class Main extends PApplet {

	Minim minim;
	Percutor percutor;
	HitEffect hitEffect;

	int input;

	int tablitasCount = 14; // 2 OCTAVAS
	int tablitaHit;
	
	int percutorFrequency;
	int percutorElast;

	PFont tipo01;

	PImage backG;

	AudioPlayer[] sonidos = new AudioPlayer[14];

	OscP5 commControl;
	NetAddress commOutAddress;
	
	ControlP5 controllers;
	Slider percutorFreq;
	Slider percutorMove;


	public void setup() {

		size(800,800);
		//size(400,400);

		frameRate(30);
		smooth();

		minim = new Minim(this);
		percutor = new Percutor(this);
		hitEffect = new HitEffect(this);

		input = 0;
		tablitaHit = 0;
		
		percutorFrequency = 1;
		percutorElast = 1;

		tipo01 = loadFont("Futura-CondensedMedium-17.vlw");
		textFont(tipo01);

		backG = loadImage("Marimba.png");

		for (int i = 0; i < sonidos.length; i++) {
			sonidos[i] = minim.loadFile("audio/Note_" + i + ".mp3");
		}

		commControl = new OscP5(this, 4444);
		commOutAddress = new NetAddress("127.0.0.1", 5555);
		
		controllers = new ControlP5(this);
		
		/*
		selectRange = selectors.addRange("CycleRange", 0, totalCyclesData.length - 1, queryWin.winX+50, queryWin.winY+100, 300, 20); // LE PONGO EL - 1 PARA USAR EL VALOR (INT) DESPUES
		selectRange.setCaptionLabel("");

		selectRange.setNumberOfTickMarks(totalCyclesData.length);
		selectRange.snapToTickMarks(true);
		selectRange.setSliderMode(Slider.FLEXIBLE);
		selectRange.showTickMarks(true);
		*/
		
		percutorFreq = controllers.addSlider("percutorFrequency", 1, 10, width - 150, 20, 20, 100);
		percutorFreq.setCaptionLabel("FRECUENCIA");
		percutorFreq.setNumberOfTickMarks(10);
		percutorFreq.snapToTickMarks(true);
		percutorFreq.showTickMarks(true);
		
		percutorMove = controllers.addSlider("percutorElast", 1, 10, width - 50, 20, 20, 100);
		percutorMove.setCaptionLabel("VELOCIDAD");
		percutorMove.setNumberOfTickMarks(10);
		percutorMove.snapToTickMarks(true);
		percutorMove.showTickMarks(true);
	}

	public void draw(){
		//background(0);
		imageMode(CORNER);
		image(backG,0,0);

		/*
		if(mouseX > width*0.6){
			input = 1;
		} else if(mouseX < width*0.4){
			input = -1;
		} else{
			input = 0;
		}
		*/
		

		drawHitMarker();
		hitEffect.render();


		percutor.update(input, percutorFrequency, percutorElast);
		percutor.render();

		if(percutor.isHiting()){
			tablitaHit = (int)(map(percutor.getRotation(), 0, TWO_PI, 0, tablitasCount));
			playSound(tablitaHit);
			startHitEffect();
		}
		text("Tablita: " + tablitaHit, 20,20);

		text("Entrada: " + input, 500,500);
		
		//drawSectors();

	}

	public static void main(String args[]){
		PApplet.main(new String[] { Main.class.getName() });
		//PApplet.main(new String[] { "--present", Main.class.getName() }); // PRESENT MODE
	}

	public void mouseMoved(){

	}

	public void playSound(int tablita){
		sonidos[tablita].rewind();
		sonidos[tablita].play();
	}

	public void startHitEffect(){
		float sectorStep = TWO_PI / tablitasCount;
		float constrainedRotation = sectorStep * (int)(map(percutor.getRotation(), 0, TWO_PI, 0, tablitasCount));
		constrainedRotation += sectorStep*0.5f;

		float xCenter = width*0.5f + (-300 * (cos(constrainedRotation + HALF_PI)));
		float yCenter = height*0.5f + (-300 * (sin(constrainedRotation + HALF_PI)));

		hitEffect.start(xCenter,yCenter);

	}

	private void drawHitMarker(){
		noFill();

		float sectorStep = TWO_PI / tablitasCount;
		float constrainedRotation = sectorStep * (int)(map(percutor.getRotation(), 0, TWO_PI, 0, tablitasCount));
		constrainedRotation += sectorStep*0.5f;

		pushMatrix();
		translate(width*0.5f,height*0.5f);
		rotate(constrainedRotation);

		stroke(255,50);
		ellipse(0,-300,80,80);
		stroke(255,100);
		ellipse(0,-300,60,60);
		stroke(255,200);
		ellipse(0,-300,40,40);
		stroke(255);
		ellipse(0,-300,20,20);
		popMatrix();
	}

	private void drawSectors(){
		fill(0,0,255);
		pushMatrix();
		translate(width*0.5f,height*0.5f);
		for (int i = 0; i < tablitasCount; i++) {
			rotate((TWO_PI / tablitasCount));
			line(0,-height*0.25f,0,-height);
			if(tablitaHit == i){
				pushMatrix();
				rotate(-(TWO_PI / (tablitasCount * 2)));
				ellipse(0,-300,100,100);
				popMatrix();
			}
		}
		popMatrix();
	}

	public void stop(){
		minim.stop();
		super.stop();
	}

	void oscEvent(OscMessage theOscMessage) {
		/* print the address pattern and the typetag of the received OscMessage */

		System.out.println("### received an osc message.");
		System.out.println(" addrpattern: "+theOscMessage.addrPattern());
		System.out.println(" typetag: "+theOscMessage.typetag());

		if(theOscMessage.checkAddrPattern("/direccion/X")){
			// DIRECCION ES INT = -1 or 0 or 1
			//System.out.println(theOscMessage.get(0).intValue());
			input = theOscMessage.get(0).intValue();
			System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).intValue());
		}
		
		if(theOscMessage.checkAddrPattern("/direccion/Y")){
			// DIRECCION ES INT = -1 or 0 or 1
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).intValue()
		}
		
		if(theOscMessage.checkAddrPattern("/angulo")){
			// 0 a TWO_PI (6.28....)
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());

		}
		
		if(theOscMessage.checkAddrPattern("/eje/X")){
			// -1 a 1
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}
		
		if(theOscMessage.checkAddrPattern("/eje/Y")){
			// -1 a 1
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}
		
		/*
		if(theOscMessage.checkAddrPattern("/delta/X")){
			// DELTA NORMALIZADO (0 a 1)
			System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}
		
		if(theOscMessage.checkAddrPattern("/delta/Y")){
			// DELTA NORMALIZADO (0 a 1)
			System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}
		*/
	}
}

