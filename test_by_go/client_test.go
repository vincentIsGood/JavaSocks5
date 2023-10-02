package testbygo_test

import (
	"fmt"
	"testing"

	"golang.org/x/net/proxy"
)

const DEST_ADDR string = "127.0.0.1:1234"
const PROXY_ADDR string = "127.0.0.1:1080"

func TestConnectionEstablishment(t *testing.T){
	t.Run("Connect to socks5 server with no auth", func(t *testing.T) {
		dialer, err := proxy.SOCKS5("tcp", PROXY_ADDR, nil, proxy.Direct)
		if err != nil{
			panic(fmt.Sprintf("Cannot connect to proxy %s: %s", PROXY_ADDR, err.Error()))
		}

		_, err = dialer.Dial("tcp", DEST_ADDR)
		if err != nil{
			panic(fmt.Sprintf("Cannot connect to %s via proxy %s: %s", DEST_ADDR, PROXY_ADDR, err.Error()))
		}
	})

	t.Run("Connect to socks5 server with auth", func(t *testing.T) {
		authCred := proxy.Auth{User: "asd", Password: "dsa"}
		dialer, err := proxy.SOCKS5("tcp", PROXY_ADDR, &authCred, proxy.Direct)
		if err != nil{
			panic(fmt.Sprintf("Cannot connect to proxy %s: %s", PROXY_ADDR, err.Error()))
		}

		_, err = dialer.Dial("tcp", DEST_ADDR)
		if err != nil{
			panic(fmt.Sprintf("Cannot connect to %s via proxy %s: %s", DEST_ADDR, PROXY_ADDR, err.Error()))
		}
	})
}

// Ref: https://stackoverflow.com/questions/33585587/creating-a-go-socks5-client
//
// Ref: https://www.developer.com/languages/intro-socket-programming-go/
func TestPayload(t *testing.T){
	// authCred := proxy.Auth{User: "", Password: ""}
    dialer, err := proxy.SOCKS5("tcp", PROXY_ADDR, nil, proxy.Direct)
	if err != nil{
		panic(fmt.Sprintf("Cannot connect to proxy %s: %s", PROXY_ADDR, err.Error()))
	}
	conn, err := dialer.Dial("tcp", DEST_ADDR)
	if err != nil{
		panic(fmt.Sprintf("Cannot connect to %s via proxy %s: %s", DEST_ADDR, PROXY_ADDR, err.Error()))
	}

	t.Run("Expect sent payload to be echoed back", func(t *testing.T) {
		const MESSAGE string = "Testing my new proxy"

		_, err = conn.Write([]byte(MESSAGE))
		if err != nil{
			panic("Cannot write to connection")
		}

		buffer := make([]byte, 1024)
		count, err := conn.Read(buffer)
		if err != nil{
			panic("Cannot read from connection")
		}

		data := string(buffer[:count])
		if data != "Testing my new proxy"{
			t.Errorf("Expected '%s' but got '%s'", MESSAGE, data)
		}
	})

}