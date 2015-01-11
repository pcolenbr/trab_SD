package com.example.desmatapp.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ClienteTCP implements Runnable{
	private String ip;
	private int porta;
	//private EventListener listener;
	private Context context;
	private Socket sock;
	private int tipo;

	public ClienteTCP(Context context, String ip, int porta, int tipo) {

		this.ip = ip;
		this.porta = porta;
		this.context = context;
		this.sock = null;
		this.tipo = tipo;
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
				final byte[] data = new byte[6556];
				try {
					InputStream is = sock.getInputStream();
					int size = is.read(data);
					if(size>0){
						((Activity) context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(context, "Mensagem do servidor: "+ data.toString(), Toast.LENGTH_SHORT).show();
							}
						});
						
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		} catch (UnknownHostException e) {
			Log.e("ERROR", e.toString());
		} catch (IOException e) {
			Log.e("ERROR", e.toString());
		}
		
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
