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

class Carro extends Rectangle implements Serializable{
  public double x;
  public double y;
  public int width;
  public int height;
  public int velocidade;
  public double theta;


  Carro(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  public void viraDireita(int X, int Y, float ANG){
        x += X;
        y += Y;
        angulo += ANG;
    }
  public void viraEsquerda() {
    
  }
}

class ServerClient extends Thread{
    ServerSocket ss;
    Carro carro;
    int carN;
    public ServerClient(ServerSocket SS, int carNumber){
        ss = SS;
        carN = carNumber;
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
            
            dout.writeObject(carro);
            
            dout.flush();
            DataInputStream din = new DataInputStream(s.getInputStream());
            char input, i=0;
            while (true) {
                input = (char) din.read();
                if((int)input==65535) break;
                if(i%2==1)
                    System.out.println(input);
                else {System.out.print(carN);System.out.print(input);}
                i++;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}