package tme4;


public class MyEcosystem_predprey
{

        private static ImageFrame winHandle;
        public static void setProg(int i)
        {
            if(winHandle != null)
            {
                winHandle.setProg(i);
            }
        }
	public static void main(String[] args) {

		// initialisation generale
	    
		int dx = 200;
		int dy = 200;
		
		int displayWidth = dx;  // 200
		int displayHeight = dy; // 200

		// pick dimension for display
		if ( displayWidth < 200 )
			displayWidth = 200;
		else
			if ( displayWidth > 600 )
				displayWidth = 600;
			else
				if ( displayWidth < 300 )
					displayWidth = displayWidth * 2; 
		if ( displayHeight < 200 )
			displayHeight = 200;
		else
			if ( displayHeight > 600 )
				displayHeight = 600;
			else
				if ( displayHeight < 300 )
					displayHeight = displayHeight * 2; 
		
		
		int delai = 20;//100; // -- delay before refreshing display -- program is hold during delay, even if no screen update was requested. USE WITH CARE. 
		int nombreDePasMaximum = Integer.MAX_VALUE;
		int it = 0;
		int displaySpeed = 1;//50; // from 1 to ...
		
		CAImageBuffer image = new CAImageBuffer(dx,dy);
                winHandle = ImageFrame.makeFrame("Hey!", image, delai, dx, dy);
                
	    // initialise l'ecosysteme
	    
		World world = new World(dx,dy,true,true);
		winHandle.setEnvWorld(world);
                
		for ( int i = 0 ; i != 10 ; i++ )
			world.add(new PreyAgent((int)(Math.random()*dx),(int)(Math.random()*dy),world));
		for ( int i = 0 ; i != 10 ; i++ )
			world.add(new PredatorAgent((int)(Math.random()*dx),(int)(Math.random()*dy),world));
		
	    // mise a jour de l'�tat du monde
		
		while ( it != nombreDePasMaximum )
		{
			// 1 - display
			
			if ( it % displaySpeed == 0 )
				world.display(image); 

			// 2 - update
						
			world.step();
						
			// 3 - iterate
			
			it++;
			
			try {
				Thread.sleep(delai);
			} catch (InterruptedException e) 
			{
			}
		}
		
	}

}
