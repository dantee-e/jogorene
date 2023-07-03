import java.net.*;  
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.imageio.*;

class Constants {
    public static final int carWidth = 50;
    public static final int carHeight = 100;
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
    public int x;
    public int y;
    public double vel = 4;
    public double angulo;
    public int lap = 0;
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
    JFrame jf;
    ServerClient cliente1, cliente2;
    ServerSend(JFrame jf){
        this.jf = jf;
        System.out.println("serversend iniciado");
    }
    public void run(){
        while(true){
            try{
                cliente1.sendJFrame(jf);
                cliente2.sendJFrame(jf);
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
    
    public void sendJFrame(JFrame jf) throws Exception{
        try {
            if (s.isConnected()) {
                dout.reset();
                dout.writeObject(jf);
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
                char input = (char) din.read();
                if ((int) input == 65535) break;
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
                        carro.vel = 4;
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


    public static JPanel makeJPanel(Carro carro[]){
        JPanel painel = new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.BLACK);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 100, 100);
                // g2d.setColor(Color.LIGHT_GRAY);
                g2d.setColor(Color.GREEN);
                g2d.fillRoundRect(150, 140, 880, 500, 400, 400);
                g2d.setColor(Color.RED);
                g2d.drawRect(pista.x, pista.y, pista.width, pista.height);
                g2d.drawRect(checkpoint.x, checkpoint.y, checkpoint.width, checkpoint.height);
                g2d.drawRect(chegada.x, chegada.y, chegada.width, chegada.height);
                //g2d.drawImage(img[2], chegada.x - 50, chegada.y, chegada.width + 100, chegada.height, this);
                g2d.rotate(carro[0].angulo, (int)carro[0].x + Constants.carWidth/2, (int)carro[0].y + Constants.carHeight/2);
                g2d.drawImage(img[0], (int)carro[0].x, (int)carro[0].y, Constants.carWidth, Constants.carHeight, this);
                g2d.rotate(-carro[0].angulo, (int)carro[0].x + Constants.carWidth/2, (int)carro[0].y + Constants.carHeight/2);
                Toolkit.getDefaultToolkit().sync();
            }
        };
        return painel;
    }


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