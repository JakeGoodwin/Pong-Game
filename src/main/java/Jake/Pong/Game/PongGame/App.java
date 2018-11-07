package Jake.Pong.Game.PongGame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import java.util.Map;
import java.util.Random;

public class App extends GameApplication {

	
	public enum EntityType {
	    PADDLE, BALL, WALL
	}
	
	
	private Entity player, NPCPlayer,  background, topBar, bottomBar;
	private Entity ball;
	double ballY = -5.5;
	Random random = new Random();
	int ballX = random.nextInt(5 - 2 + 1) + 2;

	
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(800);
        settings.setTitle("Pong");
        settings.setVersion("0.3");
        settings.setFullScreenAllowed(true);
      
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    protected void initGame() {
    	
    	background = Entities.builder()
    						.at(0,0)
    						.viewFromNode(new Rectangle(getSettings().getWidth(), getSettings().getHeight(), Color.BLACK))
    						.buildAndAttach(getGameWorld());
    	
    	player = Entities.builder()
    					 .type(EntityType.PADDLE)
    					 .at(100,400)
    					 .viewFromNodeWithBBox(new Rectangle(20,75, Color.WHITE))
    					 .with(new CollidableComponent(true))
    					 .buildAndAttach(getGameWorld());
    	
    	NPCPlayer = Entities.builder()
    			 .type(EntityType.PADDLE)
				 .at(getSettings().getWidth() - 125 ,400)
				 .viewFromNodeWithBBox(new Rectangle(20,75, Color.WHITE))
				 .with(new CollidableComponent(true))
				 .buildAndAttach(getGameWorld());
    	
    	
    	topBar = Entities.builder()
    			 .type(EntityType.WALL)
				 .at(50,50)
				 .viewFromNodeWithBBox(new Rectangle(getSettings().getWidth() - 100,5, Color.WHITE))
				 .with(new CollidableComponent(true))
				 .buildAndAttach(getGameWorld());
    	
    	bottomBar = Entities.builder()
    			 .type(EntityType.WALL)
				 .at(50,getSettings().getHeight() - 50)
				 .viewFromNodeWithBBox(new Rectangle(getSettings().getWidth() - 100,5, Color.WHITE))
				 .with(new CollidableComponent(true))
				 .buildAndAttach(getGameWorld());
    	
    	ball = Entities.builder()
    			.type(EntityType.BALL)
    			.at(getSettings().getWidth() /2, getSettings().getHeight() /2)
    			.viewFromNodeWithBBox(new Rectangle(15,15, Color.WHITE))
    			.with(new CollidableComponent(true))
				 .buildAndAttach(getGameWorld());
    	
    }
    
    @Override
    protected void initInput() {
    	Input input = getInput();
    	input.addAction(new UserAction("Move Up"){
    		@Override
    		protected void onAction() {
    			if(player.getY() > 75) {
    			player.translateY(-4);
    			}
    		}
    	}, KeyCode.UP);
    	
    	input.addAction(new UserAction("Move Down"){
    		@Override
    		protected void onAction() {
    			if(player.getY() < getSettings().getHeight() - 150) {
    			player.translateY(4);
    			}
    		}
    	}, KeyCode.DOWN);
    	
    }
    
    
    @Override
    protected void initUI() {
    	Text scorePlayer = new Text();
    	scorePlayer.setTranslateX(100);
    	scorePlayer.setTranslateY(40);
    	scorePlayer.setFont(Font.font(25));
    	scorePlayer.setFill(Color.WHITE);
    	getGameScene().addUINode(scorePlayer);
    	scorePlayer.textProperty().bind(getGameState().intProperty("scorePlayer").asString());
    	
    	Text scoreComputer = new Text();
    	scoreComputer.setTranslateX(getSettings().getWidth() - 125);
    	scoreComputer.setTranslateY(40);
    	scoreComputer.setFont(Font.font(25));
    	scoreComputer.setFill(Color.WHITE);
    	getGameScene().addUINode(scoreComputer);
    	scoreComputer.textProperty().bind(getGameState().intProperty("scoreComputer").asString());
    }
    
    
    
	@Override
	protected void initGameVars(Map<String, Object> vars) {
    vars.put("scorePlayer", 0);
   
    vars.put("scoreComputer", 0);
	}
	
	
	@Override
	protected void initPhysics() {
	    getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PADDLE, EntityType.BALL) {

	        // order of types is the same as passed into the constructor
	        @Override
	        protected void onCollisionBegin(Entity player, Entity ball) {
	            ballX = -ballX;
	        }
	    });
	    
	    getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {

	        // order of types is the same as passed into the constructor
	        @Override
	        protected void onCollisionBegin(Entity ball, Entity topBar) {
	            ballY = -ballY;
	        }
	    });
	    
	    getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {

	        // order of types is the same as passed into the constructor
	        @Override
	        protected void onCollisionBegin(Entity ball, Entity bottomBar) {
	            ballY = -ballY;
	        }
	    });
	    
	    
	    
	}
	
	

	@Override
	protected void onUpdate(double tpf) {
		
		//If there is a goal, reset positions and increase points.
		if(ball.getX() > getSettings().getWidth())
		{
			getGameState().increment("scorePlayer", +1);

			ball.setX(getSettings().getWidth() /2);
			ball.setY(getSettings().getHeight() /2);
			player.setX(100);
			player.setY(400);
			NPCPlayer.setX(getSettings().getWidth() - 125);
			NPCPlayer.setY(400);
			
			Random random = new Random();
			Boolean direction;
			if(ballX > 0)
			{
				direction = true;
			}
			else {
				direction = false;
			}
			ballX = random.nextInt(5 - 2 + 1) + 2;
			if(direction)	ballX = -ballX;
			
			
		}
		else if(ball.getX() < 0){
			getGameState().increment("scoreComputer", +1);
			
			ball.setX(getSettings().getWidth() /2);
			ball.setY(getSettings().getHeight() /2);
			player.setX(100);
			player.setY(400);
			NPCPlayer.setX(getSettings().getWidth() - 125);
			NPCPlayer.setY(400);
			
			Random random = new Random();
			Boolean direction;
			if(ballX > 0)
			{
				direction = true;
			}
			else {
				direction = false;
			}
			ballX = random.nextInt(5 - 2 + 1) + 2;
			if(direction)	ballX = -ballX;

		}
	
		
		
		
		
		if(NPCPlayer.getY() < ball.getY() && NPCPlayer.getY() < getSettings().getHeight() - 150 && ballX > 0)
		{
			NPCPlayer.translateY(4.5);
		}
		else if(NPCPlayer.getY() > ball.getY() && ballX > 0)
		{
			NPCPlayer.translateY(-4.5);
		}
		else if(NPCPlayer.getY() > 400 && ballX < 0)
		{
			NPCPlayer.translateY(-4.5);
		}
		else if(NPCPlayer.getY() < 400 && ballX < 0)
		{
			NPCPlayer.translateY(4.5);
		}
		
		
		
		//NPCPlayer.translateY(ballY);
		
		
		
		
		ball.translateX(ballX);
		ball.translateY(ballY);
		
		
		
	}
	

	

    
}