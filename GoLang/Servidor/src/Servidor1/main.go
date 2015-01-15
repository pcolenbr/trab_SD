package main

import (
	"bufio"
	"fmt"
	"log"
	"math/rand"
	"net"
	"os"
	"strconv"
	"strings"
	"time"
	//"encoding/binary"
)

const (
	CONN_HOST    = "192.168.0.140"
	CONN_PORT    = "3333"
	CONN_TYPE    = "tcp"
	VAZIO        = "0"
	ARVORE       = "10"
	CERCA        = "20"
	ARVORE_M     = "30"
	PLANTADOR    = "1"
	LENHADOR     = "2"
	MAXJOGADORES = 3
)

var (
	_tabuleiro        *Tabuleiro
	_listadejogadores *ListaJogadores
	_msg              string
)

type Jogador struct {
	id         int
	tipo       string
	pos_linha  string
	pos_coluna string
	conexao    net.Conn
}

func NovoJogador(ident int, tip string, p1 string, p2 string, conn net.Conn) *Jogador {
	jogador := &Jogador{
		id:         ident,
		tipo:       tip,
		pos_linha:  p1,
		pos_coluna: p2,
		conexao:    conn,
	}
	return jogador
}

type ListaJogadores struct {
	jogadores map[int]*Jogador
}

type Objeto struct {
	id         int
	tipo_obj   string
	tipo_jog   string
	//tempo de tempo do posicionamento?
}

func NovoObjetoVazio() *Objeto {
	objeto := &Objeto{
		id:         0,
		tipo_obj:   VAZIO,
		tipo_jog:   VAZIO,
	}

	return objeto
}

type Tabuleiro struct {
	objetos map[string]*Objeto
}

// Cria uma lista de jogadores
func NovaListaJogadores() *ListaJogadores {

	listajogadores := &ListaJogadores{
		jogadores: make(map[int]*Jogador),
	}

	return listajogadores
}

// Cria o tabuleiro com objetos vazios
func NovoTabuleiro() *Tabuleiro {
	tabuleiro := &Tabuleiro{
		objetos: make(map[string]*Objeto),
	}
	cont := 0
	for i := 0; i < 5; i++ {
		for j := 0; j < 5; j++ {
			cont++
			tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)] = NovoObjetoVazio()
		}
	}

	return tabuleiro
}

func ImprimirTabuleiro() {

	for i := 0; i < 5; i++ {
		for j := 0; j < 5; j++ {
			fmt.Println(_tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)])
		}
	}

}

func RetornarTabuleiro() string {

	retorno := "{\"objetos\" : ["

	for i := 0; i < 5; i++ {
		for j := 0; j < 5; j++ {
			retorno += "{"

						retorno += "\"id\" : " + strconv.Itoa(_tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)].id) + ","
						retorno += "\"tipoObj\" : " + _tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)].tipo_obj + ","
						retorno += "\"tipoJog\" : " + _tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)].tipo_jog
						

			if i == 4 && j == 4 {
				retorno += "}"
			} else {
				retorno += "},"
			}
		}
	}

	retorno += "]}"

	return retorno

}

func broadcast(dados []byte) {
	
	for i := 1; i <= len(_listadejogadores.jogadores); i++ {
		_,err := _listadejogadores.jogadores[i].conexao.Write(dados)
		if err != nil {
			//TODO Finalizar e remover a conexao/jogador da lista e do tabuleiro
			fmt.Println("Broadcast - Erro:", err.Error())
		}
	}
	
}

func InserirJogador(tipo string, conexao net.Conn) string {
	rand.Seed(time.Now().Unix())

	pos_coluna := strconv.Itoa(rand.Intn(4-0) + 0)
	pos_linha := strconv.Itoa(rand.Intn(4-0) + 0)

	for {

		if strings.EqualFold(_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo_obj, VAZIO) &&
			strings.EqualFold(_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo_jog, VAZIO) {
			break
		}

		pos_coluna = strconv.Itoa(rand.Intn(4-0) + 0)
		pos_linha = strconv.Itoa(rand.Intn(4-0) + 0)

	}

	fmt.Println("X: " + pos_linha)
	fmt.Println("Y: " + pos_coluna)

	id := len(_listadejogadores.jogadores) + 1
	jogador := NovoJogador(id, tipo, pos_linha, pos_coluna, conexao)
	_listadejogadores.jogadores[id] = jogador
	
	_tabuleiro.objetos[pos_linha+","+pos_coluna].id = id
	_tabuleiro.objetos[pos_linha+","+pos_coluna].tipo_jog = tipo
	fmt.Println("Inseriu o jogador")

	return "{\"id\":" + strconv.Itoa(id) + "}; {\"posicao\": \"" + pos_linha + "," + pos_coluna + "\"}"

}

func MoverJogador(id string, posAtual string, posDesejada string) string {

	ident, err := strconv.Atoi(id)
	if err != nil {
		fmt.Println("Erro:", err.Error())
		return posAtual
	}
	posAtual = strings.TrimSpace(posAtual)
	posicaoAtual := strings.Split(posAtual, ",")
	posicaoAtual[1] = strings.TrimSpace(posicaoAtual[1])
	posAtual = strings.TrimSpace(posDesejada)
	posicaoDesejada := strings.Split(posDesejada, ",")
	posicaoDesejada[1] = strings.TrimSpace(posicaoDesejada[1])
	
	
	if strings.EqualFold(_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].tipo_jog, VAZIO) {
		_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].tipo_jog = VAZIO
		_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].id = 0
		
		_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].tipo_jog = _listadejogadores.jogadores[ident].tipo
		_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].id = ident
		
		return "{\"posicao\": \"" + posDesejada + "\"}"
	}
	return "'posicao':" + posAtual

}

