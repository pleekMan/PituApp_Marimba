import processing.core.PApplet;
import processing.core.PImage;

public class Percutor {

	PApplet p5;

	int inputMode;

	float rotation;
	float rotationVel;
	float radius;

	float oscillator;
	float oscillatorIncrement;
	float tempoValue;
	int acceleration;
	float lastTempoValue;
	boolean hit;
	boolean detectHit;

	boolean triggerHeadBang;

	PImage eje;
	PImage palo;
	PImage bola;


	Percutor(PApplet _p5){

		p5 = _p5;

		inputMode = 1;

		rotation = 0f;
		rotationVel = 0.1f;
		radius = 300;

		oscillator = 0;
		oscillatorIncrement = 0.3f;

		tempoValue = 0;
		acceleration = 2;
		lastTempoValue = -1;
		hit = false;
		detectHit = false;

		triggerHeadBang = false;

		eje = p5.loadImage("Eje.png");
		palo = p5.loadImage("Palo.png");
		bola = p5.loadImage("Bola.png");


	}

	public void update(float inputX, float inputY, int leftRight, int topBottom, float frecuencia, float moveElasticity){

		switch(inputMode){

		case 1:
			updateInMode1(inputX, frecuencia, moveElasticity);
			break;
		case 2:
			updateInMode2(inputX, frecuencia, moveElasticity);
			break;
		case 3:
			updateInMode3(inputX, topBottom, frecuencia, moveElasticity);
			break;
		case 4:
			updateInMode4(inputX, leftRight, frecuencia, moveElasticity);
			break;
		case 5:
			updateInMode5(inputX, leftRight, topBottom, frecuencia, moveElasticity);
			break;
		case 6:
			updateInMode6(inputX, frecuencia, moveElasticity);
			break;
		default:
			System.out.println("Invalid Input Mode");
		}


	}

	public void render(){

		p5.pushMatrix();
		p5.translate(p5.width * 0.5f, p5.height * 0.5f);
		p5.rotate(rotation);

		p5.imageMode(p5.CENTER);

		/*
		p5.fill(0,255,0);

		p5.noStroke();

		p5.triangle(-20, 0, 0, -radius*tempoValue, 20, 0);
		p5.ellipse(0,-radius*tempoValue,80+(30*-tempoValue),80+(30*-tempoValue));

		p5.stroke(255,0,0);
		p5.line(-40,-radius*tempoValue,40,-radius*tempoValue);

		 */

		p5.image(palo, 0, 0, palo.width, palo.height * tempoValue);
		p5.image(eje,0,0);
		p5.image(bola, 0, -radius*tempoValue, bola.width + (30*-tempoValue), bola.height + (30*-tempoValue));




		p5.popMatrix();

		if(hit){
			p5.rect(0, 0, 30, 30);

		}

	}

	private void updateInMode1(float inputX, float frecuencia, float moveElasticity){
		//input = -1 OR 1
		//rotation += rotationVel * input;
		rotation += (rotationVel * (moveElasticity / 10)) * inputX;


		if(rotation > p5.TWO_PI) rotation -= p5.TWO_PI;
		if(rotation < 0) rotation += p5.TWO_PI;

		//oscillator += oscillatorIncrement;
		oscillator += oscillatorIncrement * (frecuencia / 10); // INCREMENT SETS THE TEMPO, 10 IS THE CONTROLLERS MAXIMUM
		tempoValue = ((p5.sin(oscillator) + 1) * 0.5f); // OSCILLATES BTW 0 AND 1;

		// map: SO THAT THE STICK DOESN'T DRAW OVER eje // pow: defines de acceleration curve (exponential equation) 
		tempoValue = p5.pow(tempoValue,acceleration);
		tempoValue = p5.map(tempoValue, 0, 1, 0.2f, 1); 

		//System.out.println(tempoValue);

		// HIT EVENT - BEGIN
		hit = false;
		if(lastTempoValue > tempoValue && detectHit == true){
			hit = true;
			detectHit = false;
		}
		if(lastTempoValue < tempoValue && tempoValue > 0.5){
			detectHit = true;
		}

		lastTempoValue = tempoValue;

		// HIT EVEN - END
	}

