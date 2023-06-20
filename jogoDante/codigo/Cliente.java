import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.io.Serializable;



class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 80);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            ObjectInputStream objectIn = new ObjectInputStream(in);
            Carro carro = (Carro) objectIn.readObject();
            System.out.println("check");
            System.out.println("Carro recebido:");
            System.out.println("x: " + carro.x);
            System.out.println("y: " + carro.y);
            System.out.println("Vx: " + carro.Vx);
            System.out.println("Vy: " + carro.Vy);
            System.out.println("ângulo: " + carro.angulo);

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}