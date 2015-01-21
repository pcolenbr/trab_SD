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
	CONN_HOST    = "172.16.253.88"
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
	pontuacao  int
	tipo_obj   string
	tipo_jog   string
	//tempo de tempo do posicionamento?
}

func NovoObjetoVazio() *Objeto {
	objeto := &Objeto{
		id:         0,
		pontuacao:  0,
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
						retorno += "\"tipoJog\" : " + _tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)].tipo_jog + ","
						retorno += "\"pontuacao\" : " + strconv.Itoa(_tabuleiro.objetos[strconv.Itoa(i)+","+strconv.Itoa(j)].pontuacao)
						

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
		for _,jog := range (_listadejogadores.jogadores){
		_,err := jog.conexao.Write(dados)
		if err != nil {
			//TODO Finalizar e remover a conexao/jogador da lista e do tabuleiro
			fmt.Println("Erro:", err.Error())
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
	posDesejada = strings.TrimSpace(posDesejada)
	posicaoDesejada := strings.Split(posDesejada, ",")
	posicaoDesejada[1] = strings.TrimSpace(posicaoDesejada[1])
	
	if(_listadejogadores.jogadores[ident].tipo == LENHADOR){
		if strings.EqualFold(_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].tipo_jog, VAZIO) &&
		!strings.EqualFold(_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].tipo_obj, CERCA) {
			_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].tipo_jog = VAZIO
			_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].id = 0
			
			_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].tipo_jog = _listadejogadores.jogadores[ident].tipo
			_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].id = ident
			_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].pontuacao = _tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].pontuacao
			_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].pontuacao = 0
			
			_listadejogadores.jogadores[ident].pos_linha = posicaoDesejada[0]
			_listadejogadores.jogadores[ident].pos_coluna = posicaoDesejada[1]
			
			return "{\"posicao\": \"" + posDesejada + "\"}"
		}
	} else {
			if strings.EqualFold(_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].tipo_jog, VAZIO) {
				_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].tipo_jog = VAZIO
				_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].id = 0
				
				_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].tipo_jog = _listadejogadores.jogadores[ident].tipo
				_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].id = ident
				_tabuleiro.objetos[posicaoDesejada[0]+","+posicaoDesejada[1]].pontuacao = _tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].pontuacao
				_tabuleiro.objetos[posicaoAtual[0]+","+posicaoAtual[1]].pontuacao = 0
				
				_listadejogadores.jogadores[ident].pos_linha = posicaoDesejada[0]
				_listadejogadores.jogadores[ident].pos_coluna = posicaoDesejada[1]
				
				return "{\"posicao\": \"" + posDesejada + "\"}"
		}
		
	}
	return "'posicao':" + posAtual

}

func Plantar(id string, pos string) bool {
	
	pos = strings.TrimSpace(pos)
	posicao := strings.Split(pos, ",")
	

	if strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, VAZIO) ||
	strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, ARVORE) ||
	strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, ARVORE_M) {
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj = ARVORE
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].pontuacao += 5
		return true
	}

	return false

}

func Cortar (id string, pos string) bool {
	ident, err := strconv.Atoi(id)
	if err != nil {
		fmt.Println("Erro:", err.Error())
		return false
	}
	pos = strings.TrimSpace(pos)

	posicao := strings.Split(pos, ",")
	

	if strings.EqualFold(_listadejogadores.jogadores[ident].tipo,LENHADOR) && 
	(strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, ARVORE) ||
	strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, ARVORE_M)) {
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj = VAZIO
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].pontuacao += 5

		return true
	} else if strings.EqualFold(_listadejogadores.jogadores[ident].tipo, PLANTADOR) && 
		   strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, ARVORE_M) {
				_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj = VAZIO
				_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].pontuacao -= 5

		return true
	}

	return false

}

func Cerca (id string, pos string) bool {
	pos = strings.TrimSpace(pos)
	posicao := strings.Split(pos, ",")
	

	if strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, VAZIO) {
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj = CERCA
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].pontuacao += 5

		return true
	}

	return false

}

func Destruir (id string, pos string) string {

	pos = strings.TrimSpace(pos)
	posicao := strings.Split(pos, ",")
	

	if strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, CERCA) {
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj = VAZIO
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].pontuacao += 10
		return "{\"destruido\": \"true\"}"
	}

	return "{\"destruido\": \"false\"}"

}

func Morrendo (id string, pos string) string {

	pos = strings.TrimSpace(pos)

	posicao := strings.Split(pos, ",")
	
	fmt.Println("Morrendo")

	if strings.EqualFold(_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj, ARVORE) {
		_tabuleiro.objetos[string(posicao[0]) + "," + string(posicao[1])].tipo_obj = ARVORE_M

		fmt.Println("Setado Morrendo")
		
		return "{\"morrendo\": \"true\"}"
	}

	return "{\"morrendo\": \"false\"}"

}

