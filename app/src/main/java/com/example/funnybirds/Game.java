package com.example.funnybirds;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Game extends AppCompatActivity implements OnGameOverListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        GameView gameView = new GameView(this);
        gameView.setGameOverListener(this);

        setContentView(gameView);
    }

    @Override
    public void onGameOver(int points) {
        String username = getIntent().getStringExtra("username");

        Intent intent = new Intent(Game.this, ResultActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("points", points);
        startActivity(intent);
        finish();
    }
}