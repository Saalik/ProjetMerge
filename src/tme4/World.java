package tme4;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import projet.ImageBuffer;


public class World {

	private int dx;
	private int dy;
	
	private double Buffer0[][][];
	private double Buffer1[][][];
        
        
        private CAImageBuffer imbuff;
        Kernel kernel;
        ConvolveOp cOp;
        
        private static float[] flou = { 1f/9f, 1f/9f, 1f/9f,
                 1f/9f, 1f/9f, 1f/9f,
                 1f/9f, 1f/9f, 1f/9f};
        
        
        public static double probGrass = 0.008;
	
	boolean buffering;
	boolean cloneBuffer; // if buffering, clone buffer after swith
	
	private int activeIndex;
        
        private static final int paraNum = 8;
        public static final int alti = 0;
        public static final int temp = 1;
        public static final int tempIso = 5;
        public static final int humiditeSol = 4;
        public static final int pressionSol = 6;
        public static final int eauSurface = 7;
        
        
        public static final int grassSize = 2;
        public static final int grassGreen = 3;
        
	
	private ArrayList<Agent> agents;
        private ArrayList<Item> items;
        //private Soleil sun;
	
	public World ( int dx , int dy, boolean __buffering, boolean __cloneBuffer )
	{
		this.dx = dx;
		this.dy = dy;
		
		buffering = __buffering;
		cloneBuffer = __cloneBuffer;
		
		Buffer0 = new double[dx][dy][paraNum];
		Buffer1 = new double[dx][dy][paraNum];
		activeIndex = 0;
                imbuff = new CAImageBuffer(dx, dy);
                kernel = new Kernel(3, 3, flou);
                cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
                
                
		
		agents = new ArrayList<Agent>();
                items = new ArrayList<Item>();
                
                items.add(new Tree(dx/3+7.5, dy/5*3, this));
                //sun = new Soleil();
		loadAlti("Alti.bmp");
                for ( int x = 0 ; x !=dx ; x++ )
                    for ( int y = 0 ; y !=dy ; y++ )
                    {
                        /*if(x>dx/2)
                        {
                          Buffer0[x][y][alti] = 80;
                          Buffer1[x][y][alti] = 80;
                        }
                        else
                        {
                            Buffer0[x][y][alti] = 1;
                          Buffer1[x][y][alti] = 1;
                        }*/
                          Buffer0[x][y][eauSurface] = 7;
                          Buffer1[x][y][eauSurface] = 7;
                          Buffer0[x][y][grassSize] = 0;
                          Buffer1[x][y][grassSize] = 0;
                    }
	}
	public void loadAlti(String filename)
        {
            ImageBuffer map = ImageBuffer.loadFromDisk(filename);
            for(int x = 0; x<dx; x++)
                for(int y = 0; y<dy; y++)
                    setCellState(x, y, alti, (map.getPixel(x, y)[0]+map.getPixel(x, y)[1]+map.getPixel(x, y)[2])/3);
        }
	public void checkBounds( int __x , int __y )
	{
		if ( __x < 0 || __x > dx || __y < 0 || __y > dy )
		{
			System.err.println("[error] out of bounds ("+__x+","+__y+")");
			System.exit(-1);
		}
	}
	
	public void setCellState ( int x, int y, int para, int val)
	{
		checkBounds (x,y);
			
		if ( buffering == false )
		{
			Buffer0[x][y][para] = val;
			
		}
		else
		{
			if ( activeIndex == 0 )
			{
				Buffer0[x][y][para] = val;
			}
			else
			{
				Buffer1[x][y][para] = val;
			}	
		}
	}
        public void setCellState ( int x, int y, int para, double val)
	{
		checkBounds (x,y);
			
		if ( buffering == false )
		{
			Buffer0[x][y][para] = val;
			
		}
		else
		{
			if ( activeIndex == 0 )
			{
				Buffer0[x][y][para] = val;
			}
			else
			{
				Buffer1[x][y][para] = val;
			}	
		}
	}
        public int getCellState ( int x, int y, int para)
	{
		checkBounds (x,y);
			
		if ( buffering == false )
		{
			return (int) Buffer0[x][y][para];
			
		}
		else
		{
			if ( activeIndex == 0 )
			{
				return (int) Buffer0[x][y][para];
			}
			else
			{
				return (int) Buffer1[x][y][para];
			}	
		}
	}
	
