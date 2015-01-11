package com.example.desmatapp;

import com.example.desmatapp.util.Globals;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GameActivity extends Activity {
		private Button bt_act1, bt_act2,bt_act3, bt_sair;
		private ImageView[][] tabuleiro;
		private ImageButton ib_up, ib_right, ib_down, ib_left;
		private int[] pos_atual;
		private int tipo,acao;
		// Tipos de jogadores
		private static final int PLANTADOR = 1;
		private static final int LENHADOR = 2;
		// Tipos de ações
		private static final int PLANTAR = 10;
		private static final int CERCA = 20;
		private static final int REGAR = 30;
		private static final int CORTAR = 40;
		private static final int DESTRUIR = 50;
		
		
		

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_game);
	        tipo = 0;
	        Bundle extras = getIntent().getExtras();
	        if (extras != null) {
	            tipo = extras.getInt("tipo_jogador");
	        }
	        SetComponents();
	    }

		private void SetComponents() {
			bt_sair = (Button) findViewById(R.id.bt_sair);
			bt_sair.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO Avisar ao servidor que o jogador saiu
					Globals.cliente.FecharConexao();
					finish();
				}
			});			
			//-------- Início Ações por jogador: -----------//
			bt_act1 = (Button) findViewById(R.id.bt_act1);
			bt_act2 = (Button) findViewById(R.id.bt_act2);
			bt_act3 = (Button) findViewById(R.id.bt_act3);
			
			if(tipo == PLANTADOR){//Plantador de árvores
				bt_act1.setClickable(true);
				bt_act1.setText(R.string.reg);
				bt_act1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Ação regar
						acao = REGAR;						
					}
				});
				bt_act2.setText(R.string.cerc);
				bt_act2.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO Ação Construir Cerca		
						acao = CERCA;								
					}
				});
				bt_act3.setText(R.string.arv); 
				bt_act3.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO Ação Plantar
						acao = PLANTAR;
					}
				});
				
			}
			else if(tipo == LENHADOR){//Lenhador
				bt_act1.setClickable(false);
				bt_act1.setText("      ");
				bt_act2.setText(R.string.cort);
				bt_act2.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO Ação cortar		
						acao = CORTAR;
					}
				});
				bt_act3.setText(R.string.dest);
				bt_act3.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO Ação destruir		
						acao = DESTRUIR;
					}
				});
			}
			else{
				finish();
			}
			//--------- Fim Ações por jogador -----------//
			
			//--------- Início tabuleiro ----------//
			tabuleiro = new ImageView[5][5];
			int cont = 0;
			for(int i=0;i<5;i++){
				for(int j=0;j<5;j++){
					cont++;
					String name = "imageView"+cont;
					int ivID = getResources().getIdentifier(name,
						    "id", getPackageName());
					tabuleiro[i][j] = (ImageView) findViewById(ivID);
					tabuleiro[i][j].setTag(R.drawable.empty);
					
				}
			}
			//--------- Fim tabuleiro ----------//
			
			//--------- Início movimentações ----------//
			pos_atual = get_posicao();
			switch (tipo) {
			case PLANTADOR:
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.plant);				
				break;
			case LENHADOR:
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.lenh);				
				break;
			default:
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.empty);
				break;
			}
			ib_up = (ImageButton) findViewById(R.id.ib_up);
			ib_up.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					set_posicao(pos_atual[0]-1,pos_atual[1]);
				}
			});
			ib_right = (ImageButton) findViewById(R.id.ib_right);
			ib_right.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					set_posicao(pos_atual[0],pos_atual[1]+1);
				}
			});
			ib_down = (ImageButton) findViewById(R.id.ib_down);
			ib_down.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					set_posicao(pos_atual[0]+1,pos_atual[1]);
				}
			});
			ib_left = (ImageButton) findViewById(R.id.ib_left);
			ib_left.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					set_posicao(pos_atual[0],pos_atual[1]-1);
				}
			});
			
			//--------- Fim movimentações -----------//
		}
		
		


		private int[] get_posicao() {
			// TODO Inserir código para enviar requisição ao servidor
			//Código abaixo somente para testes
			int[] pos = new int[2];
			if(tipo==LENHADOR){
				pos[0] = 4;
				pos[1] = 4;
			}
			return pos;
		}

		private void set_posicao(int i, int j) {
			// TODO Inserir código para enviar requisição ao servidor
			//Código abaixo somente para testes
			int[] pos_futura = new int[2];
			pos_futura[0] = i;
			pos_futura[1] = j;
			//Tratar fronteiras
			if(i>4){
				pos_futura[0] = 4;
			}
			if(j>4){
				pos_futura[1] = 4;
			}
			if(i<0){
				pos_futura[0] = 0;
			}
			if(j<0){
				pos_futura[1] = 0;
			}
			int ImgPosFutura = (Integer) tabuleiro[pos_atual[0]][pos_atual[1]].getTag();
			switch (acao) {
			case CERCA:
				tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.fence);
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.fence);
				acao = 0;
				break;
			case CORTAR:
				tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.empty);
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.empty);	
				acao = 0;	
				break;
			case DESTRUIR:
				//TODO colocar um tempo
				tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.empty);
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.empty);
				acao = 0;				
				break;
			case PLANTAR:
				tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.tree);
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.tree);
				acao = 0;				
				break;
			case REGAR:
				tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.tree);
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.tree);
				acao = 0;				
				break;
			default:
				tabuleiro[pos_atual[0]][pos_atual[1]].setTag(ImgPosFutura);
				tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(ImgPosFutura);
				acao = 0;
				break;
			}
			switch (tipo) {
			case PLANTADOR:
				tabuleiro[pos_futura[0]][pos_futura[1]].setImageResource(R.drawable.plant);				
				break;
			case LENHADOR:
				tabuleiro[pos_futura[0]][pos_futura[1]].setImageResource(R.drawable.lenh);				
				break;
			default:
				tabuleiro[pos_futura[0]][pos_futura[1]].setImageResource(R.drawable.empty);
				break;
			}
			pos_atual = pos_futura;			
		}
}