func Plantar(id string, pos string) string {

	fmt.Println(pos)
	pos = strings.TrimSpace(pos)
	
	fmt.Println(pos)
	
	linha := strings.Split(pos, ",")
	coluna := strings.Split(pos, ",")
	
	fmt.Println("Linha:" + linha[0])
	fmt.Println("Coluna:" + coluna[0])

	if strings.EqualFold(_tabuleiro.objetos[linha[0]+","+coluna[0]].tipo_obj, VAZIO) {
		_tabuleiro.objetos[linha[0]+","+coluna[0]].tipo_obj = ARVORE

		return "'plantar' : true"
	}

	return "'plantar' : false"

}

func Cortar(id string, pos string) string {

	linha := strings.Split(pos, ",")
	coluna := strings.Split(pos, ",")

	if strings.EqualFold(_tabuleiro.objetos[linha[0]+","+coluna[0]].tipo_obj, ARVORE) {
		_tabuleiro.objetos[linha[0]+","+coluna[0]].tipo_obj = VAZIO

		return "'cortar' : true"
	}

	return "'cortar' : false"
}

func removerJogador(id string, posAtual string) bool {
	
	ident, err := strconv.Atoi(id)
	if err != nil {
		fmt.Println("Erro:", err.Error())
		return false
	}
	
	linha := strings.Split(posAtual, ",")
	coluna := strings.Split(posAtual, ",")
	
	_tabuleiro.objetos[linha[0]+","+coluna[0]].tipo_jog = VAZIO
	
	delete(_listadejogadores.jogadores, ident)
	fmt.Println("Jogador removido da lista")
	
	return true
}

func main() {
	_tabuleiro = NovoTabuleiro()
	fmt.Println("Tabuleiro Criado")
	_listadejogadores = NovaListaJogadores()
	fmt.Println("Lista de Jogadores Criada")
	
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
			go func(net.Conn) {
				defer func() { <-sem }()
				go HandleRequest(conn)
			}(conn)
		}
		// Handle connections in a new goroutine.
		
	}
}


func HandleRequest(conn net.Conn) {
	
	buf := make([]byte, 1024)
	keep := true

	for (keep) {
		reqLen, err := conn.Read(buf)
		if err != nil {
			fmt.Println("Error reading buf:", err.Error())
			keep = false
		}

		if (reqLen > 0) {
			mensagem := string(buf[0:reqLen-1])

			fmt.Println(mensagem)
			cmd := strings.Split(mensagem, ":")

			fmt.Println(cmd[0])

			if strings.EqualFold(cmd[0], string("iniciarJogador")) {

				tipo := cmd[1]
			
				fmt.Println("Inserir jogador")
				id := InserirJogador(tipo, conn)
				tab := RetornarTabuleiro()
				
				b := []byte(id + ";" +tab)
	
				broadcast(b)
	
			}
		
			if strings.EqualFold(cmd[0], string("moverJogador")) {
	
				id := cmd[1]
				posAtual := cmd[2]
				posDesejada := strings.TrimSpace(cmd[3])
				
	
				fmt.Println("Mover jogador")
				pos := MoverJogador(id, posAtual, posDesejada)
				tab := RetornarTabuleiro()
	
				b :=[]byte(pos + ";" + tab)
	
				
				broadcast(b)
	
			} else if strings.EqualFold(cmd[0], string("plantar")) {
	
				id := cmd[1]
				pos := strings.TrimSpace(cmd[2])
	
				fmt.Println("Plantar")	
				plantar := Plantar(id, pos)
				tab := RetornarTabuleiro()
				b := []byte(plantar + ";" + tab)
		
				broadcast(b)
	
			} else if strings.EqualFold(cmd[0], string("cortar")) {
	
				id := cmd[1]
				pos := cmd[2]
	
				cortar := Cortar(id, pos)
				tab := RetornarTabuleiro()
	
				conn.Write([]byte(cortar + ";" + tab))
	
			} else if strings.EqualFold(cmd[0], string("sair")) {
				
				fmt.Println("Sair")
				id := cmd[1]
				posAtual := strings.TrimSpace(cmd[2])
				removerJogador(id, posAtual)
				keep = false;
			}
		}

		reader := bufio.NewReader(conn)
		msg, err := reader.ReadString('\n')
		if err != nil {
		}
	
		// Close the connection when you're done with it.
		cmd := strings.Split(msg, ":")
	
		if strings.EqualFold(cmd[0], string("moverJogador")) {
	
			id := cmd[1]
			posAtual := cmd[2]
			posDesejada := strings.TrimSpace(cmd[3])
				
	
			fmt.Println("Mover jogador")
			pos := MoverJogador(id, posAtual, posDesejada)
			tab := RetornarTabuleiro()
	
			b :=[]byte(pos + ";" + tab)
				
			broadcast(b)
	
		} else if strings.EqualFold(cmd[0], string("plantar")) {
	
			id := cmd[1]
			pos := strings.TrimSpace(cmd[2])
	
			fmt.Println("Plantar")
			plantar := Plantar(id, pos)
			tab := RetornarTabuleiro()
			b := []byte(plantar + ";" + tab)
	
			broadcast(b)
	
		} else if strings.EqualFold(cmd[0], string("cortar")) {
	
			id := cmd[1]
			pos := cmd[2]
	
			cortar := Cortar(id, pos)
			tab := RetornarTabuleiro()
	
			conn.Write([]byte(cortar + ";" + tab))
	
		} else if strings.EqualFold(cmd[0], string("sair")) {
				
			fmt.Println("Sair")
			id := cmd[1]
			posAtual := strings.TrimSpace(cmd[2])
			removerJogador(id, posAtual)
			keep = false;
		}
	}
}
