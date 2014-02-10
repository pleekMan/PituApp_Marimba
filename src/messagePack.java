
public class messagePack {
	
	int direccionX;
	int direccionY;
	float deltaX;
	float deltaY;
	float x;
	float y;
	float angulo;
	
	messagePack(){
		
		direccionX = 0;
		direccionY = 0;
		deltaX = 0;
		deltaY = 0;
		x = 0;
		y = 0;
		angulo = 0;
		
	}
	
	public void setDireccionX(int value){
		direccionX = value;
	}
	public void setDireccionY(int value){
		direccionY = value;
	}
	
	public void setDeltaX(float value){
		deltaX = value;
	}
	public void setDeltaY(float value){
		deltaY = value;
	}
	
	public void setX(float value){
		x = value;
	}
	public void setY(float value){
		y = value;
	}
	
	public void setAngulo(float value){
		angulo = value;
	}
	
	/////////
	
	public int getDireccionX(){
		return direccionX;
	}
	public int getDireccionY(){
		return direccionY;
	}
	
	public float getDeltaX(){
		return deltaX;
	}
	public float getDeltaY(){
		return deltaY;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	
	public float getAngulo(){
		return angulo;
	}

}
