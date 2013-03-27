package tme4;


public abstract class Agent extends Item{

	World _world;
	
	static int redId   = 0;
	static int greenId = 1;
	static int blueId  = 2;
	
	int 	_orient;
	int 	_etat;
	
	int 	_redValue;
	int 	_greenValue;
	int 	_blueValue;
	
	public Agent( int __x, int __y, World __w )
	{
		x = __x;
		y = __y;
		_world = __w;
                world = __w;
		
		_redValue = 255;
		_greenValue = 0;
		_blueValue = 0;
		
		_orient = 0;
	}
        public void move(double direction, double dist)
        {
            x += Math.cos(direction)*dist;
            y += Math.sin(direction)*dist;
        }
        
	
}
