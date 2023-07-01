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

class ClienteReceive extends Thread{
    Conexao c;
    ClienteReceive(Conexao c){
        this.c = c;
    }
    public void run(){
        while(true){
            try{
                c.getCar();
            }catch(Exception e){
                System.out.println("Erro no client receive");
                e.printStackTrace();
                break;
            }
            
        }
    }
    
}

class Conexao{
    Socket socket;
    DataInputStream in;
    ObjectInputStream objectIn;
    OutputStream outputStream;
    OutputStreamWriter writer;
    Carro carro[] = new Carro[2];
    JogoBase jb;
    Conexao(Carro carro1, Carro carro2, JogoBase j){
        carro[0] = carro1;
        carro[1] = carro2;
        jb = j;
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

    public void getCar() throws Exception{
        try {
            carro[0] = (Carro) objectIn.readObject();
            carro[1] = (Carro) objectIn.readObject();
        } catch (Exception e) {
            System.out.println("Fechou a conexao do cliente");
            socket.close();
            throw e;
        }
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
            /*if(ativado) writer.write('P');
            else writer.write('R');*/
            writer.flush();
        } catch(Exception e){System.out.println(e);}
    }
    public void sendR(boolean ativado){
        try{
            writer.write('R');
            /*if(ativado) writer.write('P');
            else writer.write('R');*/
            writer.flush();
        } catch(Exception e){System.out.println(e);}
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
        conexao = new Conexao(carro[0], carro[1], this);
        
        new ClienteReceive(conexao).start();
        try {
            img[0] = ImageIO.read(new File("../sprites/BlueCar.png"));
            img[1] = ImageIO.read(new File("../sprites/BlueCar.png"));
            img[2] = ImageIO.read(new File("../sprites/FinishLine.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setPreferredSize(new Dimension(1000, 600));
        Rectangle pista = new Rectangle(120, 100, getWidth() - 240, getHeight() - 200);

        painel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.BLACK);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 100, 100);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(120, 100, getWidth() - 240, getHeight() - 200);
                //g2d.fillRoundRect(120, 100, getWidth() - 240, getHeight() - 200, 200, 200);
                g2d.rotate(carro[0].angulo, (int)carro[0].x + Constants.carWidth/2, (int)carro[0].y + Constants.carHeight/2);
                //g2d.fillRect((int)carro.x, (int)carro.y, Constants.carWidth, Constants.carHeight);
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
            carro[0].angulo -= Math.toRadians(5);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            carro[0].angulo += Math.toRadians(5);
            } 
        }
        });

        /*Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            *if (carro[0].intersects(pista)) {
                //carro[0].x -= pista.getX();
                //carro[0].y -= pista.getY();
            }*
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
                //carro[0].velocidade = 3;
                double dx = Math.cos(carro[0].angulo);
                double dy = Math.sin(carro[0].angulo);
                double magnitude = Math.sqrt(dx * dx + dy * dy); // Calcula a magnitude do vetor de velocidade

                if (magnitude != 0.0) {
                    dx /= magnitude; // Normaliza a componente X
                    dy /= magnitude; // Normaliza a componente Y
                }
                //carro.x += carro.velocidade * dx;
                //carro.y += carro.velocidade * dy;
                repaint();
            }
        });
        timer.start();*/

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(painel);
        pack();
        setVisible(true);
        while(true){
            System.out.println(carro[0].x);
            repaint();
        }
    }
}

class Cliente{
    public static void main(String[] str){
        new JogoBase();
    }
}

