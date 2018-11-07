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


/*
 * ----------------PONG----------------
 * 
 * Made by Jake Goodwin using FXGL
 * 
 * --------RELEASES--------
 * 
 * V.1 - Initial Push to Git with Screen setup --COMPLETE--
 * V.2 - Working Entities with Colour --COMPLETE--
 * V.3 - Basic ball movement and 'Physics' --COMPLETE--
 * V.4 - Working Single Player --COMPLETE--
 * V.5 - Working Multiplayer
 * V.6 - Functioning Main Menu for Single/Multiplayer
 * V.7 - Single player game difficulty 
 * V.8 - Working Textures and Sound
 * V.9 - Code Cleanup and Bug Fixes
 * V1.0 - Final Push to Git, game complete
 * 
 */

public class App extends GameApplication {

	//Entities used for checking collision
	public enum EntityType {
		PADDLE, BALL, WALL
	}

	private Entity player, NPCPlayer, background, topBar, bottomBar, ball;
	Random random = new Random();
	
	int ballY = -5;
	int ballX = random.nextInt(6 - 4 + 1) + 4; //Ball has slight change in speed each round.

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(1200);
		settings.setHeight(800);
		settings.setTitle("Pong");
		settings.setVersion("0.4"); 
		settings.setFullScreenAllowed(true);

	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

	
	
	
	
	@Override
	//Initialise Entities and their values
	protected void initGame() {

		background = Entities.builder().at(0, 0)
				.viewFromNode(new Rectangle(getSettings().getWidth(), getSettings().getHeight(), Color.BLACK))
				.buildAndAttach(getGameWorld());

		player = Entities.builder().type(EntityType.PADDLE).at(100, 400)
				.viewFromNodeWithBBox(new Rectangle(20, 75, Color.WHITE)).with(new CollidableComponent(true))
				.buildAndAttach(getGameWorld());

		NPCPlayer = Entities.builder().type(EntityType.PADDLE).at(getSettings().getWidth() - 125, 400)
				.viewFromNodeWithBBox(new Rectangle(20, 75, Color.WHITE)).with(new CollidableComponent(true))
				.buildAndAttach(getGameWorld());

		topBar = Entities.builder().type(EntityType.WALL).at(50, 50)
				.viewFromNodeWithBBox(new Rectangle(getSettings().getWidth() - 100, 5, Color.WHITE))
				.with(new CollidableComponent(true)).buildAndAttach(getGameWorld());

		bottomBar = Entities.builder().type(EntityType.WALL).at(50, getSettings().getHeight() - 50)
				.viewFromNodeWithBBox(new Rectangle(getSettings().getWidth() - 100, 5, Color.WHITE))
				.with(new CollidableComponent(true)).buildAndAttach(getGameWorld());

		ball = Entities.builder().type(EntityType.BALL).at(getSettings().getWidth() / 2, getSettings().getHeight() / 2)
				.viewFromNodeWithBBox(new Rectangle(15, 15, Color.WHITE)).with(new CollidableComponent(true))
				.buildAndAttach(getGameWorld());

	}

	@Override
	//Initialise and handle user Input
	protected void initInput() {
		Input input = getInput();
		input.addAction(new UserAction("Move Up") {
			@Override
			protected void onAction() {
				if (player.getY() > 75) {
					player.translateY(-4);
				}
			}
		}, KeyCode.UP);

		input.addAction(new UserAction("Move Down") {
			@Override
			protected void onAction() {
				if (player.getY() < getSettings().getHeight() - 150) {
					player.translateY(4);
				}
			}
		}, KeyCode.DOWN);

	}

	@Override
	//Initialise User Interface - The scoreboard
	protected void initUI() {
		
		//Pong Title
		Text title = new Text();
		title.setTranslateX(getSettings().getWidth() / 2 - 90);
		title.setTranslateY(40);
		title.setFont(Font.font(40));
		title.setFill(Color.WHITE);
		title.setText("P O N G");
		getGameScene().addUINode(title);
		
		
		
		//Score for Player
		Text scorePlayer = new Text();
		scorePlayer.setTranslateX(100);
		scorePlayer.setTranslateY(40);
		scorePlayer.setFont(Font.font(25));
		scorePlayer.setFill(Color.WHITE);
		getGameScene().addUINode(scorePlayer);
		scorePlayer.textProperty().bind(getGameState().intProperty("scorePlayer").asString());

		//Score for NPC (or Player 2 when implemented)
		Text scoreComputer = new Text();
		scoreComputer.setTranslateX(getSettings().getWidth() - 125);
		scoreComputer.setTranslateY(40);
		scoreComputer.setFont(Font.font(25));
		scoreComputer.setFill(Color.WHITE);
		getGameScene().addUINode(scoreComputer);
		scoreComputer.textProperty().bind(getGameState().intProperty("scoreComputer").asString());
	}

	@Override
	//Scoreboards
	protected void initGameVars(Map<String, Object> vars) {
		vars.put("scorePlayer", 0);
		vars.put("scoreComputer", 0);
	}

	@Override
	//Check for collision between objects and make the ball react
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
	//The game loop
	protected void onUpdate(double tpf) {

			// If there is a goal, reset positions and increase points.
			//Goal Player 1
		if (ball.getX() > getSettings().getWidth()) {
			getGameState().increment("scorePlayer", +1);

			ball.setX(getSettings().getWidth() / 2);
			ball.setY(getSettings().getHeight() / 2);
			player.setX(100);
			player.setY(400);
			NPCPlayer.setX(getSettings().getWidth() - 125);
			NPCPlayer.setY(400);

			Random random = new Random();
			Boolean direction;
			if (ballX > 0) {
				direction = true;
			} else {
				direction = false;
			}
			int ballX = random.nextInt(6 - 4 + 1) + 4;
			if (direction)
				ballX = -ballX;

			//Goal Player 2
		} else if (ball.getX() < 0) {
			getGameState().increment("scoreComputer", +1);

			ball.setX(getSettings().getWidth() / 2);
			ball.setY(getSettings().getHeight() / 2);
			player.setX(100);
			player.setY(400);
			NPCPlayer.setX(getSettings().getWidth() - 125);
			NPCPlayer.setY(400);

			Random random = new Random();
			Boolean direction;
			if (ballX > 0) {
				direction = true;
			} else {
				direction = false;
			}
			int ballX = random.nextInt(6 - 4 + 1) + 4;
			if (direction)
				ballX = -ballX;

		}

		//Control the movement of the NPC Player - Moves paddle to the middle whilst waiting for ball return
		if (NPCPlayer.getY() < ball.getY() && NPCPlayer.getY() < getSettings().getHeight() - 150 && ballX > 0) {
			NPCPlayer.translateY(4.5);
		} else if (NPCPlayer.getY() > ball.getY() && ballX > 0) {
			NPCPlayer.translateY(-4.5);
		} else if (NPCPlayer.getY() > 400 && ballX < 0) {
			NPCPlayer.translateY(-4.5);
		} else if (NPCPlayer.getY() < 400 && ballX < 0) {
			NPCPlayer.translateY(4.5);
		}

		//Move ball each frame
		ball.translateX(ballX);
		ball.translateY(ballY);

	}

}