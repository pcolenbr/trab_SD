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
	public int[] pos_atual;
	public int id;
	public ImageView[][] tabuleiro;

	public ClienteTCP(String ip, int porta, int tipo) {

		this.ip = ip;
		this.porta = porta;
		this.sock = null;
		this.tipo = tipo;
		this.tabuleiro = new ImageView[5][5];
		this.pos_atual = new int[2];
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
				while(!sock.isClosed()){	
				if(sock.isConnected()){
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
								}else if(job.has("posicao")){
									String st = (String) job.get("posicao");
									String[] pos = st.split(",");
									pos_atual[0] = Integer.parseInt(pos[0]);
									pos_atual[1] = Integer.parseInt(pos[1].replaceAll("\\n", ""));							
								} 
								else if(job.has("objetos")){
									JSONArray ja = job.getJSONArray("objetos");
									((GameActivity) ((Activity)context)).desenharTabuleiro(ja);
								}							
							}						
						}
					}catch (Exception e) {
						((Activity) context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								((Activity) context).finish();
								FecharConexao();
							}
						});
						e.printStackTrace();
					}
				} 
				}
				
			}
		} catch (UnknownHostException e) {
			Log.e("ERROR", e.toString());
		} catch (IOException e) {
			Log.e("ERROR", e.toString());
		}
		
	}
	
	public void MoverJogador(int id, String var_linha, String var_coluna){
		DataOutputStream os;
		try {
			os = new DataOutputStream(sock.getOutputStream());
			os.flush();
			os.writeBytes("moverJogador:" + id + ":" + var_linha+ ":" + var_coluna+"\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void FecharConexao(){
		try {
			DataOutputStream os = new DataOutputStream(sock.getOutputStream());
			os.writeBytes("sair:"+id);
			os.flush();

			sock.setKeepAlive(false);
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
