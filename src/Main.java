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
	messagePack paqueteOSC;

	int interactionMode;

	float inputX;
	float inputY;

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
	RadioButton modeSelector;


	public void setup() {

		size(800,800);
		//size(400,400);

		frameRate(30);
		smooth();

		minim = new Minim(this);
		percutor = new Percutor(this);
		hitEffect = new HitEffect(this);
		paqueteOSC = new messagePack();

		interactionMode = 1;

		inputX = 0f;
		inputY = 0f;
		tablitaHit = 0;

		percutorFrequency = 1;
		percutorElast = 1;

		tipo01 = loadFont("Futura-CondensedMedium-17.vlw");
		textFont(tipo01);

		backG = loadImage("Marimba.png");

		// SOUND
		for (int i = 0; i < sonidos.length; i++) {
			sonidos[i] = minim.loadFile("audio/Note_" + i + ".mp3");
		}

		// OSC COMMUNICATION
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

		modeSelector = controllers.addRadioButton("SELECCION MODO")
				.setPosition(20,20)
				.setSize(40,20)
				.setColorForeground(color(120))
				.setColorActive(color(255))
				.setColorLabel(color(255))
				.setItemsPerRow(1)
				.setSpacingColumn(50)
				.addItem("A-1 : MOVIMIENTO CONTINUO, AUTO-PERCUTIR, NO SE DETIENE",0)
				.addItem("A-2 : MOVIMIENTO CONTINUO, AUTO-PERCUTIR, SE DETIENE",1)
				.addItem("A-3 : MOVIMIENTO CONTINUO, PERCUTIR CON LA CABEZA, SE DETIENE",2)
				.addItem("B-1 : MOVIMIENTO CON ACELERACION, AUTO-PERCUTIR",3)
				.addItem("B-2 : MOVIMIENTO CON ACELERACION, PERCUTIR CON LA CABEZA",4)
				.addItem("B-3 : ROTACION GUIADA (angulo de la cabeza), AUTO-PERCUTIR",5)
				;
	}

	public void draw(){
		//background(0);
		imageMode(CORNER);
		image(backG,0,0);

		// DEFINE INPUT INTERPRETATION
		/* MODES:
		 * 1 - CONSTANT ROTATION VELOCITY, AUTO-HIT, NEVER STOPS
		 * 2 - CONSTANT ROTATION VELOCITY, AUTO-HIT, STOPS AT NO MOVEMENT
		 * 3 - CONSTANT ROTATION VELOCITY, HEAD-BANG HIT, ROTATION STOPS AT NO MOVEMENT BUT CAN STILL HIT 
		 * 4 - INERTIAL ROTATION (DELTA), AUTO-HIT,
		 * 5 - INERTIAL ROTATION (DELTA), HEAD-BANG HIT
		 * 6 - ROTATION BY ANGLE (osc Angle msj), AUTO-HIT
		 * 
		 * INPUT VALUE IS DEFINED HERE, AND PASSED ONTO percutor.update()
		 * WHEN CHANGING interactionMode, percutor ALSO CHANGES inputMode
		 * THEREFORE IT KNOWS HOW TO INTERPRET THE INPUTS BY ITSELF (AS INT OR FLOAT, IN WHAT RANGE)
		 */

		int leftOrRight = leftOrRight();
		int topOrBottom = topOrBottom();

		// MOUSE POINTER TESTING (TO DISABLE, UNCOMMENT ALL paqueteOSC LINES)
		if(interactionMode == 1 || interactionMode == 2 || interactionMode == 3){
			inputX = pointerArea((float)mouseX / width);
			inputY = pointerArea((float)mouseY / height);
			// THE DIFFERENCE BTW THESE FUNCTION AND leftOrRight
			// IS THAT THESE CONSIDER A MIDDLE NO-MOVEMENT AREA (AS ZERO. 0.4 < AREA < 0.6)

			// OSC INPUT
			//inputX = (float)paqueteOSC.getDireccionX();
			//inputY = (float)paqueteOSC.getDireccionX();

		}		
		if(interactionMode == 4 || interactionMode == 5){
			inputX = pointerDeltaX();
			inputY = pointerDeltaY();

			// OSC INPUT
			//inputX = (float)paqueteOSC.getDeltaX();
			//inputY = (float)paqueteOSC.getDeltaY();
		}

		if (interactionMode == 6) {
			inputX = pointerAngle();

			//inputX = paqueteOSC.getAngulo(); 
		}


		// END DEFINE INPUT INTERPRETATION


		drawHitMarker();
		hitEffect.render();


		percutor.update(inputX, inputY, leftOrRight, topOrBottom, percutorFrequency, percutorElast);
		percutor.render();

		if(percutor.isHiting()){
			tablitaHit = (int)(map(percutor.getRotation(), 0, TWO_PI, 0, tablitasCount));
			playSound(tablitaHit);
			startHitEffect();
		}
		text("Tablita: " + tablitaHit, 20,20);

		text("Entrada Pointer: " + ((float)mouseX / width), 500,480);
		text("Entrada: " + inputX, 500,500);
		//System.out.println((float)mouseX / 800);

		//drawSectors();

	}

	public static void main(String args[]){
		PApplet.main(new String[] { Main.class.getName() });
		//PApplet.main(new String[] { "--present", Main.class.getName() }); // PRESENT MODE
	}

	private int leftOrRight(){
		if(mouseX > width*0.5){
			return 1;
		} else {
			return -1;
		}
	}

	private int topOrBottom(){
		if(mouseY > height*0.5){
			return 1;
		} else {
			return -1;
		}
	}

	private int pointerArea(float pos){
		int value = 0;

		if(pos > 0.6f){
			value = 1;
		} else if(pos < 0.4f){
			value = -1;
		} else{
			value = 0;
		}

		return value;
	}

	/*
	private int pointerYArea(){
		int value = 0;

		if(mouseY > height*0.6){
			value = 1;
		} else if(mouseY < height*0.4){
			value = -1;
		} else{
			value = 0;
		}

		return value;
	}
	 */

	private float pointerDeltaX(){
		float deltaX = 0;
		deltaX = abs(mouseX - (width * 0.5f)) / (width * 0.5f);
		//deltaX = norm(deltaX, 0, width * 0.5f);
		return deltaX;
	}

	private float pointerDeltaY(){
		float deltaY = 0;
		deltaY = abs(mouseY - (width * 0.5f)) / (width * 0.5f);
		//deltaY = norm(deltaX, 0, width * 0.5f);
		return deltaY;
	}

	private float pointerAngle(){

		float angle = 0;

		pushMatrix();
		translate(width/2, height/2);
		angle = atan2(mouseY-height/2, mouseX-width/2);
		angle += HALF_PI;
		popMatrix();

		return angle;
	}

	public void keyPressed() {


		if (key == ENTER || key == RETURN) {

		}

		if (key == '1') {
			interactionMode = 1;
		}
		if (key == '2') {
			interactionMode = 2;
		}
		if (key == '3') {
			interactionMode = 3;
		}
		if (key == '4') {
			interactionMode = 4;
		}
		if (key == '5') {
			interactionMode = 5;
		}
		if (key == '6') {
			interactionMode = 6;
		}
		percutor.setInputMode(interactionMode);
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
	
	public void controlEvent(ControlEvent theEvent) {
		  if(theEvent.isFrom(modeSelector)) {
			  for(int i=0;i<theEvent.getGroup().getArrayValue().length;i++) {
			      if((int)(theEvent.getGroup().getArrayValue()[i]) == 1);
			      interactionMode = i;
			    }
		  }
	}


	public void oscEvent(OscMessage theOscMessage) {
		//print the address pattern and the typetag of the received OscMessage

		/*
		System.out.println("### received an osc message.");
		System.out.println(" addrpattern: "+theOscMessage.addrPattern());
		System.out.println(" typetag: "+theOscMessage.typetag());
		 */

		if(theOscMessage.checkAddrPattern("/direccion/X")){
			// DIRECCION ES INT = -1 or 0 or 1
			//System.out.println(theOscMessage.get(0).intValue());
			paqueteOSC.setDireccionX(theOscMessage.get(0).intValue());
			System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).intValue());
		}

		if(theOscMessage.checkAddrPattern("/direccion/Y")){
			// DIRECCION ES INT = -1 or 0 or 1
			paqueteOSC.setDireccionY(theOscMessage.get(0).intValue());
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).intValue()
		}

		if(theOscMessage.checkAddrPattern("/angulo")){
			// 0 a TWO_PI (6.28....)
			paqueteOSC.setAngulo(theOscMessage.get(0).floatValue());
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());

		}

		if(theOscMessage.checkAddrPattern("/eje/X")){
			// -1 a 1
			paqueteOSC.setX(theOscMessage.get(0).floatValue());
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}

		if(theOscMessage.checkAddrPattern("/eje/Y")){
			// -1 a 1
			paqueteOSC.setY(theOscMessage.get(0).floatValue());
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}


		if(theOscMessage.checkAddrPattern("/delta/X")){
			// DELTA NORMALIZADO (0 a 1)
			paqueteOSC.setDeltaX(theOscMessage.get(0).floatValue());
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}

		if(theOscMessage.checkAddrPattern("/delta/Y")){
			// DELTA NORMALIZADO (0 a 1)
			paqueteOSC.setDeltaY(theOscMessage.get(0).floatValue());
			//System.out.println(theOscMessage.addrPattern() + ": " + theOscMessage.get(0).floatValue());
		}

	}

}

