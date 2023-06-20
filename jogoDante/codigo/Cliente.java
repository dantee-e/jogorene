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
    public void sendU(boolean ativado){
        try{
            writer.write('U');
            writer.flush();
            if(ativado) writer.write('P');
            else writer.write('R');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendD(boolean ativado){
        try{
            writer.write('D');
            if(ativado) writer.write('P');
            else writer.write('R');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
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
    JPanel painel;
    Carro carro[] = new Carro[2];
    
    public JogoBase(Conexao conexao){
        carro[0] = new Carro(30, 30, 0);
        carro[1] = new Carro(30, 30, 0);
        setTitle("JogoBase");  // Define o título do JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Define o comportamento ao fechar o JFrame
        setPreferredSize(new Dimension(1000, 600));
        painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 100, 100);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRoundRect(120, 100, getWidth() - 240, getHeight() - 200, 200, 200);
                g.setColor(Color.RED);
                g.drawRect(carro[0].x, carro[0].y, 100, 50);
                //g.drawRect(rect[1].x, rect[1].y, rect[1].width, rect[1].height);
                //g.drawImage(img[2], 250, 250, 50, 50, this);
                Toolkit.getDefaultToolkit().sync();
            }
        };
        painel.setFocusable(true);

        painel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                
                if(e.getKeyCode()==KeyEvent.VK_UP){
                    conexao.sendU(true);
                }
                else if(e.getKeyCode()==KeyEvent.VK_DOWN){
                    conexao.sendD(true);
                }
                else if(e.getKeyCode()==KeyEvent.VK_LEFT){
                    conexao.sendL(true);
                }
                else if(e.getKeyCode()==KeyEvent.VK_RIGHT){
                    conexao.sendR(true);
                }
            }
            public void keyReleased(KeyEvent e) {
                
                if(e.getKeyCode()==KeyEvent.VK_UP){
                    conexao.sendU(false);
                }
                else if(e.getKeyCode()==KeyEvent.VK_DOWN){
                    conexao.sendD(false);
                }
                else if(e.getKeyCode()==KeyEvent.VK_LEFT){
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

