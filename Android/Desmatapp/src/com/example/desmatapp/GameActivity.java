package com.example.desmatapp;


import org.json.JSONArray;
import org.json.JSONException;

import com.example.desmatapp.util.Globals;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity {
		private Button bt_act1, bt_act2,bt_act3, bt_sair;
		private TextView tv_pts;
		
		private ImageButton ib_up, ib_right, ib_down, ib_left;
		//private int[] Globals.cliente.pos_atual;
		private int tipo,pontuacao;
		// Tipos de jogadores
		private static final int PLANTADOR = 1;
		private static final int LENHADOR = 2;
		// Tipos de ações
		private static final int PLANTAR = 10;
		private static final int CERCA = 20;
		private static final int MORRENDO = 30;
		private static final int REGAR = 40;
		private static final int CORTAR = 50;
		private static final int DESTRUIR = 60;
		// Tempos do jogo
		private static final int ARVORE_DESIDRATADA = 5000;
		
		
		
		

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
	        EnableButtons(false);
			new Thread(Globals.cliente).start();
	    }

		

		private void SetComponents() {
			Globals.cliente.setContext(GameActivity.this);
			tv_pts = (TextView) findViewById(R.id.tv_pts);
			bt_sair = (Button) findViewById(R.id.bt_sair);
			bt_sair.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
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
						bt_act3.callOnClick();
					}
				});
				bt_act2.setText(R.string.cerc);
				bt_act2.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						Globals.cliente.Cerca( Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1] );								
					}
				});
				bt_act3.setText(R.string.arv); 
				bt_act3.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						final String pos = Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1];
						Globals.cliente.Plantar( pos );
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
						    public void run() {
						    	Globals.cliente.ArvoreMorrendo( pos );
						    	Handler handler2 = new Handler();
								handler2.postDelayed(new Runnable() {
								    public void run() {
								    	Globals.cliente.Cortar( pos );
								    }
								}, ARVORE_DESIDRATADA);
						    }
						}, ARVORE_DESIDRATADA);
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
						Globals.cliente.Cortar( Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1] );
					}
				});
				bt_act3.setText(R.string.dest);
				bt_act3.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						EnableButtons(false);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
						    public void run() {
						    	Globals.cliente.Destruir( Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1] );
						    }
						}, 1000);						
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
						Globals.cliente.MoverJogador(Globals.cliente.pos_atual[0] + "," + Globals.cliente.pos_atual[1], Globals.cliente.pos_atual[0]-1 + "," + Globals.cliente.pos_atual[1]);
					
				}
			});
			ib_right = (ImageButton) findViewById(R.id.ib_right);
			ib_right.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[1]<=3)
						Globals.cliente.MoverJogador(Globals.cliente.pos_atual[0]+","+Globals.cliente.pos_atual[1], Globals.cliente.pos_atual[0]+","+(Globals.cliente.pos_atual[1]+1));
				}
			});
			ib_down = (ImageButton) findViewById(R.id.ib_down);
			ib_down.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[0]<=3)
						Globals.cliente.MoverJogador(Globals.cliente.pos_atual[0]+","+Globals.cliente.pos_atual[1], (Globals.cliente.pos_atual[0]+1)+","+Globals.cliente.pos_atual[1]);
				}
			});
			ib_left = (ImageButton) findViewById(R.id.ib_left);
			ib_left.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(Globals.cliente.pos_atual[1]>=1)
						Globals.cliente.MoverJogador(Globals.cliente.pos_atual[0]+","+Globals.cliente.pos_atual[1], Globals.cliente.pos_atual[0]+","+(Globals.cliente.pos_atual[1]-1));
					
				}
			});
			
			//--------- Fim movimentações -----------//
		}
		
		
		private void EnableButtons(boolean botoes) {
			bt_act1.setEnabled(botoes);
			bt_act2.setEnabled(botoes);
			bt_act3.setEnabled(botoes);
			ib_down.setEnabled(botoes);
			ib_left.setEnabled(botoes);
			ib_right.setEnabled(botoes);
			ib_up.setEnabled(botoes);
			
			if(botoes){
				((ProgressBar) findViewById(R.id.pb_action)).setVisibility(View.INVISIBLE);
			}
			else{
				((ProgressBar) findViewById(R.id.pb_action)).setVisibility(View.VISIBLE);
			}
			
			
		}

		public void desenharTabuleiro(final JSONArray dados) throws JSONException {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					((RelativeLayout)findViewById(R.id.rl_loading)).setVisibility(View.INVISIBLE);
					EnableButtons(true);
					int count = 0;					
					int obj = 0;
					int jog = 0;
					int id = 0;
					int pt = 0;
					for (int x = 0; x < 5; x++) {
						for (int y = 0; y < 5; y++) { 
							
							try {
								obj = dados.getJSONObject(count).getInt("tipoObj");
								jog = dados.getJSONObject(count).getInt("tipoJog");
								id = dados.getJSONObject(count).getInt("id");
								pt = dados.getJSONObject(count).getInt("pontuacao");
							} catch (JSONException e) {
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
								case MORRENDO:
									Globals.cliente.tabuleiro[x][y].setTag(R.drawable.dtree);
									Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.dtree);
									break;
								default:
									Globals.cliente.tabuleiro[x][y].setTag(R.drawable.empty);
									Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.empty);
									break;
							}
							
							switch (jog) {
								case PLANTADOR:
									if(id == Globals.cliente.id){
										Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.plant);
										Globals.cliente.pos_atual[0] = x;
										Globals.cliente.pos_atual[1] = y;
										pontuacao = pt;
										tv_pts.setText(""+pontuacao);
									}
									else
										Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.plant_outro);
									break;
								case LENHADOR:
									if(id == Globals.cliente.id){
										Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.lenh);
										Globals.cliente.pos_atual[0] = x;
										Globals.cliente.pos_atual[1] = y;	
										pontuacao = pt;
										tv_pts.setText(""+pontuacao);
									}
									else
										Globals.cliente.tabuleiro[x][y].setImageResource(R.drawable.lenh_outro);
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
