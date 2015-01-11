package main

import (
    "fmt"
    "net"
    "os"
    "log"
    "bufio"
	"strings"
	"strconv"
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
			tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)] = NovoObjeto(cont,strconv.Itoa(i),strconv.Itoa(j))
		}
	}
	return tabuleiro
}


func InserirJogador(tipo string,pos_linha string,pos_coluna string,conexao net.Conn ){
	fmt.Println("Preparando insercao na linha "+pos_linha+" e coluna "+pos_coluna)
	id := len(_listadejogadores.jogadores) + 1
	jogador := NovoJogador(id, tipo, pos_linha, pos_coluna, conexao)
	fmt.Println("Criou o jogador")
	_listadejogadores.jogadores = append(_listadejogadores.jogadores,jogador)
	fmt.Println("Inseriu o jogador")
	if strings.EqualFold(tipo,PLANTADOR){
		fmt.Println("Tipo Plantador")
		fmt.Println(_tabuleiro.objetos[pos_linha+","+pos_coluna])
		if (_tabuleiro.objetos[pos_linha+","+pos_coluna] != nil){
			fmt.Println("Encontrou o objeto")
			if strings.EqualFold(_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo,PLANTADOR) || 
				strings.EqualFold(_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo,LENHADOR){
					linha,err := strconv.Atoi(pos_linha)
					if err != nil {
			            fmt.Println("Erro convertendo a linha: ", err.Error())
			        }
					coluna,err := strconv.Atoi(pos_coluna)
					if err != nil {
			            fmt.Println("Erro convertendo a coluna: ", err.Error())
			        }
					if(coluna == 4){
						if(linha >= 0 && linha <4){
							linha = linha+1
						}else{
							linha = linha-1	
						}
					}else{
						coluna = coluna+1
					}
					pos_linha = strconv.Itoa(linha)
					pos_coluna = strconv.Itoa(coluna)
					InserirJogador(tipo,pos_linha,pos_coluna,conexao)
				}else{
					fmt.Println("Posicao no tabuleiro esta vazia")
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
  	mensagem := string(buf[0:1])
  	if (strings.EqualFold(mensagem,string(PLANTADOR))){
  		fmt.Println("Inserir jogador")
   		InserirJogador(PLANTADOR,strconv.Itoa(0),strconv.Itoa(0),conn)
   		fmt.Print(_tabuleiro.objetos["0,0"])
  	}else{
  		if(strings.EqualFold(mensagem,string(LENHADOR))){
	  		fmt.Println("Inserir jogador")
	   		InserirJogador(LENHADOR,strconv.Itoa(4),strconv.Itoa(4),conn)
	   		fmt.Print(_tabuleiro.objetos["4,4"])
	  	} 
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
