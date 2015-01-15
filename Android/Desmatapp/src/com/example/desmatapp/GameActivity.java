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
import android.widget.RelativeLayout;

public class GameActivity extends Activity {
		private Button bt_act1, bt_act2,bt_act3, bt_sair;
		
		private ImageButton ib_up, ib_right, ib_down, ib_left;
		//private int[] Globals.cliente.pos_atual;
		private int tipo,acao;

		private JSONArray ultimo_tabuleiro;
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
						Globals.cliente.Plantar(Globals.cliente.id, Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1]);
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
			
			//--------- Início Globals.cliente.tabuleiro ----------//
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
			
			//--------- Início movimentações ----------//
			//Globals.cliente.pos_atual = get_posicao();
			/*switch (tipo) {
			case PLANTADOR:
				Globals.cliente.tabuleiro[Globals.cliente.pos_atual[0]][Globals.cliente.pos_atual[1]].setImageResource(R.drawable.plant);				
				break;
			case LENHADOR:
				Globals.cliente.tabuleiro[Globals.cliente.pos_atual[0]][Globals.cliente.pos_atual[1]].setImageResource(R.drawable.lenh);				
				break;
			default:
				Globals.cliente.tabuleiro[Globals.cliente.pos_atual[0]][Globals.cliente.pos_atual[1]].setImageResource(R.drawable.empty);
				break;
			}*/
			ib_up = (ImageButton) findViewById(R.id.ib_up);
			ib_up.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[0]>=1)
						Globals.cliente.MoverJogador(Globals.cliente.id,Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1], Globals.cliente.pos_atual[0]-1 + "," + Globals.cliente.pos_atual[1]);
					
				}
			});
			ib_right = (ImageButton) findViewById(R.id.ib_right);
			ib_right.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[1]<=3)
						Globals.cliente.MoverJogador(Globals.cliente.id, Globals.cliente.pos_atual[0]+","+Globals.cliente.pos_atual[1], Globals.cliente.pos_atual[0]+","+(Globals.cliente.pos_atual[1]+1));
				}
			});
			ib_down = (ImageButton) findViewById(R.id.ib_down);
			ib_down.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[0]<=3)
						Globals.cliente.MoverJogador(Globals.cliente.id, Globals.cliente.pos_atual[0]+","+Globals.cliente.pos_atual[1], (Globals.cliente.pos_atual[0]+1)+","+Globals.cliente.pos_atual[1]);
				}
			});
			ib_left = (ImageButton) findViewById(R.id.ib_left);
			ib_left.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[1]>=1)
						Globals.cliente.MoverJogador(Globals.cliente.id, Globals.cliente.pos_atual[0]+","+Globals.cliente.pos_atual[1], Globals.cliente.pos_atual[0]+","+(Globals.cliente.pos_atual[1]-1));
					
				}
			});
			
			//--------- Fim movimentações -----------//
		}
		
		


		/*private int[] get_posicao() {
			// TODO Inserir código para enviar requisição ao servidor
			//Código abaixo somente para testes
			return Globals.cliente.pos_atual;
		}*/

				
		public void desenharTabuleiro(final JSONArray dados) throws JSONException {
			this.ultimo_tabuleiro = dados;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					((RelativeLayout)findViewById(R.id.rl_loading)).setVisibility(View.INVISIBLE);
					int count = 0;					
					int obj = 0;
					int jog = 0;
					for (int x = 0; x < 5; x++) {
						for (int y = 0; y < 5; y++) { 
							
							try {
								obj = dados.getJSONObject(count).getInt("tipoObj");
								jog = dados.getJSONObject(count).getInt("tipoJog");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
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
									Globals.cliente.tabuleiro[x][y].setTag(R.drawable.empty);
									Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.empty);
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
			});
						
			
		}
}
