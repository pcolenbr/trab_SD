package com.example.desmatapp;

import org.json.JSONArray;
import org.json.JSONException;

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
		
		private ImageButton ib_up, ib_right, ib_down, ib_left;
		private int[] pos_atual;
		private int tipo,acao;
		// Tipos de jogadores
		private static final int PLANTADOR = 1;
		private static final int LENHADOR = 2;
		// Tipos de a��es
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
			Globals.cliente.setContext(GameActivity.this);
			bt_sair = (Button) findViewById(R.id.bt_sair);
			bt_sair.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO Avisar ao servidor que o jogador saiu
					Globals.cliente.FecharConexao();
					finish();
				}
			});			
			//-------- In�cio A��es por jogador: -----------//
			bt_act1 = (Button) findViewById(R.id.bt_act1);
			bt_act2 = (Button) findViewById(R.id.bt_act2);
			bt_act3 = (Button) findViewById(R.id.bt_act3);
			
			if(tipo == PLANTADOR){//Plantador de �rvores
				bt_act1.setClickable(true);
				bt_act1.setText(R.string.reg);
				bt_act1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO A��o regar
						acao = REGAR;						
					}
				});
				bt_act2.setText(R.string.cerc);
				bt_act2.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO A��o Construir Cerca		
						acao = CERCA;								
					}
				});
				bt_act3.setText(R.string.arv); 
				bt_act3.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO A��o Plantar
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
						// TODO A��o cortar		
						acao = CORTAR;
					}
				});
				bt_act3.setText(R.string.dest);
				bt_act3.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO A��o destruir		
						acao = DESTRUIR;
					}
				});
			}
			else{
				finish();
			}
			//--------- Fim A��es por jogador -----------//
			
			//--------- In�cio Globals.cliente.tabuleiro ----------//
			int cont = 0;
			for(int i=0;i<5;i++){
				for(int j=0;j<5;j++){
					cont++;
					String name = "imageView"+cont;
					int ivID = getResources().getIdentifier(name,
						    "id", getPackageName());
					Globals.cliente.tabuleiro[i][j] = (ImageView) findViewById(ivID);
					Globals.cliente.tabuleiro[i][j].setTag(R.drawable.empty);
					
				}
			}
			//--------- Fim Globals.cliente.tabuleiro ----------//
			
			//--------- In�cio movimenta��es ----------//
			pos_atual = get_posicao();
			switch (tipo) {
			case PLANTADOR:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.plant);				
				break;
			case LENHADOR:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.lenh);				
				break;
			default:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.empty);
				break;
			}
			ib_up = (ImageButton) findViewById(R.id.ib_up);
			ib_up.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					String[] s = (Globals.cliente.MoverJogador(Globals.cliente.id, pos_atual[0]+","+pos_atual[1], (pos_atual[0]-1)+","+pos_atual[1])).split(",");					
					set_posicao(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
				}
			});
			ib_right = (ImageButton) findViewById(R.id.ib_right);
			ib_right.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					String[] s = (Globals.cliente.MoverJogador(Globals.cliente.id, pos_atual[0]+","+pos_atual[1], pos_atual[0]+","+(pos_atual[1]+1))).split(",");					
					set_posicao(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
				}
			});
			ib_down = (ImageButton) findViewById(R.id.ib_down);
			ib_down.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					String[] s = (Globals.cliente.MoverJogador(Globals.cliente.id, pos_atual[0]+","+pos_atual[1], (pos_atual[0]+1)+","+pos_atual[1])).split(",");
					set_posicao(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
				}
			});
			ib_left = (ImageButton) findViewById(R.id.ib_left);
			ib_left.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					String[] s = (Globals.cliente.MoverJogador(Globals.cliente.id, pos_atual[0]+","+pos_atual[1], pos_atual[0]+","+(pos_atual[1]-1))).split(",");					
					set_posicao(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
				}
			});
			
			//--------- Fim movimenta��es -----------//
		}
		
		


		private int[] get_posicao() {
			// TODO Inserir c�digo para enviar requisi��o ao servidor
			//C�digo abaixo somente para testes
			int[] pos = new int[2];
			if(tipo==LENHADOR){
				pos[0] = 4;
				pos[1] = 4;
			}
			return pos;
		}

		private void set_posicao(int i, int j) {
			// TODO Inserir c�digo para enviar requisi��o ao servidor
			//C�digo abaixo somente para testes
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
			int ImgPosFutura = (Integer) Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].getTag();
			switch (acao) {
			case CERCA:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.fence);
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.fence);
				acao = 0;
				break;
			case CORTAR:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.empty);
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.empty);	
				acao = 0;	
				break;
			case DESTRUIR:
				//TODO colocar um tempo
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.empty);
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.empty);
				acao = 0;				
				break;
			case PLANTAR:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.tree);
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.tree);
				acao = 0;				
				break;
			case REGAR:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setTag(R.drawable.tree);
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(R.drawable.tree);
				acao = 0;				
				break;
			default:
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setTag(ImgPosFutura);
				Globals.cliente.tabuleiro[pos_atual[0]][pos_atual[1]].setImageResource(ImgPosFutura);
				acao = 0;
				break;
			}
			switch (tipo) {
			case PLANTADOR:
				Globals.cliente.tabuleiro[pos_futura[0]][pos_futura[1]].setImageResource(R.drawable.plant);				
				break;
			case LENHADOR:
				Globals.cliente.tabuleiro[pos_futura[0]][pos_futura[1]].setImageResource(R.drawable.lenh);				
				break;
			default:
				Globals.cliente.tabuleiro[pos_futura[0]][pos_futura[1]].setImageResource(R.drawable.empty);
				break;
			}
			pos_atual = pos_futura;			
		}
		
		public void redesenharTabuleiro(int acao, int tipo, int x, int y){
			switch (acao) {
				case CERCA:
					Globals.cliente.tabuleiro[x][y].setTag(R.drawable.fence);
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.fence);
					acao = 0;
					break;
				case CORTAR:
					Globals.cliente.tabuleiro[x][y].setTag(R.drawable.empty);
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.empty);	
					acao = 0;	
					break;
				case DESTRUIR:
					//TODO colocar um tempo
					Globals.cliente.tabuleiro[x][y].setTag(R.drawable.empty);
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.empty);
					acao = 0;				
					break;
				case PLANTAR:
					Globals.cliente.tabuleiro[x][y].setTag(R.drawable.tree);
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.tree);
					acao = 0;				
					break;
				case REGAR:
					Globals.cliente.tabuleiro[x][y].setTag(R.drawable.tree);
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.tree);
					acao = 0;				
					break;
				default:
					acao = 0;
					break;
			}
			switch (tipo) {
				case PLANTADOR:
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.plant);				
					break;
				case LENHADOR:
					Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.lenh);				
					break;
				default:
					break;
			}
		}
		
		public void desenharTabuleiro(JSONArray dados) throws JSONException {
			int count = 0;
			
			int obj = -1;
			int jog = -1;
		
			for (int x = 0; x < 5; x++) {
				for (int y = 0; y < 5; y++) { 
					
					obj = dados.getJSONObject(count).getInt("tipoObj");
					jog = dados.getJSONObject(count).getInt("tipoJog");
					
					switch (obj) {
						case CERCA:
							Globals.cliente.tabuleiro[x][y].setTag(R.drawable.fence);
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.fence);
							break;
						case CORTAR:
							Globals.cliente.tabuleiro[x][y].setTag(R.drawable.empty);
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.empty);	
							break;
						case DESTRUIR:
							//TODO colocar um tempo
							Globals.cliente.tabuleiro[x][y].setTag(R.drawable.empty);
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.empty);
							break;
						case PLANTAR:
							Globals.cliente.tabuleiro[x][y].setTag(R.drawable.tree);
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.tree);
							break;
						case REGAR:
							Globals.cliente.tabuleiro[x][y].setTag(R.drawable.tree);
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.tree);
							break;
						default:
							break;
					}
					
					switch (jog) {
						case PLANTADOR:
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.plant);				
							break;
						case LENHADOR:
							Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.lenh);				
							break;
						default:
							break;
					}
					
					count++;
					
				}
			}				
			
		}
}