func removerJogador(id string) bool {
	
	ident, err := strconv.Atoi(id)
	if err != nil {
		fmt.Println("Erro:", err.Error())
		return false
	}
	
	_tabuleiro.objetos[string(_listadejogadores.jogadores[ident].pos_linha) + "," + string(_listadejogadores.jogadores[ident].pos_coluna)].id = 0
	_tabuleiro.objetos[string(_listadejogadores.jogadores[ident].pos_linha) + "," + string(_listadejogadores.jogadores[ident].pos_coluna)].tipo_jog = VAZIO
	_listadejogadores.jogadores[ident].conexao.Close()
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
				MoverJogador(id, posAtual, posDesejada)
				tab := RetornarTabuleiro()
	
				b :=[]byte(tab)
	
				
				broadcast(b)
	
			} else if strings.EqualFold(cmd[0], string("plantar")) {
	
				id := cmd[1]
				pos := cmd[2]
	
				fmt.Println("Plantar")
				if(Plantar(id, pos)){
					tab := RetornarTabuleiro()
					b := []byte(tab)
		
					broadcast(b)
				}
	
			} else if strings.EqualFold(cmd[0], string("cortar")) {
	
				id := cmd[1]
				pos := cmd[2]
	
				fmt.Println("Cortar")
				if(Cortar(id, pos)){
					tab := RetornarTabuleiro()
					b := []byte(tab)
		
					broadcast(b)
				}
	
			} else if strings.EqualFold(cmd[0], string("cerca")) {
	
				id := cmd[1]
				pos := cmd[2]
	
				fmt.Println("Cerca")
				if(Cerca(id, pos)){
					tab := RetornarTabuleiro()
					b := []byte(tab)
		
					broadcast(b)
				}
	
			} else if strings.EqualFold(cmd[0], string("destruir")) {
	
				id := cmd[1]
				pos := cmd[2]
	
				fmt.Println("Destruir")
				destruir := Destruir( id, pos )
				tab := RetornarTabuleiro()
				b := []byte( destruir + ";" + tab )
	
				broadcast(b)
	
			} else if strings.EqualFold(cmd[0], string("morrendo")) {
				
				fmt.Println("CMD: Morrendo")
	
				id := cmd[1]
				fmt.Println("CMD: ID Morrendo")
				pos := cmd[2]
				fmt.Println("CMD: POS Morrendo")
	
				fmt.Println("Morrendo")
				Morrendo( id, pos )
				tab := RetornarTabuleiro()
				b := []byte( tab )
	
				broadcast(b)
	
			} else if strings.EqualFold(cmd[0], string("sair")) {
				
				fmt.Println("Sair")
				id := strings.TrimSpace(cmd[1])
				if(removerJogador(id)){
					tab := RetornarTabuleiro()
					b :=[]byte(tab)	
					broadcast(b)
				}
	
			} else if strings.EqualFold(cmd[0], string("tabuleiro")) {
				
				fmt.Println("Pedido de Tabuleiro")
				id,err := strconv.Atoi(strings.TrimSpace(cmd[1]))
				if err == nil {
					tab := RetornarTabuleiro()
					b := []byte(tab)
					_listadejogadores.jogadores[id].conexao.Write(b)
				}
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
			MoverJogador(id, posAtual, posDesejada)
			tab := RetornarTabuleiro()
	
			b :=[]byte(tab)
				
			broadcast(b)
	
		} else if strings.EqualFold(cmd[0], string("plantar")) {
	
			id := cmd[1]
			pos := cmd[2]
	
			fmt.Println("Plantar")
			if(Plantar(id, pos)){
				tab := RetornarTabuleiro()
				b := []byte(tab)
	
				broadcast(b)
			}
	
		} else if strings.EqualFold(cmd[0], string("cortar")) {
	
			id := cmd[1]
			pos := cmd[2]

			if(Cortar(id, pos)){
				tab := RetornarTabuleiro()
				b := []byte(tab)
	
				broadcast(b)
			}
	
		} else if strings.EqualFold(cmd[0], string("cerca")) {
	
			id := cmd[1]
			pos := cmd[2]

			if(Cerca(id, pos)){
				tab := RetornarTabuleiro()
				b := []byte(tab)
	
				broadcast(b)
			}

		} else if strings.EqualFold(cmd[0], string("destruir")) {
	
			id := cmd[1]
			pos := cmd[2]

			destruir := Destruir( id, pos )
			tab := RetornarTabuleiro()
			b := []byte( destruir + ";" + tab )

			broadcast(b)
	
		} else if strings.EqualFold(cmd[0], string("morrendo")) {
			
			fmt.Println("CMD: Morrendo")

			id := cmd[1]
			fmt.Println("CMD: ID Morrendo")
			pos := cmd[2]
			fmt.Println("CMD: POS Morrendo")

			fmt.Println("Morrendo")
			Morrendo( id, pos )
			tab := RetornarTabuleiro()
			b := []byte( tab )

			broadcast(b)

		} else if strings.EqualFold(cmd[0], string("sair")) {
			
			fmt.Println("Sair")
			id := strings.TrimSpace(cmd[1])
			if(removerJogador(id)){
				tab := RetornarTabuleiro()
				b :=[]byte(tab)	
				broadcast(b)
			}
		} else if strings.EqualFold(cmd[0], string("tabuleiro")) {
				
				fmt.Println("Pedido de Tabuleiro")
				id,err := strconv.Atoi(strings.TrimSpace(cmd[1]))
				if err == nil {
					tab := RetornarTabuleiro()
					b := []byte(tab)
					_listadejogadores.jogadores[id].conexao.Write(b)
				}
			}
	}
}
