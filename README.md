# Java Socks 5
This project aims to implement Socks 5 server in java. 

Currently, the project is still in development, bugs may be found.

## Basic Usage
Basic usage of the API is starting the server:
```java
public static void main(String[] args) throws IOException {
    new Socks5Server(1080).start();
}
```

Advanced options will be developed in the future.