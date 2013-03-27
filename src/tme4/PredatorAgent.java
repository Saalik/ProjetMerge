package tme4;



public class PredatorAgent extends Agent {

	boolean _predator;
        int it = 0;
        static int M = 250; 
        static int L = 200;
        int lastmeal = 0;
        int Radius = 30;
        int orientNS;
        int orientEO;
        int tired= 0;
        static int nrjreprod = 25;
        boolean deadguy;
        int stillstinks;
        static int deadmax = 40;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public PredatorAgent(boolean _predator, int orientNS, int orientEO, boolean deadguy, int __x, int __y, World __w) {
        super(__x, __y, __w);
        this._predator = _predator;
        this.orientNS = orientNS;
        this.orientEO = orientEO;
        this.deadguy = deadguy;
    }
	
	public PredatorAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		_predator = true;
	}
	
	public void step( )
	{
		// met a jour l'agent
		
		// A COMPLETER
                lastmeal++;
                tired++;
                
                if(deadguy == true && stillstinks > (deadmax-1)){
                     _world.remove(this);
                    return;
                }
                
                if(deadguy == true){
                    stillstinks++;
                    return;
                }
                
                if(tired == 100){
                    tired = 0;
                    return;
                }
                
                
                if(lastmeal == L){
                    deadguy = true;
                    return;
                }
		/*int cellColor[] = _world.getCellState(x, y);
		
		cellColor[redId] = 255;
		cellColor[greenId] = 240;
		cellColor[blueId] = 225;*/

                
                boolean found = false; // Permet de savoir si le déplacement doit se faire de façon aléatoire
                
                double distMin=300;
                double dist;
                int nbagents =_world.getAgentSize();
                for (int i = 0; i < nbagents; i++)
                {
                    found = true;
                    dist = Math.sqrt(Math.pow(x+1-_world.get(i).x,2)+Math.pow(y+1-_world.get(i).y,2));
                    if(dist<Radius && dist<distMin && _world.get(i) instanceof PreyAgent)
                    {
                        distMin = dist;
                            if(x>_world.get(i).x)
                                orientEO = 0;
                            else if (x<_world.get(i).x)
                                orientEO = 1;
                            else
                                orientEO = -1; 
                            if(y<_world.get(i).y)
                                orientNS = 0;
                            else if (y>_world.get(i).y)
                                orientNS = 1;
                            else
                                orientNS = -1;
                    }
                    
                    else
                    {
                        found = false;
                    }
                        /*else if(_x-1 == _world.agents.get(i)._x && _y == _world.agents.get(i)._y && _world.agents.get(i) instanceof PreyAgent)
                     {
                        _orient= 3;
                        break;
                     }
                    else if(_x == _world.agents.get(i)._x && _y-1 == _world.agents.get(i)._y && _world.agents.get(i) instanceof PreyAgent)
                    {
                        _orient= 0;
                        break;
                    }
                    
                    else if(_x == _world.agents.get(i)._x && _y+1 == _world.agents.get(i)._y && _world.agents.get(i) instanceof PreyAgent)
                    {
                        _orient= 2;
                        break;
                    }*/
                }
                
                
               if (!found)
               {
                   if (Math.random() > 0.5) // au hasard
                   {
                            orientNS = (orientNS + (int) (Math.random()*25)) % 2;
                            //System.out.println(orientNS); Fonction ultilisé pour tester l'algorithme aléatoire                  
                   }
                   if (Math.random() > 0.5)
                {
                            orientEO = (orientEO + (int) (Math.random()*25)) % 2;
                            //System.out.println(orientNS); Fonction ultilisé pour tester l'algorithme aléatoire
                }    
                }

		// met a jour: la position de l'agent (depend de l'orientation)
		if(orientNS == 1)	
         		y = ( y - 1 + _world.getHeight() ) % _world.getHeight();
                else if (orientNS == 0)
                        y = ( y + 1 + _world.getHeight() ) % _world.getHeight();
         	if(orientEO == 1)
         		x = ( x + 1 + _world.getWidth() ) % _world.getWidth();
                else if (orientEO == 0)
         		x = ( x - 1 + _world.getWidth() ) % _world.getWidth();

                nbagents =_world.getAgentSize();
                for (int i = 0; i < nbagents; i++)
                {
                    if(x == _world.get(i).x && y == _world.get(i).y && _world.get(i) instanceof PreyAgent)
                    {
                        _world.remove(i);
                        nbagents--;
                        lastmeal = 0;
                    }
                }
                
                it++;
                
                if(it == M && lastmeal < nrjreprod)
                {
                _world.add(new PredatorAgent ((int) x,(int) y, _world));
                 it = 0;
                }
                

	}
	
}
