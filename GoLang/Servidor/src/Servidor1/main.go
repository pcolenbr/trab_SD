package main

import (
    "fmt"
    "net"
    "os"
    "log"
    "bufio"
	"strings"
	//"bytes"
	//"encoding/binary"
)

const (
    CONN_HOST = "192.168.56.101"
    CONN_PORT = "3333"
    CONN_TYPE = "tcp"
    VAZIO 	  = "0"
    ARVORE 	  = "10"
    CERCA	  = "20"
    ARVORE_M  = "30"
    PLANTADOR = "1"
    LENHADOR  = "2"
    MAXJOGADORES = 3
)

var (
	_tabuleiro *Tabuleiro
	_listadejogadores *ListaJogadores
	_msg string
)

type Jogador struct{
	id int
	tipo string
	pos_linha string
	pos_coluna string
	conexao net.Conn
	}

func NovoJogador(ident int, tip string, p1 string, p2 string, conn net.Conn) *Jogador{
	jogador:= &Jogador{
		id: ident,
		tipo: tip,
		pos_linha: p1,
		pos_coluna: p2,
		conexao: conn,
	}
	return jogador
}

type ListaJogadores struct{
	jogadores []*Jogador
	}

type Objeto struct{
	id int
	tipo string
	pos_linha string
	pos_coluna string
	//tempo de tempo do posicionamento?	
	}

func NovoObjeto(ident int, p1 string, p2 string) *Objeto{
	objeto:= &Objeto{
		id: ident,
		tipo: VAZIO,
		pos_linha: p1,
		pos_coluna: p2,
	}
	
	return objeto
}
type Tabuleiro struct{
	objetos map[string]*Objeto
}

// Cria uma lista de jogadores
func NovaListaJogadores() *ListaJogadores{
	listajogadores := &ListaJogadores{
		jogadores: make([]*Jogador,0),
		}
	return listajogadores
}

// Cria o tabuleiro com objetos vazios
func NovoTabuleiro() *Tabuleiro{
	tabuleiro := &Tabuleiro{
		objetos: make(map[string]*Objeto),
	}
	cont:=0
	for i:=0; i<5; i++{
		for j:=0; j<5; j++{
			cont++
			tabuleiro.objetos[string(i)+","+string(j)] = NovoObjeto(cont,string(i),string(j))
		}
	}
	return tabuleiro
}


func InserirJogador(tipo string,pos_linha string,pos_coluna string,conexao net.Conn ){
	fmt.Println("Preparando insercao na linha "+pos_linha+" e coluna "+pos_coluna)
	id := len(_listadejogadores.jogadores) + 1
	jogador := NovoJogador(id, tipo, pos_linha, pos_coluna, conexao)
	_listadejogadores.jogadores = append(_listadejogadores.jogadores,jogador)
	if strings.EqualFold(tipo,PLANTADOR){
		if (_tabuleiro.objetos[pos_linha+","+pos_coluna] != nil){
			if strings.EqualFold(_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo,PLANTADOR) || 
				strings.EqualFold(_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo,LENHADOR){
					_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo = PLANTADOR
					fmt.Println("Plantador inserido na linha "+pos_linha+" e coluna "+pos_coluna)
				}
		}
	}
	
}



func main() {
	fmt.Println("Criando o tabuleiro")
	_tabuleiro = NovoTabuleiro()
	fmt.Println("Tabuleiro Criado")
	fmt.Println("Criando uma lista de jogadores")
	_listadejogadores = NovaListaJogadores()
	fmt.Println("Lista Criada")
    // Escutar novas conexões.
    l, err := net.Listen(CONN_TYPE, CONN_HOST+":"+CONN_PORT)
    if err != nil {
        fmt.Println("Erro escutando:", err.Error())
        os.Exit(1)
    }
    defer l.Close()
    fmt.Println("Escutando o servidor " + CONN_HOST + ":" + CONN_PORT)
	sem := make(chan bool, MAXJOGADORES)
    for {
        conn, err := l.Accept()
        if err != nil {
            fmt.Println("Erro aceitando a conexao: ", err.Error())
            os.Exit(1)
        } else {
				log.Printf("Nova conexao estabelecida")
				sem <- true
				go func (net.Conn) {
				defer func() { <-sem } ()
        			go handleRequest(conn)
				}(conn)
			}
        // Handle connections in a new goroutine.
    }
}

func fecharConexao(conn net.Conn){
    fmt.Println("Jogador desconectado")
	
  	conn.Close()
}

// Handles incoming requests.
func handleRequest(conn net.Conn) {
  // Make a buffer to hold incoming data.
  buf := make([]byte, 1024)
  // Read the incoming connection into the buffer.
  
  
  // Send a response back to person contacting us.
  conn.Write([]byte("Message received."))
  reqLen, err := conn.Read(buf)
  if err != nil {
    fmt.Println("Error reading buf:", err.Error())
  }
  if reqLen>0{
  	mensagem := string(buf[:])
  	fmt.Println(mensagem)
	fmt.Println(string(PLANTADOR))
  	if strings.EqualFold(mensagem,string(PLANTADOR)){
  		fmt.Println("Inserir jogador")
   		InserirJogador(PLANTADOR,"0","0",conn)
  	
  	}
    }
  reader := bufio.NewReader(conn)
  msg, err := reader.ReadString(' ')
  if err != nil {
  }
  
  // Close the connection when you're done with it.
  if strings.EqualFold(msg,"sair"){
  	fecharConexao(conn)
  	}else{
  	msg = ""
  	}
}
