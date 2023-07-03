import java.net.*;  
import java.io.*;
import java.util.*;
import java.awt.*;

class Clock {
    static long start = System.nanoTime();
    public static long getTime(){
        return System.nanoTime();
    }
    public static long getExecTime(){
        return System.nanoTime()-start;
    }
 }

class Carro extends Rectangle {
    public int x;
    public int y;
    public double vel;
    public double angulo;
    public Carro(int X, int Y, double ANG){
        x = X;
        y = Y;
        angulo = ANG;
    }
    public void updateCarro(int X, int Y, float ANG){
        x += X;
        y += Y;
        angulo += ANG;
    }
    public void printCarro(){
        System.out.println("X = "+ x);
        System.out.println("Y = "+ y);
        System.out.println("Angulo = "+ angulo);
    }
}

class ServerSend extends Thread{
    ServerClient carro1, carro2;
    ServerSend(ServerClient carro1_, ServerClient carro2_){
        this.carro1 = carro1_;
        this.carro2 = carro2_;
        System.out.println("serversend iniciado");
    }
    public void run(){
        while(true){
            try{
                carro1.sendCarro();
                carro2.sendCarro();
            } catch(Exception e){
                System.out.println("Erro na run do serversend");
                break;
            }
        }
    }
}

class ServerClient extends Thread{
    ServerSocket ss;
    public Carro carro;
    Socket s;
    int carN;
    ObjectOutputStream dout;
    DataInputStream din;
    public ServerClient(ServerSocket SS, int carNumber){
        carro = new Carro(31, 31, 2);
        ss = SS;
        carN = carNumber;
        try{
            s = ss.accept();
            dout = new ObjectOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
        }catch(Exception e){
            System.out.println("erro na construtora do ServerClient");
        }
    }
    
    public void updateCarro(int newX, int newY, int angulo){
        carro.updateCarro(newX, newY, angulo);
    }
    public void sendCarro() throws Exception{
        try {
            if (s.isConnected()) {
                dout.reset();
                dout.writeObject(carro);
                dout.flush();
            } else {
                System.out.println("fechou conexao servidor");
                s.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }
    public void run() {
        try {
            System.out.println("Carro pronto");

            while (true) {
                try {
                    char input = (char) din.read();
                    if ((int) input == 65535) break;
                    if(input=='L'){
                        carro.angulo -= Math.toRadians(5);
                    }
                    else if(input=='R'){
                        carro.angulo += Math.toRadians(5);
                    }
                } catch (EOFException e) {
                    // Lidar com a exceção EOFException (fim do fluxo)
                    System.out.println("Fim do fluxo de entrada. Encerrando a execução.");
                    break;
                }
            }

            System.out.println("conexao encerrada");

        } catch (Exception e) {
            System.out.println("erro no run da ServerClient");
        }
    }

}

class Servidor {
    public static void main(String args[]) {
        ServerSocket ss;
        new Clock();

        try {
            ss = new ServerSocket(8080);
            ServerClient car1 = new ServerClient(ss, 1);
            car1.start();
            ServerClient car2 = new ServerClient(ss, 2);
            car2.start();
            ServerSend sS = new ServerSend(car1, car2);
            sS.start();
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.out.println("erro na main do servidor");
        }
    }
}