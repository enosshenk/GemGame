package com.shenko.rockhound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class RockhoundGame implements Screen{

	final int WIDTH = 1024;
	final int HEIGHT = 584;
	final int GRID_SIZE = 40;
	final int GRID_COUNT_X = 25;
	final int GRID_COUNT_Y = 14;
	
	public int GemsRemaining = 0;

	public int GameMode;		// 0 = Waiting for move, 1 = Destroying, 2 = Collapsing, 3 = Column shifting
	public Gem[][] Gems = new Gem[GRID_COUNT_X][GRID_COUNT_Y];
	
	public int GemsDestroyed = 0;
	public int Score = 0;
	
	private Texture GridTexture, Gem0Texture, Gem1Texture, Gem2Texture, Gem3Texture;
	private SpriteBatch Batch;
	private OrthographicCamera Camera;
	private BitmapFont RockhoundFont;
	
	@Override
	public void render(float delta) {
		// Update gems
		if (GameMode == 1)
		{
			int x, y, count;
			count = 0;
			
			for (x=0; x < GRID_COUNT_X; x++)
			{
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					if (Gems[x][y] != null)
					{
						if (Gems[x][y].PendingDestroy)
						{
							count += 1;
							Gems[x][y] = null;
							GemsDestroyed += 1;
						}
					}
				}
			}
			
			if (count == 0)
			{
				// All gems are removed
				UpdateGrid(0);
				System.out.print(GemsDestroyed + " gems removed.\n");
				Score += 10 * GemsDestroyed;
				GemsRemaining -= GemsDestroyed;
			}
		}
		
		if (GameMode == 2)
		{
			// Handle gem collapse animations
			int x, y, Count;
			Count = 0;
			
			for (x=0; x < GRID_COUNT_X; x++)
			{
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					if (Gems[x][y] != null && Gems[x][y].Collapsing)
					{
						if (Gems[x][y].CurrentPixel.y > Gems[x][y].EndPixel.y)
						{
							Gems[x][y].CurrentPixel.y = Lerp(Gems[x][y].CurrentPixel.y, Gems[x][y].EndPixel.y, 0.1f);
							Count += 1;
						}
						else
						{
							Gems[x][y].Collapsing = false;
						}
					}
				}
			}
			
			if (Count == 0)
			{
				// All collapses are finished
				UpdateGrid(1);
			}
		}
		
		if (GameMode == 3)
		{
			// Handle column slide animations
			int x, y, Count;
			Count = 0;
			
			for (x=0; x < GRID_COUNT_X; x++)
			{
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					if (Gems[x][y] != null && Gems[x][y].Sliding)
					{
						if (Gems[x][y].CurrentPixel.x > Gems[x][y].EndPixel.x)
						{
							Gems[x][y].CurrentPixel.x -= 3;
							Count += 1;
						}
						else
						{
							Gems[x][y].Sliding = false;
						}
					}
				}
			}
			
			if (Count == 0)
			{
				// All slides are finished
				UpdateGrid(1);
			}			
		}
		
		// Rendering
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        GridTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);		
		
        Batch.begin();
        Batch.draw(GridTexture, 0, 0, 1024, 584);
        
        if (GameMode == 0 || GameMode == 1)
        {
        	// Draw gems at grid locations in game mode 0
			int x, y;
			for (x=0; x < GRID_COUNT_X; x++)
			{
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					if (Gems[x][y] != null)
					{
						GridPoint2 DrawLoc = GridToPixel(Gems[x][y].Grid);
						
						switch (Gems[x][y].Type)
						{
							case 0: 
								Gem0Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem0Texture, DrawLoc.x, DrawLoc.y, 40, 40); 
								break;
							case 1: 
								Gem1Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem1Texture, DrawLoc.x, DrawLoc.y, 40, 40); 
								break;
							case 2: 
								Gem2Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem2Texture, DrawLoc.x, DrawLoc.y, 40, 40); 
								break;
							case 3: 
								Gem3Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem3Texture, DrawLoc.x, DrawLoc.y, 40, 40);
								break;
						}
					}
				}
			}
        }
        else if (GameMode == 2 || GameMode == 3)
        {
        	// Collapsing game mode, draw gems at currentpixel locations
			int x, y;
			for (x=0; x < GRID_COUNT_X; x++)
			{
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					if (Gems[x][y] != null)
					{
						GridPoint2 DrawLoc = Gems[x][y].CurrentPixel;
						
						switch (Gems[x][y].Type)
						{
							case 0: 
								Gem0Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem0Texture, DrawLoc.x, DrawLoc.y, 40, 40); 
								break;
							case 1: 
								Gem1Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem1Texture, DrawLoc.x, DrawLoc.y, 40, 40); 
								break;
							case 2: 
								Gem2Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem2Texture, DrawLoc.x, DrawLoc.y, 40, 40); 
								break;
							case 3: 
								Gem3Texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
								Batch.draw(Gem3Texture, DrawLoc.x, DrawLoc.y, 40, 40);
								break;
						}
					}
				}
			}        	
        }
		
		RockhoundFont.draw(Batch, "Score: " + Score + " Mode: " + GameMode, 32, 580);
		RockhoundFont.draw(Batch, "Gems Remaining: " + GemsRemaining, 720, 580);
        
        Batch.end();
        
	}

	@Override
	public void show() {
				
		GridTexture = new Texture(Gdx.files.internal("data/grid2.png"));
		
		Gem0Texture = new Texture(Gdx.files.internal("data/brick1.tga")); 
		Gem1Texture = new Texture(Gdx.files.internal("data/brick2.tga")); 
		Gem2Texture = new Texture(Gdx.files.internal("data/brick3.tga")); 
		Gem3Texture = new Texture(Gdx.files.internal("data/brick4.tga")); 
				
		Camera = new OrthographicCamera();
		Camera.setToOrtho(false);
		
		Batch = new SpriteBatch();
		Batch.setProjectionMatrix(Camera.combined);
		
		RockhoundFont = new BitmapFont();
		
		GameMode = 0;
		
		Gdx.input.setInputProcessor(new GestureDetector(new RockhoundGestureListener()));
		
		BuildGrid();
	}
	
	public void BuildGrid()
	{
		int x, y;
		for (x=0; x < GRID_COUNT_X; x++)
		{
			for (y=0; y < GRID_COUNT_Y; y++)
			{
				Gems[x][y] = new Gem(new GridPoint2(x, y), (int)(Math.random() * 4));
				Gems[x][y].Game = this;
				GemsRemaining += 1;
			}
		}
		
		for (x=0; x < GRID_COUNT_X; x++)
		{
			for (y=0; y < GRID_COUNT_Y; y++)
			{
				Gems[x][y].UpdateLinks();
			}
		}		
	}
	
	public void UpdateGrid(int Mode)
	{
		int x, y, Count;
		
		if (Mode == 0)
		{
			for (x=0; x < GRID_COUNT_X; x++)
			{
				// Iterate all columns and collapse them all, dropping gems down
				Collapse(x);
			}
			
			GameMode = 2;
		}
		else if (Mode == 1)
		{
			Count = 0;
			for (x=0; x < GRID_COUNT_X; x++)
			{			
				if (ColumnCount(x) == 0 && x + 1 < GRID_COUNT_X)
				{
					Count += 1;
					ColumnShift(x + 1);
				}
			} 
			
			GameMode = 3;
			if (Count == 0)
			{
				UpdateGrid(2);
			}
		}
		else if (Mode == 2)
		{
			for (x=0; x < GRID_COUNT_X; x++)
			{
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					// Iterate all gems and tell them to update links
					if (Gems[x][y] != null)
					{
						Gems[x][y].UpdateLinks();
					}
				}
			}
			
			GameMode = 0;
		}
	}
	
	public void ColumnShift(int Column)
	{
		// Shifts the entire given column of gems into the leftmost column
		// Check column first before executing
		int y;
		if (ColumnCount(Column) != 0)
		{
			for (y=0; y < GRID_COUNT_Y; y++)
			{
				if (Gems[Column][y] != null)
				{
					Gems[Column][y].StartPixel = GridToPixel(Gems[Column][y].Grid);
					Gems[Column][y].CurrentPixel = GridToPixel(Gems[Column][y].Grid);
					Gems[Column][y].EndPixel = GridToPixel(new GridPoint2(Column - 1, y));
					Gems[Column][y].Sliding = true;
					
					Gems[Column - 1][y] = Gems[Column][y];
					Gems[Column - 1][y].Grid.x -= 1;
					Gems[Column][y] = null;
				}
			}
		}
	}
	
