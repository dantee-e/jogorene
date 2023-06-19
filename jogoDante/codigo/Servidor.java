import java.net.*;  
import java.io.*;
import java.util.*;

class Carro{
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
    void updateV(int vel){
        Vx = vel*Math.cos(angulo);
        Vy = vel/Math.sin(angulo);
    }
}
class MyServer{  
    public static void main(String args[])throws Exception{  
        ServerSocket ss=new ServerSocket(80);
        Carro carro[] = new Carro[2];
        carro[0] = new Carro(30, 30, 0);
        carro[1] = new Carro(30, 30, 0);
        Socket s[] = new Socket[2];
        for(int i=0;i<2;i++){
            s[i]=ss.accept(); 
        }
        System.out.println("Os dois carros estao prontos");
        DataInputStream din[];
        din[0]=new DataInputStream(s[0].getInputStream());
        din[1]=new DataInputStream(s[1].getInputStream()); 
        DataOutputStream dout[];
        dout[0]=new DataOutputStream(s[0].getOutputStream());
        dout[1]=new DataOutputStream(s[1].getOutputStream()); 
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        
        String str="",str2="";  
        while(!str.equals("stop")){  
            str=din[0].readUTF();
            System.out.println("client says: "+str);  
            str2=br.readLine();  
            dout.writeUTF(str2);  
            dout.flush();  
        }  
        din.close();  
        s[0].close();
        s[1].close();  
        ss.close();  
    }
}

class input1 implements Runnable{
    public void run(){
        
    }
}