	private void updateInMode2(float inputX, float frecuencia, float moveElasticity){
		//input = -1 OR 1
		//rotation += rotationVel * input;
		if(inputX != 0){
			rotation += (rotationVel * (moveElasticity / 10)) * inputX;


			if(rotation > p5.TWO_PI) rotation -= p5.TWO_PI;
			if(rotation < 0) rotation += p5.TWO_PI;

			//oscillator += oscillatorIncrement;
			oscillator += oscillatorIncrement * (frecuencia / 10); // INCREMENT SETS THE TEMPO, 10 IS THE CONTROLLERS MAXIMUM
			tempoValue = ((p5.sin(oscillator) + 1) * 0.5f); // OSCILLATES BTW 0 AND 1;

			// map: SO THAT THE STICK DOESN'T DRAW OVER eje // pow: defines de acceleration curve (exponential equation) 
			tempoValue = p5.pow(tempoValue,acceleration);
			tempoValue = p5.map(tempoValue, 0, 1, 0.2f, 1); 
		}


		//System.out.println(tempoValue);

		// HIT EVENT - BEGIN
		hit = false;
		if(lastTempoValue > tempoValue && detectHit == true){
			hit = true;
			detectHit = false;
		}
		if(lastTempoValue < tempoValue && tempoValue > 0.5){
			detectHit = true;
		}

		lastTempoValue = tempoValue;

		// HIT EVEN - END
	}

	private void updateInMode3(float inputX, int topBottom, float frecuencia, float moveElasticity){
		//input = -1 OR 1
		//rotation += rotationVel * input;
		if(inputX != 0){
			rotation += (rotationVel * (moveElasticity / 10)) * inputX;
			if(rotation > p5.TWO_PI) rotation -= p5.TWO_PI;
			if(rotation < 0) rotation += p5.TWO_PI;
		}


		if(topBottom == 1){

			//oscillator += oscillatorIncrement;
			oscillator += oscillatorIncrement * (frecuencia / 10); // INCREMENT SETS THE TEMPO, 10 IS THE CONTROLLERS MAXIMUM
			tempoValue = ((p5.sin(oscillator) + 1) * 0.5f); // OSCILLATES BTW 0 AND 1;

			// map: SO THAT THE STICK DOESN'T DRAW OVER eje // pow: defines de acceleration curve (exponential equation) 
			tempoValue = p5.pow(tempoValue,acceleration);
			tempoValue = p5.map(tempoValue, 0, 1, 0.2f, 1); 

			//System.out.println(tempoValue);

			// HIT EVENT - BEGIN
			hit = false;
			if(lastTempoValue > tempoValue && detectHit == true){
				hit = true;
				detectHit = false;
			}
			if(lastTempoValue < tempoValue && tempoValue > 0.5){
				detectHit = true;
			}

			lastTempoValue = tempoValue;

			// HIT EVEN - END

		} else {
			// IF HEAD IS AT THE TOP
			// POSITION THE PERCUTOR IN STAND-BY
			oscillator = 0.5f;
			tempoValue = 0.5f;
		}

	}

	private void updateInMode4(float inputX, int leftRight, float frecuencia, float moveElasticity){
		//input = -1 TO 1	
		rotation += (rotationVel * (moveElasticity / 10)) * (inputX * leftRight);
		//rotation *= leftRight;

		if(rotation > p5.TWO_PI) rotation -= p5.TWO_PI;
		if(rotation < 0) rotation += p5.TWO_PI;

		//oscillator += oscillatorIncrement;
		oscillator += oscillatorIncrement * (frecuencia / 10); // INCREMENT SETS THE TEMPO, 10 IS THE CONTROLLERS MAXIMUM
		tempoValue = ((p5.sin(oscillator) + 1) * 0.5f); // OSCILLATES BTW 0 AND 1;

		// map: SO THAT THE STICK DOESN'T DRAW OVER eje // pow: defines de acceleration curve (exponential equation) 
		tempoValue = p5.pow(tempoValue,acceleration);
		tempoValue = p5.map(tempoValue, 0, 1, 0.2f, 1); 



		//System.out.println(tempoValue);

		// HIT EVENT - BEGIN
		hit = false;
		if(lastTempoValue > tempoValue && detectHit == true){
			hit = true;
			detectHit = false;
		}
		if(lastTempoValue < tempoValue && tempoValue > 0.5){
			detectHit = true;
		}

		lastTempoValue = tempoValue;

		// HIT EVEN - END
	}
	
