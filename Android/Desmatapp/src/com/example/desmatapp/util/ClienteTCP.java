package com.example.desmatapp.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.desmatapp.GameActivity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ClienteTCP implements Runnable {
	private String ip;
	private int porta;
	//private EventListener listener;
	private Context context;
	private Socket sock;
	private int tipo;
	public int id;
	public ImageView[][] tabuleiro;

	public ClienteTCP(String ip, int porta, int tipo) {

		this.ip = ip;
		this.porta = porta;
		this.sock = null;
		this.tipo = tipo;
		this.tabuleiro = new ImageView[5][5];
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
				final byte[] data = new byte[2048];
				try {
					InputStream is = sock.getInputStream();
					int size = is.read(data);
					if(size>0){
						final String s = new String(data);
						String[] res = s.split(";");
						
						for (int i = 0; i< res.length; i++){
							JSONObject job = new JSONObject(res[i]);
							
							if (job.has("id")){
								this.id = (Integer) job.get("id");									
							} else if(job.has("objetos")){
								JSONArray ja = job.getJSONArray("objetos");
								((GameActivity) ((Activity)context)).desenharTabuleiro(ja);
							}							
						}
//						if (res[0].con("id")){
//							this.id = Integer.parseInt(res[1]); 
//						}
//						if (res[1])
						
						
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
		} catch (UnknownHostException e) {
			Log.e("ERROR", e.toString());
		} catch (IOException e) {
			Log.e("ERROR", e.toString());
		}
		
	}
	
	public String MoverJogador(int id, String var_linha, String var_coluna){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.writeBytes("moverJogador:" + id + ":" + var_linha+ ":" + var_coluna + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final byte[] data = new byte[2048];
		try {
			InputStream is = sock.getInputStream();
			int size = is.read(data);
			if(size>0){
				return new String(data);			
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}
	
	public void FecharConexao(){
		try {
			DataOutputStream os = new DataOutputStream(sock.getOutputStream());
			os.writeBytes("sair");
			os.flush();
			sock.close();
		} catch (IOException e) {
			Log.e("ERROR", e.toString());
		}
		if(sock.isClosed()){		
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(context, "Conexão com o servidor encerrada", Toast.LENGTH_SHORT).show();
				}
			});
			
		}
	}
	
	/*public void setListener(EventListener eventListener){
		this.listener=eventListener;
	}*/
	
	

}
