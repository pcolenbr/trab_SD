package com.example.desmatapp.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.desmatapp.GameActivity;

public class ClienteTCP implements Runnable {
	private String ip;
	private int porta;
	//private EventListener listener;
	private Context context;
	private Socket sock;
	private int tipo;
	private int inicio;
	private Calendar c;
	public int[] pos_atual;
	public int id;
	public ImageView[][] tabuleiro;
	
	public boolean startGame;

	public ClienteTCP(String ip, int porta, int tipo) {

		this.ip = ip;
		this.porta = porta;
		this.sock = null;
		this.tipo = tipo;
		this.tabuleiro = new ImageView[5][5];
		this.pos_atual = new int[2];
		this.startGame = false;
		
	}
	
	public void setContext(Context context){

		this.context = context;
	}
	public Context getContext(){
		return this.context;
	}
	

	@Override
	public void run() {
		try {
			sock = new Socket(ip, porta);
			if(sock.isConnected()){		
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(context, "Conectado com o servidor.", Toast.LENGTH_SHORT).show();
					}
				});
				 
				DataOutputStream os = new DataOutputStream(sock.getOutputStream());
				os.writeBytes("iniciarJogador:" + tipo+"\n");				
				os.flush();
				c = Calendar.getInstance(); 
		        inicio = c.get(Calendar.SECOND);
				while ( !sock.isClosed() ) {
					if(sock.isConnected()) {
						final byte[] data = new byte[2048];
						InputStream is = sock.getInputStream();
						int size = is.read(data);
						if ( size > 0 ) {
							final String s = new String(data);
							String[] res = s.split(";");
							
							for (int i = 0; i< res.length; i++){
								JSONObject job = new JSONObject(res[i]);
								
								if ( job.has("id") ) {
									if(this.id == 0) {
										this.id = job.getInt("id");
									}
								}  else if ( job.has("objetos") ) {
									
									JSONArray ja = job.getJSONArray("objetos");
									((GameActivity) ((Activity)context)).desenharTabuleiro(ja);
									
								}  else if ( job.has("startGame") ) {
									this.startGame = job.getBoolean("startGame");
									((GameActivity) ((Activity)context)).iniciarJogo(this.startGame);;
								} else if ( job.has("salaCheia") ) {
									if (job.getBoolean("salaCheia")) {
										((GameActivity) ((Activity)context)).salaCheia();
									}
								}
								
							}						
						}
					
					}
					if(id==0){
						if(c.get(Calendar.SECOND) == (inicio+10)){
							Globals.cliente.PedirTabuleiro();
							inicio = c.get(Calendar.SECOND);
						}
					}
				}				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void PedirTabuleiro(){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("tabuleiro:" + id + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void MoverJogador(String posAtual, String posDesejada){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("moverJogador:" + id + ":" + posAtual+ ":" + posDesejada + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Plantar(String pos){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("plantar:" + id + ":" + pos + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Cortar(String pos){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("cortar:" + id + ":" + pos + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Cerca(String pos){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("cerca:" + id + ":" + pos + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Destruir(String pos){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("destruir:" + id + ":" + pos + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ArvoreMorrendo(String pos) {
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("morrendo:" + id + ":" + pos + "\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void FecharConexao(int op){
		if(op == 1) {
			try {
				DataOutputStream os = new DataOutputStream(sock.getOutputStream());
				os.writeBytes("sair:" + id + "\n");
				os.flush();
				
				sock.setKeepAlive(false);
				sock.close();
				
				if ( sock.isClosed() ) {
					((Activity) context).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(context, "Conexão com o servidor encerrada", Toast.LENGTH_SHORT).show();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (op == 2) {
			try {
				sock.setKeepAlive(false);
				sock.close();
				
				if ( sock.isClosed() ) {
					((Activity) context).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(context, "Sala Cheia", Toast.LENGTH_SHORT).show();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}

		
	

}
