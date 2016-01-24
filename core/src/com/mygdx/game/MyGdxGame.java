package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.Starships;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	private Socket socket;
	Starships player;
	Texture playerShip;
	Texture friendlyShip;
	HashMap<String, Starships> friendlyPlayers;


	@Override
	public void create () {
		batch = new SpriteBatch();
		playerShip =  new Texture("playerShip2.png");
		friendlyShip = new Texture("playerShip.png");
		friendlyPlayers = new HashMap<String, Starships>();
		connectSocket();
		configSocketEvents();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput(Gdx.graphics.getDeltaTime());
		batch.begin();
		if(player != null) {
			player.draw(batch);
		}
		for(HashMap.Entry<String, Starships> entry: friendlyPlayers.entrySet()) {
			entry.getValue().draw(batch);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		super.dispose();
		playerShip.dispose();
		friendlyShip.dispose();
	}

	public void handleInput(float dt) {
		if(player != null) {
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				player.setPosition(player.getX() + (-200 * dt), player.getY());
			} else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				player.setPosition(player.getX() + (200 * dt), player.getY());
			}
		}
	}

	public void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();

		} catch(Exception e) {
			System.out.println(e);
		}
	}

	public void configSocketEvents() {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				player = new Starships(playerShip);


			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				//System.out.print("Hi");
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + id);
				} catch(JSONException e) {
					Gdx.app.log("SocketIO", "Error getting Player ID");
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				//System.out.print("Hi");
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "New Player Connect: " + id);
					friendlyPlayers.put(id, new Starships(friendlyShip));
				} catch(JSONException e) {
					Gdx.app.log("SocketIO", "Error getting new Player ID");
				}
			}

		});
	}
}
