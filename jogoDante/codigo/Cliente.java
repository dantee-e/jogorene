import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.imageio.*;


class Constants {
    public static final int carWidth = 50;
    public static final int carHeight = 100;
}



class Conexao extends Thread{
    Socket socket;
    DataInputStream in;
    ObjectInputStream objectIn;
    OutputStream outputStream;
    OutputStreamWriter writer;
    JogoBase jb;
    Conexao(){
        try {
            socket = new Socket("localhost", 8080);
            in = new DataInputStream(socket.getInputStream());
            objectIn = new ObjectInputStream(in);

            outputStream = socket.getOutputStream();
            writer = new OutputStreamWriter(outputStream);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Carro getCar() throws Exception{
        try {
            Carro carro = (Carro) objectIn.readObject();
            return carro;
        } catch (Exception e) {
            System.out.println("Fechou a conexao do cliente");
            socket.close();
            throw e;
        }
    }    
    public void sendL(){
        try{
            writer.write('L');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendR(){
        try{
            writer.write('R');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendSlow(){
        try{
            writer.write('S');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendQuick(){
        try{
            writer.write('Q');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendVolta(){
        try{
            writer.write('V');
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
}

class ClienteReceive extends Thread {
    ClienteReceive(Carro carro1, Carro carro2, Conexao conexao){
        try {
            carro1 = conexao.getCar();
            carro2 = conexao.getCar();
        }catch(Exception e){}
    }
}
class JogoBase extends JFrame{
    Image img[] = new Image[3];
    JPanel painel;
    Conexao conexao;
    Carro carro[] = new Carro[2];
    JogoBase() {
        carro[0] = new Carro(50, 100, 0);
        carro[1] = new Carro(50, 200, 0);
        conexao = new Conexao();
        
        
        try {
            img[0] = ImageIO.read(new File("../sprites/BlueCar.png"));
            img[1] = ImageIO.read(new File("../sprites/BlueCar.png"));
            img[2] = ImageIO.read(new File("../sprites/FinishLine.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setPreferredSize(new Dimension(1200, 800));
        
        Rectangle pista = new Rectangle(250, 165, 680, 445);
        Rectangle checkpoint = new Rectangle(575, 600, 50, 180);
        Rectangle chegada = new Rectangle(575, 0, 50, 180);

        painel = new JPanel() {
            protected void paintComponent(Graphics g) {
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

        painel.setFocusable(true);
        painel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    conexao.sendL();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    conexao.sendR();
                } 
            }
        });

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carro[0].setBounds((int)carro[0].x, (int)carro[0].y, Constants.carWidth, Constants.carHeight);
                //logica da velocidade em relacao a pista
                if (carro[0].intersects(pista)) {
                    conexao.sendSlow();
                }
                else {
                    conexao.sendQuick();
                }
                //logica das voltas
                if (carro[0].lap == 0 && carro[0].intersects(checkpoint))
                    conexao.sendVolta();
                System.out.println(carro[0].lap);
                if (carro[0].lap == 1 && carro[0].intersects(chegada)) {
                    conexao.sendVolta();
                }
                if (carro[0].lap == 2 && carro[0].intersects(checkpoint))
                    conexao.sendVolta();
                if (carro[0].lap == 3 && carro[0].intersects(chegada) || carro[1].lap==4) {
                    JOptionPane.showMessageDialog(painel, "Jogo encerrado");
                    conexao.sendVolta();
                    System.exit(0);
                }

                //logica de impedir que o carro saia da janela
                if (carro[0].x < 0) {
                    carro[0].x = 0; // Limite esquerdo da janela
                } else if (carro[0].x + Constants.carWidth > getWidth()) {
                    carro[0].x = getWidth() - Constants.carWidth; // Limite direito da janela
                }

                if (carro[0].y < 0) {
                    carro[0].y = 0; // Limite superior da janela
                } else if (carro[0].y + Constants.carHeight > getHeight()) {
                    carro[0].y = getHeight() - Constants.carHeight; // Limite inferior da janela
                }

            
                
                repaint();
            }
        });
        timer.start();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(painel);
        pack();
        setVisible(true);
        while(true){
            try {
                carro[0] = conexao.getCar();
                carro[1] = conexao.getCar();
            }catch(Exception e){}
            repaint();
        }
    }
}

class Cliente{
    public static void main(String[] str){
        new JogoBase();
    }
}