	private void updateInMode5(float inputX, int leftRight, int topBottom, float frecuencia, float moveElasticity){
		//inputX = -1 TO 1	
		rotation += (rotationVel * (moveElasticity / 10)) * (inputX * leftRight);
		//rotation *= leftRight;

		if(rotation > p5.TWO_PI) rotation -= p5.TWO_PI;
		if(rotation < 0) rotation += p5.TWO_PI;

		if(topBottom == 1){

			//oscillator += oscillatorIncrement;
			oscillator += oscillatorIncrement * (frecuencia / 10); // INCREMENT SETS THE TEMPO, 10 IS THE CONTROLLERS MAXIMUM
			tempoValue = ((p5.sin(oscillator) + 1) * 0.5f); // OSCILLATES BTW 0 AND 1;

			// map: SO THAT THE STICK DOESN'T DRAW OVER eje // pow: defines de acceleration curve (exponential equation) 
			tempoValue = p5.pow(tempoValue,acceleration);
			tempoValue = p5.map(tempoValue, 0, 1, 0.2f, 1); 

			//System.out.println(tempoValue);

			// HIT EVENT - BEGIN
			hit = false;
			if(lastTempoValue > tempoValue && detectHit == true){
				hit = true;
				detectHit = false;
			}
			if(lastTempoValue < tempoValue && tempoValue > 0.5){
				detectHit = true;
			}

			lastTempoValue = tempoValue;

		} else {
			// IF HEAD IS AT THE TOP
			// POSITION THE PERCUTOR IN STAND-BY
			oscillator = 0.5f;
			tempoValue = 0.5f;
		}

		// HIT EVEN - END
	}
	private void updateInMode6(float inputX, float frecuencia, float moveElasticity){
		//inputX = RECIEVES ANGLE. BEHAVES AS A STEERING WHEEL
		//rotation += rotationVel * input;
		
		rotation = inputX;

		if(rotation > p5.TWO_PI) rotation -= p5.TWO_PI;
		if(rotation < 0) rotation += p5.TWO_PI;

		//oscillator += oscillatorIncrement;
		oscillator += oscillatorIncrement * (frecuencia / 10); // INCREMENT SETS THE TEMPO, 10 IS THE CONTROLLERS MAXIMUM
		tempoValue = ((p5.sin(oscillator) + 1) * 0.5f); // OSCILLATES BTW 0 AND 1;

		// map: SO THAT THE STICK DOESN'T DRAW OVER eje // pow: defines de acceleration curve (exponential equation) 
		tempoValue = p5.pow(tempoValue,acceleration);
		tempoValue = p5.map(tempoValue, 0, 1, 0.2f, 1); 

		//System.out.println(tempoValue);

		// HIT EVENT - BEGIN
		hit = false;
		if(lastTempoValue > tempoValue && detectHit == true){
			hit = true;
			detectHit = false;
		}
		if(lastTempoValue < tempoValue && tempoValue > 0.5){
			detectHit = true;
		}

		lastTempoValue = tempoValue;

		// HIT EVEN - END
	}



	public void setInputMode(int _mode){
		inputMode = _mode;

		/* MODES:
		 * 1 - CONSTANT ROTATION VELOCITY, AUTO-HIT, NEVER STOPS
		 * 2 - CONSTANT ROTATION VELOCITY, AUTO-HIT, STOPS AT NO MOVEMENT
		 * 3 - CONSTANT ROTATION VELOCITY, HEAD-BANG HIT, ROTATION STOPS AT NO MOVEMENT BUT CAN STILL HIT 
		 * 4 - INERTIAL ROTATION (DELTA), AUTO-HIT,
		 * 5 - INERTIAL ROTATION (DELTA), HEAD-BANG HIT
		 * 6 - ROTATION BY ANGLE (osc Angle msj), AUTO-HIT 
		 */
	}

	public boolean isHiting(){
		return hit;
	}
	public float getRotation(){
		return rotation;
	}
}