	/**
	 * Update the world state and return an array for the current world state (may be used for display)
	 * @return
	 */
	public void step ( )
	{
		stepWorld();
		stepAgents();
		
		if ( buffering && cloneBuffer )
		{
			/*if ( activeIndex == 0 )
				for ( int x = 0 ; x != dx ; x++ )
					for ( int y = 0 ; y != dy ; y++ )
					{
                                            for(int i = 0; i<paraNum; i++)
						Buffer1[x][y][i] = Buffer0[x][y][i];
					}
			else
				for ( int x = 0 ; x != dx ; x++ )
					for ( int y = 0 ; y != dy ; y++ )
					{
                                            for(int i = 0; i<paraNum; i++)
						Buffer0[x][y][i] = Buffer1[x][y][i];
					}*/

			activeIndex = (activeIndex + 1 ) % 2; // switch buffer index
		}

	}
	
	public double[][][] getCurrentBuffer()
	{
		if ( activeIndex == 0 || buffering == false ) 
			return Buffer0;
		else
			return Buffer1;		
	}
        public double[][][] getLastBuffer()
	{
		if ( activeIndex == 0 || buffering == false ) 
			return Buffer1;
		else
			return Buffer0;		
	}
	
	public int getWidth()
	{
		return dx;
	}
	
	public int getHeight()
	{
		return dy;
	}
	
	public void add (Agent agent)
	{
		agents.add(agent);
	}
        public Agent get (int i)
	{
		return agents.get(i);
	}
        public int getAgentSize ()
	{
		return agents.size();
	}
        public void remove(Agent a)
        {
            agents.remove(a);
        }
        public void remove(int i)
        {
            agents.remove(i);
        }
	
