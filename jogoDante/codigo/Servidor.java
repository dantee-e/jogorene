import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import javax.swing.*;

class Constants {
    public static final int carWidth = 50;
    public static final int carHeight = 100;
    public static final double carV = 1;
}

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
    public double x;
    public double y;
    public double vel = Constants.carV;
    public double angulo;
    public int lap = 0;
    
    public Carro(double X, double Y, double ANG){
        x = X;
        y = Y;
        angulo = ANG;
    }
    public Carro(Carro carro_){
        x = carro_.getX();
        y = carro_.getY();
        angulo = carro_.getAng();
    }
    public void updateCarro(double X, double Y, double ANG){
        x += X;
        y += Y;
        angulo += ANG;
    }
    public void printCarro(){
        System.out.println("X = "+ x);
        System.out.println("Y = "+ y);
        System.out.println("Angulo = "+ angulo);
    }
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getAng(){
        return angulo;
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
                carro1.sendCarro(carro1.dout);
                carro1.carro.printCarro();

                carro2.sendCarro(carro1.dout);
                carro2.carro.printCarro();

                carro1.sendCarro(carro2.dout);
                carro1.carro.printCarro();
                
                carro2.sendCarro(carro2.dout);
                carro2.carro.printCarro();
                
            } catch(Exception e){
                System.out.println("Erro na run do serversend");
                e.printStackTrace();
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
    public ObjectOutputStream dout;
    DataInputStream din;
    
    public ServerClient(ServerSocket SS, int carNumber){
        if(carNumber==1)
            carro = new Carro(31, 31, Math.toRadians(-90));
        else
            carro = new Carro(31, 50, Math.toRadians(-90));
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
    
    public void sendCarro(ObjectOutputStream dou) throws Exception{
        try {
            if (s.isConnected()) {
                dou.reset();
                dou.writeObject(carro);
                dou.flush();
            } else {
                System.out.println("fechou conexao servidor");
                s.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void run() {
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carro.setBounds((int) carro.x, (int) carro.y, Constants.carWidth, Constants.carHeight);
                double dx = Math.cos(carro.angulo-Math.toRadians(-90));
                double dy = Math.sin(carro.angulo-Math.toRadians(-90));
                double magnitude = Math.sqrt(dx * dx + dy * dy); // Calcula a magnitude do vetor de velocidade

                if (magnitude != 0.0) {
                    dx /= magnitude; // Normaliza a componente X
                    dy /= magnitude; // Normaliza a componente Y
                }

                carro.x += carro.vel * dx;
                carro.y += carro.vel * dy;
            }
        });
        timer.start();

        try {
            System.out.println("Carro pronto");
            while (true) {
                char input = (char) din.read();
                if ((int) input == 65535) break;
                System.out.println(input);
                switch(input){
                    case 'L':
                        carro.angulo -= Math.toRadians(5);
                        break;
                    case 'R':
                        carro.angulo += Math.toRadians(5);
                        break;
                    case 'S':
                        carro.vel = 0.2;
                        break;
                    case 'Q':
                        carro.vel = 1;
                        break;
                }
            }
            
            System.out.println("conexao encerrada");
        } catch (Exception e) {
            System.out.println("erro no run da ServerClient");
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
            ServerSend sS = new ServerSend(car1, car2);
            sS.start();
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.out.println("erro na main do servidor");
        }
    }
}