/*	public void ColumnShift()
	{	
		Gem[][] FoundGems = new Gem[GRID_COUNT_X][GRID_COUNT_Y];
		int x, y;
		int FullColumns = 0;
		int Found = 0;
		
		for (x=0; x < GRID_COUNT_X; x++)
		{			
			if (ColumnCount(x) != 0 || Found != 0)
			{				
				for (y=0; y < GRID_COUNT_Y; y++)
				{
					if (Gems[x][y] != null)
					{
						Gems[x][y].StartPixel = GridToPixel(Gems[x][y].Grid);
						Gems[x][y].CurrentPixel = GridToPixel(Gems[x][y].Grid);
						Gems[x][y].EndPixel = GridToPixel(new GridPoint2(Found, y));
						FoundGems[Found][y] = Gems[x][y];						
					}
				}
				FullColumns += 1;
				Found += 1;
			}
			
			for (y=0; y < GRID_COUNT_Y; y++)
			{
				Gems[x][y] = null;
			}
		}
		
		System.out.print("Found " + FullColumns + " full columns\n");
		
		for (x=0; x < FullColumns; x++)
		{
			for (y=0; y < GRID_COUNT_Y; y++)
			{
				Gems[x][y] = FoundGems[x][y];
				if (Gems[x][y] != null)
				{
					Gems[x][y].Sliding = true;
				}
			}
		}		
	} */
	
	public void Collapse(int Column)
	{
		// Collapses all gems in given column down
		
		int Count = ColumnCount(Column);
		Gem[] FoundGems = new Gem[Count + 1];
		int y, i;
		
		i = 0;
		for (y=0; y < GRID_COUNT_Y; y++)
		{
			if (Gems[Column][y] != null)
			{
				FoundGems[i] = Gems[Column][y];
				Gems[Column][y] = null;
				i++;
			}
		}
		
		for (y=0; y < Count; y++)
		{
			Gems[Column][y] = FoundGems[y];
			
			Gems[Column][y].StartPixel = GridToPixel(Gems[Column][y].Grid);
			Gems[Column][y].CurrentPixel = GridToPixel(Gems[Column][y].Grid);
			Gems[Column][y].EndPixel = GridToPixel(new GridPoint2(Column, y));
			Gems[Column][y].Collapsing = true;
			
			Gems[Column][y].Grid.x = Column;
			Gems[Column][y].Grid.y = y;
		}
	}
	
	public int ColumnCount(int Column)
	{
		int y, FoundY;
		FoundY = 0;
		for (y=0; y < GRID_COUNT_Y; y++)
		{
			if (Gems[Column][y] != null)
			{
				FoundY += 1;
			}
		}
		
		return FoundY;
	}
	
	public GridPoint2 PixelToGrid(GridPoint2 inPixels)
	{
		GridPoint2 OutPoints = new GridPoint2();
		
		OutPoints.x = (int)Math.floor((inPixels.x - 12) / GRID_SIZE);
		OutPoints.y = (int)Math.floor((inPixels.y) / GRID_SIZE);
		
		return OutPoints;
	}
	
	public GridPoint2 GridToPixel(GridPoint2 inGrid)
	{
		GridPoint2 OutPoints = new GridPoint2();
		
		OutPoints.x = (int)(Math.floor((inGrid.x + 1) * GRID_SIZE) - 28);
		OutPoints.y = (int)(Math.floor(inGrid.y * GRID_SIZE));
		
		return OutPoints;		
	}
	
	public float Lerp(float v0, float v1, float alpha)
	{
		return v0 + (v1 - v0) * alpha;
	}
	
	public int Lerp(int v0, int v1, float alpha)
	{
		return (int)(v0 + (v1 - v0) * alpha);
	}	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public class RockhoundGestureListener implements GestureListener
	{

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (x > 12 && x < 1012 && y > 24 && GameMode == 0)
			{
				GridPoint2 TouchGrid = PixelToGrid(new GridPoint2((int)x, (int)(584 - y)));
				
				if (Gems[TouchGrid.x][TouchGrid.y] != null)
				{
					if (Gems[TouchGrid.x][TouchGrid.y].Links != 0)
					{
						GameMode = 1;
						GemsDestroyed = 0;
						Gems[TouchGrid.x][TouchGrid.y].Destroy();
						System.out.print("Tapped gem at x" + TouchGrid.x + " y" + TouchGrid.y + ", moving to GameMode 1.\n");
					}
					else
					{
						System.out.print("Tapped single gem at x" + TouchGrid.x + " y" + TouchGrid.y + ".\n");
					}
					
				}
				else
				{
					System.out.print("Tapped empty cell at x" + TouchGrid.x + " y" + TouchGrid.y + ".\n");
				}
			}
			return true;
		}

		@Override
		public boolean longPress(float x, float y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
				Vector2 pointer1, Vector2 pointer2) {
			// TODO Auto-generated method stub
			return false;
		}
}
	
}
