import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import javax.swing.*;

class Constants {
    public static final int carWidth = 50;
    public static final int carHeight = 100;
    public static final double carVF = .8;
    public static final double carVS = carVF/10;
    public static final int scrHeight = 800;
    public static final int scrWidth = 1200;
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
    public double vel;
    public double angulo;
    public int lap = 0;
    public Carro(double X, double Y, double ANG, double velocidade){
        x = X;
        y = Y;
        angulo = ANG;
        vel = velocidade;
    }
    public Carro(Carro carro_){
        x = carro_.getX();
        y = carro_.getY();
        angulo = carro_.getAng();
        vel = carro_.vel;
        lap = carro_.lap;
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
        System.out.println("Volta = " + lap);
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

                carro2.sendCarro(carro1.dout);

                carro1.sendCarro(carro2.dout);
                
                carro2.sendCarro(carro2.dout);
                Thread.sleep(10);
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
    Rectangle pista;
    Rectangle checkpoint;
    Rectangle chegada;
    public ServerClient(ServerSocket SS, int carNumber){
        if(carN==1)
            carro = new Carro(30, 10, Math.toRadians(-90), Constants.carVF);
        else
            carro = new Carro(30, 60, Math.toRadians(-90), Constants.carVF);
        ss = SS;
        carN = carNumber;
        pista = new Rectangle(250, 165, 680, 445);
        checkpoint = new Rectangle(575, 600, 50, 180);
        chegada = new Rectangle(575, 0, 50, 180);
        try{
            s = ss.accept();
            dout = new ObjectOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
        }catch(Exception e){
            System.out.println("erro na construtora do ServerClient");
        }
    }
    public void resetCarro(){
        if(carN==1)
            carro = new Carro(30, 10, Math.toRadians(-90), Constants.carVF);
        else
            carro = new Carro(30, 60, Math.toRadians(-90), Constants.carVF);
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
    public boolean isOnline(){
        return s.isConnected();
    }
    public void run() {
        Timer timer = new Timer(5, new ActionListener() {
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

                if (carro.intersects(pista))
                    carro.vel = Constants.carVS;
                else
                    carro.vel = Constants.carVF;
                if(carro.lap==0 && carro.intersects(chegada)){
                    carro.lap++;
                }
                if(carro.lap==1 && carro.intersects(checkpoint)){
                    carro.lap++;
                }
                if(carro.lap==2 && carro.intersects(chegada)){
                    carro.lap++;
                }
                if(carro.lap==3 && carro.intersects(checkpoint)){
                    carro.lap++;
                }
                if(carro.lap==4 && carro.intersects(chegada)){
                    carro.lap++;
                }
                if(carro.x<0) carro.x=0;
                if(carro.y<0) carro.y=0;
                if(carro.x>Constants.scrWidth-Constants.carWidth) carro.x=Constants.scrWidth-Constants.carWidth;
                if(carro.y>Constants.scrHeight-Constants.carHeight) carro.y=Constants.scrHeight-Constants.carHeight;
                
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
                        carro.angulo -= Math.toRadians(10);
                        break;
                    case 'R':
                        carro.angulo += Math.toRadians(10);
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

            while(!car1.isOnline() && ! car2.isOnline());
            car1.resetCarro();
            car2.resetCarro();
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.out.println("erro na main do servidor");
        }
    }
}