	public void stepWorld() // world THEN agents
	{
            double current[][][] = getCurrentBuffer();
            double last[][][] = getLastBuffer();
            double request[] = new double[8];
            int min;
            int numW;
            double waterBuff;
            double given;
            double reqMin;
            double wVol = 0;
            
            int buff;
            
            for(int x = 0; x<dx; x++)
                for(int y = 0; y<dy; y++)
                    current[x][y][eauSurface] = 0;
            
            for(int x = 0; x<dx; x++)
                for(int y = 0; y<dy; y++)
                {
                    current[x][y][grassSize]= last[x][y][grassSize]+0.1;
                   if(last[x][y][eauSurface]>0.1)
                        current[x][y][grassSize]-=0.1;
                    if(current[x][y][grassSize]>1)
                        current[x][y][grassSize]=1;
                    if(current[x][y][grassSize]<0)
                        current[x][y][grassSize]=0;
                    current[x][y][temp] = (last[x][y][temp]*last[x][y][tempIso]
                                                +((last[x][y>0?(y-1)%dy:dy-1][temp]
                                                + last[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][temp] *3/4
                                                + last[(x+1)%dx][y>0?(y-1)%dy:dy-1][temp] *3/4
                                                + last[x][(y+1)%dy][temp]
                                                + last[x>0?(x-1)%dx:dx-1][(y+1)%dy][temp] *3/4
                                                + last[(x+1)%dx][(y+1)%dy][temp] *3/4
                                                + last[x>0?(x-1)%dx:dx-1][y][temp]
                                                + last[(x+1)%dx][y][temp])/7)
                                                *(100-last[x][y][tempIso]))/100;
                    
                    current[x][y][humiditeSol] = (last[x][y>0?(y-1)%dy:dy-1][humiditeSol]
                                                + last[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][humiditeSol] * 3/4
                                                + last[(x+1)%dx][y>0?(y-1)%dy:dy-1][humiditeSol] *3/4
                                                + last[x][(y+1)%dy][humiditeSol]
                                                + last[x>0?(x-1)%dx:dx-1][(y+1)%dy][humiditeSol] *3/4
                                                + last[(x+1)%dx][(y+1)%dy][humiditeSol] * 3/4
                                                + last[x>0?(x-1)%dx:dx-1][y][humiditeSol]
                                                + last[(x+1)%dx][y][humiditeSol])/7;
                    
                    /*current[x][y][grassSize] = last[x][y][grassSize]
                                                +((last[x][y>0?(y-1)%dy:dy-1][grassSize]
                                                + last[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][grassSize] *3/4
                                                + last[(x+1)%dx][y>0?(y-1)%dy:dy-1][grassSize] *3/4
                                                + last[x][(y+1)%dy][grassSize]
                                                + last[x>0?(x-1)%dx:dx-1][(y+1)%dy][grassSize] *3/4
                                                + last[(x+1)%dx][(y+1)%dy][grassSize] *3/4
                                                + last[x>0?(x-1)%dx:dx-1][y][grassSize]
                                                + last[(x+1)%dx][y][grassSize])/7);*/
                    
                    
                    request[0] = (last[x][y][eauSurface]+last[x][y][alti]*10)-(last[x][y>0?(y-1)%dy:dy-1][eauSurface]+last[x][y>0?(y-1)%dy:dy-1][alti]*10);
                    request[1] = ((last[x][y][eauSurface]+last[x][y][alti]*10)-(last[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][eauSurface]+last[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][alti]*10))*3/4;
                    request[2] = ((last[x][y][eauSurface]+last[x][y][alti]*10)-(last[(x+1)%dx][y>0?(y-1)%dy:dy-1][eauSurface]+last[(x+1)%dx][y>0?(y-1)%dy:dy-1][alti]*10))*3/4;
                    request[3] = (last[x][y][eauSurface]+last[x][y][alti]*10)-(last[x][(y+1)%dy][eauSurface]+last[x][(y+1)%dy][alti]*10);
                    request[4] = ((last[x][y][eauSurface]+last[x][y][alti]*10)-(last[x>0?(x-1)%dx:dx-1][(y+1)%dy][eauSurface]+last[x>0?(x-1)%dx:dx-1][(y+1)%dy][alti]*10))*3/4;
                    request[5] = ((last[x][y][eauSurface]+last[x][y][alti]*10)-(last[(x+1)%dx][(y+1)%dy][eauSurface]+last[(x+1)%dx][(y+1)%dy][alti]*10))*3/4;
                    request[6] = (last[x][y][eauSurface]+last[x][y][alti]*10)-(last[x>0?(x-1)%dx:dx-1][y][eauSurface]+last[x>0?(x-1)%dx:dx-1][y][alti]*10);
                    request[7] = (last[x][y][eauSurface]+last[x][y][alti]*10)-(last[(x+1)%dx][y][eauSurface]+last[(x+1)%dx][y][alti]*10);
                    
                    
                    
                    numW = 8;
                    waterBuff = last[x][y][eauSurface]-last[x][y][eauSurface]/10;
                    given = 0;
                    while(numW>0 && waterBuff>0)
                    {
                        min = 0;
                        numW = 8;
                        for(int i = 0; i<8; i++)
                        {
                            if(request[i]>0.001 && (request[i]<request[min] || request[min]<0))
                                min = i;
                            if(request[i]<0.001)
                                numW--;
                        }
                        reqMin = request[min];
                        //System.out.println(request[min]);
                        /*if(request[min]>0)*/
                            //System.out.println(x+ " " + y);
                        if(reqMin>0)
                        {
                            if(reqMin*numW>waterBuff)
                            {
                                reqMin=waterBuff/numW;
                                //waterBuff = 0;
                            }
                            else
                            {
                                //waterBuff -= request[min]*numW;
                            }
                            if(request[0]>0.001)
                            {
                                current[x][y>0?(y-1)%dy:dy-1][eauSurface]+= reqMin;
                                request[0]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[1]>0.001)
                            {
                                current[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][eauSurface]+= reqMin;
                                request[1]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[2]>0.001)
                            {
                                current[(x+1)%dx][y>0?(y-1)%dy:dy-1][eauSurface]+= reqMin;
                                request[2]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[3]>0.001)
                            {
                                current[x][(y+1)%dy][eauSurface]+= reqMin;
                                request[3]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[4]>0.001)
                            {
                                current[x>0?(x-1)%dx:dx-1][(y+1)%dy][eauSurface]+= reqMin;
                                request[4]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[5]>0.001)
                            {
                                current[(x+1)%dx][(y+1)%dy][eauSurface]+= reqMin;
                                request[5]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[6]>0.001)
                            {
                                current[x>0?(x-1)%dx:dx-1][y][eauSurface]+= reqMin;
                                request[6]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }
                            if(request[7]>0.001)
                            {
                                current[(x+1)%dx][y][eauSurface]+= reqMin;
                                request[7]-=reqMin;
                                waterBuff -= reqMin;
                                given += reqMin;
                            }

                            //System.out.println(x + " "+ y + " "+ waterBuff+ " " + request[min]);
                            
                        }
                        request[min] = -1;
                    }
                    current[x][y][eauSurface]+=(waterBuff+last[x][y][eauSurface]/10);
                    /*if((waterBuff + given - last[x][y][eauSurface])>0.1 ||(waterBuff + given - last[x][y][eauSurface])<-0.1)
                        System.out.println("err" + (waterBuff + given - last[x][y][eauSurface]));*/
                    
                                                
                    
                    
                }
            
                //System.out.println();
            for(int x = 0; x<dx; x++)
                for(int y = 0; y<dy; y++)
                {
                    if(current[x][y][eauSurface]<0)
                        current[x][y][eauSurface] = 0;
                    if(current[x][y][eauSurface]>0)
                    {
                        last[x][y][eauSurface] = (current[x][y][eauSurface]*1
                                                    +((current[x][y>0?(y-1)%dy:dy-1][eauSurface]
                                                    + current[x>0?(x-1)%dx:dx-1][y>0?(y-1)%dy:dy-1][eauSurface] *3/4
                                                    + current[(x+1)%dx][y>0?(y-1)%dy:dy-1][eauSurface] *3/4
                                                    + current[x][(y+1)%dy][eauSurface]
                                                    + current[x>0?(x-1)%dx:dx-1][(y+1)%dy][eauSurface] *3/4
                                                    + current[(x+1)%dx][(y+1)%dy][eauSurface] *3/4
                                                    + current[x>0?(x-1)%dx:dx-1][y][eauSurface]
                                                    + current[(x+1)%dx][y][eauSurface])/7)
                                                    *(100-1))/100;
                        wVol += current[x][y][eauSurface];
                    }
                    
                }
            /*for(int x = 0; x<dx; x++)
                for(int y = 0; y<dy; y++)
                {
                    current[x][y][eauSurface] = last[x][y][eauSurface];
                }*/
            //System.out.println(wVol);
	}
	
	public void stepAgents() // world THEN agents
	{
		for ( int i = 0 ; i < agents.size() ; i++ )
		{
			synchronized ( Buffer0 ) {
				agents.get(i).step();
			}
			
		}
                for ( int i = 0 ; i < items.size() ; i++ )
		{
			synchronized ( Buffer0 ) {
				items.get(i).step();
			}
			
		}
                MyEcosystem_predprey.setProg(items.size());
	}
        public void addItem(Item i)
        {
            items.add(i);
        }
        public void removeItem(Item i)
        {
            items.remove(i);
        }
        
        public void addTree(double x, double y)
        {
            items.add(new Tree(x*dx, y*dy, this));
        }
	
	public void display( CAImageBuffer image )
	{
                imbuff.update(this.getCurrentBuffer());
		cOp.filter(imbuff,image);

		for ( int i = 0 ; i != agents.size() ; i++ )
			image.setPixel((int)agents.get(i).x, (int)agents.get(i).y, agents.get(i)._redValue, agents.get(i)._greenValue, agents.get(i)._blueValue);
                for ( int i = 0 ; i != items.size() ; i++ )
                    items.get(i).display(image);
	}
	
}
