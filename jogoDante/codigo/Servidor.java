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
    public double Vx;
    public double Vy;
    public double angulo;
    public Carro(int X, int Y, float ANG){
        x = X;
        y = Y;
        angulo = ANG;
    }
    public void updateCarro(int X, int Y, float ANG){
        x = X;
        y = Y;
        angulo = ANG;
    }
}

class ServerClient extends Thread{
    ServerSocket ss;
    Carro carro;
    public ServerClient(ServerSocket SS){
        ss = SS;
    }

    public void updateCarro(int newX, int newY, int angulo){
        carro.updateCarro(newX, newY, angulo);
    }

    public void sendCarro(ObjectOutputStream dout){
        try{
            dout.writeObject(carro);
            dout.flush();
        } catch(Exception e){System.out.println(e);}
        
    }

    public void run() {
        carro = new Carro(30, 30, 0);
        Socket s;
        try {
            s = ss.accept();
    
            System.out.println("Carro pronto");
            ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
            System.out.println("check");
            //ObjectInputStream din = new ObjectInputStream(s.getInputStream());
            System.out.println("check");
            dout.writeObject(carro);
            
            dout.flush();
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class Servidor {
    public static void main(String args[]) {
        ServerSocket ss;
        new Clock();

        try {
            ss = new ServerSocket(80);
            ServerClient car1 = new ServerClient(ss);
            car1.start();
            ServerClient car2 = new ServerClient(ss);
            car2.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}