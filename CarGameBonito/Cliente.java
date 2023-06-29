import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.*;



class Conexao extends Thread{
    Socket socket;
    DataInputStream in;
    ObjectInputStream objectIn;
    OutputStream outputStream;
    OutputStreamWriter writer;

    public void run() {
        try { 
            socket = new Socket("localhost", 8080);
            in = new DataInputStream(socket.getInputStream());
            objectIn = new ObjectInputStream(in);
            Carro carro = (Carro) objectIn.readObject();

            outputStream = socket.getOutputStream();
            writer = new OutputStreamWriter(outputStream);

            System.out.println("check");
            System.out.println("Carro recebido:");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void closeSocket(){
        try{
            socket.close();
        } catch(Exception e){e.printStackTrace();}
    }
    public void sendL(boolean ativado){
        try{
            writer.write('L');
            if(ativado) writer.write('P');
            else writer.write('R');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendR(boolean ativado){
        try{
            writer.write('R');
            if(ativado) writer.write('P');
            else writer.write('R');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }

}

class JogoBase extends JFrame{
    Image img[] = new Image[3];
    JPanel painel;
    public JogoBase() {
        try {
            img[0] = ImageIO.read(new File("sprites/RedCar.png"));
            img[1] = ImageIO.read(new File("sprites/BlueCar.png"));
            img[2] = ImageIO.read(new File("sprites/FinishLine.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setPreferredSize(new Dimension(1000, 600));
        Carro carro  = new Carro(100, 50, 70, 40);
        painel = new JPanel() {
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 100, 100);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRoundRect(120, 100, getWidth() - 240, getHeight() - 200, 200, 200);
            g2d.setColor(Color.RED);
            g2d.rotate(carro.theta, (int)carro.x + carro.width/2, (int)carro.y + carro.height/2);
            //g2d.fillRect((int)carro.x, (int)carro.y, carro.width, carro.height);
            g2d.drawImage(img[1], (int)carro.x, (int)carro.y, carro.width, carro.height, this);
            g2d.rotate(-carro.theta, (int)carro.x + carro.width/2, (int)carro.y + carro.height/2);
            Toolkit.getDefaultToolkit().sync();
        }
        };

        painel.setFocusable(true);
        painel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_LEFT){
                    conexao.sendL(true);
                }
                else if(e.getKeyCode()==KeyEvent.VK_RIGHT){
                    conexao.sendR(true);
                }
            }
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_LEFT){
                    conexao.sendL(false);
                }
                else if(e.getKeyCode()==KeyEvent.VK_RIGHT){
                    conexao.sendR(false);
                }
            }
        }); 
        getContentPane().add(painel);  // Adiciona o painel ao JFrame

        pack();  // Redimensiona o JFrame para se adequar ao tamanho preferido do painel
        setLocationRelativeTo(null);  // Centraliza o JFrame na tela
        setVisible(true); 
    }
}

class Cliente{
    public static void main(String[] str){
        Conexao conexao = new Conexao();
        conexao.start();

        new JogoBase(conexao);
    }
}

