/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tme4;

import java.util.ArrayList;

/**
 *
 * @author Arnaud
 */
public class Tree extends Item
{
    protected int size;
    protected ArrayList<Tree> nearby;
    public static int probGrow = 2;
    private static int round = 0;
    private int lastCheckedRound;
    private static int distNearby = 6;
    private static double probGetFire = 0.00001;
    private static double probStopFire = 0.01;
    private static double probReborn = 0.001;
    private static double probDeadFall = 1;
    private boolean onFire;
    private boolean dead;
    
    
    public Tree(double x, double y, World w)
    {
        size = 1;
        this.x = x;
        this.y = y;
        this.world = w;
        nearby = new ArrayList<Tree>();
        lastCheckedRound = round-1;
        onFire = false;
    }
    public void display(CAImageBuffer image)
    {
        if(!onFire)
            image.show(x, y, 0, size, 0);
        else if(!dead)
             image.show(x, y, 250, 0, 0);
        else
            image.show(x, y, 100, 100, 100);
    }
    
    public void step()
    {
        double nx = x+(Math.random())*10-5;
        double ny = y+(Math.random())*10-5;
        
        
        if(onFire)
        {
            world.setCellState((int) x, (int) y, world.temp, world.getCellState((int) x, (int) y, world.temp)+size);
            size--;
            if(Math.random()<probStopFire)
                onFire = false;
        }
        else if(world.getCellState((int) x, (int) y, world.temp)>size||Math.random()<probGetFire)
            onFire = true;
        else if(world.getCellState((int) x, (int) y, World.eauSurface)>0)
        {
            size-=world.getCellState((int) x, (int) y, World.eauSurface);
        }
        else 
        {
            if(Math.random()<(1/Math.pow(size/100,2))/4)
                size++;
            if(nx>0&&nx<world.getWidth()&&ny>0&&ny<world.getHeight()&&Math.random()<(1/Math.pow(nearby.size(),2))/5 /*Math.pow(Math.random(),3)/probGrow>Math.random()
                    &&*//**Math.random()*(-Math.pow(((world.getCellState((int)nx,(int)ny, world.alti)-128)/128),2)+1)>0.5*/)
            {
                Tree t = new Tree(nx,ny,world);
                creatNearby(t,round++);
                world.addItem(t);
            }
        }
        if(size <=0)
        {
            dead = true;
            onFire = false;
        }
        if(dead && Math.random()<probReborn)
        {
            size = 1;
            dead = false;
        }
        if(dead && Math.random()<probDeadFall)
        {
            world.removeItem(this);
            for(int i =0; i<nearby.size();i++)
                nearby.get(i).removeNearby(this);
        }
    }
    public void creatNearby(Tree t, int r)
    {
        if(lastCheckedRound != r)
        {
            lastCheckedRound = r;
            for(int i =0; i<nearby.size();i++)
            {
                if(!nearby.get(i).equals(t))
                {
                    nearby.get(i).creatNearby(t, r);
                }
            }
            if(Math.sqrt(Math.pow(x-t.x,2)+Math.pow(y-t.y,2))<distNearby)
                addNearby(t);
        }
    }
    public void addNearby(Tree t)
    {
        if(!nearby.contains(t))
        {
            nearby.add(t);
            t.addNearby(this);
        }
    }
    public void removeNearby(Tree t)
    {
        nearby.remove(t);
    }
}
