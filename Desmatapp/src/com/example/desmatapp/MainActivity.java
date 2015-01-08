package com.example.desmatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
	private Button bt_plant, bt_lenh, bt_sair;
	// Tipos de jogadores
	private static final int PLANTADOR = 0;
	private static final int LENHADOR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetComponents();
    }

	private void SetComponents() {
		bt_plant = (Button) findViewById(R.id.bt_plant);		
		bt_plant.setOnLongClickListener(new OnLongClickListener() {			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(MainActivity.this, "Comece o jogo como Plantador de Árvores", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		bt_plant.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Iniciar o jogo como plantador
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				intent.putExtra("tipo_jogador", PLANTADOR);
				startActivity(intent);
			}
		});
		
		bt_lenh = (Button) findViewById(R.id.bt_lenh);
		bt_lenh.setOnLongClickListener(new OnLongClickListener() {			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(MainActivity.this, "Comece o jogo como Lenhador de Árvores", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		bt_lenh.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Iniciar o jogo como lenhador
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				intent.putExtra("tipo_jogador", LENHADOR);
				startActivity(intent);
			}
		});
		
		bt_sair = (Button) findViewById(R.id.bt_sair);
		bt_sair.setOnLongClickListener(new OnLongClickListener() {			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(MainActivity.this, "Deixar o jogo", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		bt_sair.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Desconectar do jogo;
				finish();
			}
		});
		
	}


}
