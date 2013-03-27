package tme4;




public class PreyAgent extends Agent {

	boolean _alive;
        int it = 0;
        static int N = 250;
        static int O = 200;
        int lastmeal = 0;
        int Radius= 20;
        int orientNS;
        int orientEO;
        int tired = 0;
        static int nrjreprod = 25;
        boolean deadeatable;
        int maxeatable = 40;
        int eatablesince;
        
        
	public PreyAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		_redValue = 0;
		_greenValue = 128;
		_blueValue = 255;
		
		_alive = true;
	}
	
	public void step( )
	{
		// met a jour l'agent
		
		// ... A COMPLETER
                if(deadeatable == true && eatablesince > (maxeatable-1)){
                     world.remove(this);
                    return;
                }
            
                if(deadeatable == true){
                    eatablesince++;
                    return;
                }
            
                if(lastmeal >= O){
                    deadeatable = true;
                    eatablesince = 0;
                    this._redValue= 140;
                    this._blueValue= 140;
                    this._greenValue= 140;
                    return;
                }

                
                lastmeal++;
                
                tired++;
                
                if(tired == 5){
                    tired = 0;
                    return;
                }
                
		double grassSize = _world.getCellState((int) x, (int) y, World.grassSize);
                
                if(grassSize>0.7)
                {
                    lastmeal = 0;
                    world.setCellState((int) x, (int) y, World.grassSize, grassSize>0.7?grassSize-0.7:0);
                }


                
                //if(lastmeal >= O)
                //{
                //    _world.agents.remove(this);
                //    return;
                //}
                
                
                int nbagents =world.getAgentSize();
                
                boolean found = false;
                double distMin=300;
                double dist;
                
                for (int i = 0; i < nbagents; i++)
                {
                    found = true;
                    dist = Math.sqrt(Math.pow(x+1-_world.get(i).x,2)+Math.pow(y+1-_world.get(i).y,2));
                    if(dist<Radius && dist<distMin && _world.get(i) instanceof PredatorAgent)
                    {
                        distMin = dist;
                            if(x>_world.get(i).x)
                                orientEO = 1;
                            else if (x<_world.get(i).x)
                                orientEO = 0;
                            else
                                orientEO = -1; 
                            if(y<_world.get(i).y)
                                orientNS = 1;
                            else if (y>_world.get(i).y)
                                orientNS = 0;
                            else
                                orientNS = -1;
                    }
                    
                    else
                    {
                        found = false;
                    }
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
         		y = ( y - 1 + world.getHeight() ) % _world.getHeight();
                else if (orientNS == 0)
                        y = ( y + 1 + _world.getHeight() ) % _world.getHeight();
         	if(orientEO == 1)
         		x = ( x + 1 + _world.getWidth() ) % _world.getWidth();
                else if (orientEO == 0)
         		x = ( x - 1 + _world.getWidth() ) % _world.getWidth();
                it++;
                
                if(it == N && lastmeal < nrjreprod)
                {
                _world.add( new PreyAgent ((int) x, (int)y, _world));
                 it = 0;
                }
                
		
	}
	
}
