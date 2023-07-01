import java.net.*;  
import java.io.*;
import java.util.*;

class Clock {
    static long start = System.nanoTime();
    public static long getTime(){
        return System.nanoTime();
    }
    public static long getExecTime(){
        return System.nanoTime()-start;
    }
 }

class Carro implements Serializable{
    public int x;
    public int y;
    public double vel;
    public double angulo;
    public Carro(int X, int Y, float ANG){
        x = X;
        y = Y;
        angulo = ANG;
    }
    public void updateCarro(int X, int Y, float ANG){
        x += X;
        y += Y;
        angulo += ANG;
    }
}

class ServerSend extends Thread{
    ServerClient carro1, carro2;
    ServerSend(ServerClient carro1_, ServerClient carro2_){
        this.carro1 = carro1_;
        this.carro2 = carro2_;
    }
    public void run(){
        while(true){
            carro1.sendCarro();
            carro2.sendCarro();
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
        carro = new Carro(30, 30, 0);
        ss = SS;
        carN = carNumber;
        try{
            s = ss.accept();
            dout = new ObjectOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
        }catch(Exception e){e.printStackTrace();}
        
    }
    public Carro getCarro(){
        return carro;
    }
    public void updateCarro(int newX, int newY, int angulo){
        carro.updateCarro(newX, newY, angulo);
    }
    public void sendCarro(){
        try{
            dout.writeObject(carro);
            System.out.println(carro.x);
            dout.flush();
        } catch(IOException e){System.out.println(e);}
        
    }
    public void run() {
        try {
            
    
            System.out.println("Carro pronto");
            
            char input;
            int i=0;
            while (true) {
                input = (char) din.read();
                if((int)input==65535) break;
                /*if(i%2==1)
                    System.out.println(input);
                else {System.out.print(carN);System.out.print(input);}
                i++;*/
                System.out.print(carN);
                System.out.println(input);
            }
            System.out.println("conexao encerrada");
            
        } catch (Exception e) {
            e.printStackTrace();
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
            new ServerSend(car1, car2).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}