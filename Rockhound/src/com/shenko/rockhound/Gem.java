package com.shenko.rockhound;

import com.badlogic.gdx.math.GridPoint2;

public class Gem {

	public RockhoundGame Game;
	public GridPoint2 Grid;
	public int Type = 0;
	public int Links = 0;
	public boolean LinkUp, LinkDown, LinkLeft, LinkRight = false;
	public boolean PendingDestroy = false;
	public boolean Collapsing, Sliding = false;
	
	public GridPoint2 StartPixel, EndPixel, CurrentPixel;
	
	public Gem(GridPoint2 inGrid, int inType)
	{
		Grid = new GridPoint2(inGrid);
		StartPixel = new GridPoint2();
		EndPixel = new GridPoint2();
		CurrentPixel = new GridPoint2();
		Type = inType;
	}
	
	public void UpdateLinks()
	{
		Links = 0;
		
		if (Grid.y != 13 && Game.Gems[Grid.x][Grid.y + 1] != null)
		{
			if (Game.Gems[Grid.x][Grid.y + 1].Type == this.Type)
			{
				// Above
				LinkUp = true;
				Links += 1;
			}
			else
			{
				LinkUp = false;
			}
		}
		if (Grid.y != 0 && Game.Gems[Grid.x][Grid.y - 1] != null)
		{
			if (Game.Gems[Grid.x][Grid.y - 1].Type == this.Type)
			{
				// Below
				LinkDown = true;
				Links += 1;
			}
			else
			{
				LinkDown = false;
			}
		}	
		if (Grid.x != 24 && Game.Gems[Grid.x + 1][Grid.y] != null)
		{
			if (Game.Gems[Grid.x + 1][Grid.y].Type == this.Type) {
				// Right
				LinkRight = true;
				Links += 1;
			}
			else
			{
				LinkRight = false;
			}
		}
		if (Grid.x != 0 && Game.Gems[Grid.x - 1][Grid.y] != null)
		{
			if (Game.Gems[Grid.x - 1][Grid.y].Type == this.Type)
			{
				// Left
				LinkLeft = true;
				Links += 1;
			}
			else
			{
				LinkLeft = false;
			}
		}
	}
	
	public void Destroy()
	{
		if (!PendingDestroy)
		{
			PendingDestroy = true;
			Game.GemsRemaining -= 1;
				
			if (LinkUp && Game.Gems[Grid.x][Grid.y + 1] != null)
			{
				Game.Gems[Grid.x][Grid.y + 1].Destroy();
			}
			if (LinkDown && Game.Gems[Grid.x][Grid.y - 1] != null)
			{
				Game.Gems[Grid.x][Grid.y - 1].Destroy();
			}
			if (LinkLeft && Game.Gems[Grid.x - 1][Grid.y] != null)
			{
				Game.Gems[Grid.x - 1][Grid.y].Destroy();
			}
			if (LinkRight && Game.Gems[Grid.x + 1][Grid.y] != null)
			{
				Game.Gems[Grid.x + 1][Grid.y].Destroy();
			}
			
		}
	}	
}
