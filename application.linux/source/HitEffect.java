import processing.core.PApplet;

public class HitEffect {

	PApplet p5;

	float maxRadius;
	//float radius;
	float radiusIncrement;
	int circleCount;
	float x, y;


	HitEffect(PApplet _p5){

		p5 = _p5;

		maxRadius = 200;
		//radius = 1;
		radiusIncrement = 1f;
		circleCount = 10;
		x = 0;
		y = 0;

	}

	public void render(){

		if(!isFinished()){

			p5.noFill();
			
			radiusIncrement += 5;	

			for (int i = circleCount; i > 0; i--) {
				float radius = i * 10 + radiusIncrement;
				p5.stroke((int)p5.random(255), (int)p5.random(255), (int)p5.random(255), p5.map(radiusIncrement, 0, maxRadius, 255, 0));
				p5.ellipse(x,y,radius,radius);
			}
		}

	}

	public void start(float _x, float _y){
		x = _x;
		y = _y;
		radiusIncrement = 2;

	}

	private boolean isFinished(){
		if(radiusIncrement > maxRadius){
			return true;
		} else {
			return false;
		}
	}

}
