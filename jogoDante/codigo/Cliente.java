import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.*;
import javax.imageio.*;






class Conexao extends Thread {
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
            System.out.println("Erro na conexao cliente");        }
    }
    public Carro getCar() throws Exception {
        try {
            
            Carro carro = (Carro) objectIn.readObject();
            return carro;
        }catch (Exception e) {
            System.out.println("Fechou a conexão do cliente");
            socket.close();
            throw e;
        }
    }
    private class KeySender extends Thread {
        private char command;

        public KeySender(char command) {
            this.command = command;
        }

        public void run() {
            try {
                writer.write(command);
                writer.flush();
            } catch (IOException e) {
                System.out.println("erro no keysender");
            }
        }
    }

    public void sendL() {
        new KeySender('L').start();
    }
    public void sendR() {
        new KeySender('R').start();
    }
    public void sendSlow() {
        new KeySender('S').start();
    }
    public void sendQuick() {
        new KeySender('Q').start();
    }
    public void sendVolta() {
        new KeySender('V').start();
    }
    public void sendT() {
        new KeySender('T').start();
    }
    public void sendB() {
        new KeySender('B').start();
    }
    public void sendE() {
        new KeySender('E').start();
    }
    public void sendD() {
        new KeySender('D').start();
    }
}

class ClienteReceive extends Thread {
    Carro[] carro;
    Conexao conexao;
    
    ClienteReceive(Carro[] carro, Conexao conexao){
        this.carro = carro;
        this.conexao = conexao;
    }

    public void run() {
        while (true) {
            try {
                Carro carro1 = conexao.getCar();
                Carro carro2 = conexao.getCar();

                carro[0] = new Carro(carro1);
                carro[1] = new Carro(carro2);
                

            } catch (Exception e) {
                break;
            }
        }
    }
}

class GameLoop extends Thread{
    JogoBase jb;
    GameLoop(JogoBase jb){
        this.jb = jb;
        System.out.println("gameloop iniciado");
    }
        
    public void run() {
        while (true) {
            if (jb.carro[0].lap == 5) {
                JOptionPane.showMessageDialog(jb.painel, "Azul ganhou");
                jb.conexao.sendVolta();
                System.exit(0);
            }
            else if(jb.carro[1].lap == 5){
                JOptionPane.showMessageDialog(jb.painel, "Vermelho ganhou");
                jb.conexao.sendVolta();
                System.exit(0);
            }
            jb.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}

class JogoBase extends JFrame{
    Image img[] = new Image[3];
    JPanel painel;
    Conexao conexao;
    Carro carro[] = new Carro[2];

    JogoBase() {
        carro[0] = new Carro(30, 10, Math.toRadians(-90), Constants.carVF);
        carro[1] = new Carro(30, 60, Math.toRadians(-90), Constants.carVF);
        conexao = new Conexao();
        ClienteReceive clienteReceive = new ClienteReceive(carro, conexao);
        clienteReceive.start();
        new GameLoop(this).start();
        try {
            img[0] = ImageIO.read(new File("../sprites/BlueCar.png"));
            img[1] = ImageIO.read(new File("../sprites/RedCar.png"));
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
                try{
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
                    
                    

                    g2d.rotate(Math.toRadians(90), chegada.x + chegada.width / 2, chegada.y + chegada.height / 2);
                    g2d.drawImage(img[2], chegada.x - 50, chegada.y, chegada.width + 100, chegada.height, this);
                    g2d.rotate(-Math.toRadians(90), chegada.x + chegada.width / 2, chegada.y + chegada.height / 2);


                    g2d.rotate(carro[0].angulo, (int)carro[0].x + Constants.carWidth/2, (int)carro[0].y + Constants.carHeight/2);
                    g2d.drawImage(img[0], (int)carro[0].x, (int)carro[0].y, Constants.carWidth, Constants.carHeight, this);
                    g2d.rotate(-carro[0].angulo, (int)carro[0].x + Constants.carWidth/2, (int)carro[0].y + Constants.carHeight/2);

                    g2d.rotate(carro[1].angulo, (int)carro[1].x + Constants.carWidth/2, (int)carro[1].y + Constants.carHeight/2);
                    g2d.drawImage(img[1], (int)carro[1].x, (int)carro[1].y, Constants.carWidth, Constants.carHeight, this);
                    g2d.rotate(-carro[1].angulo, (int)carro[1].x + Constants.carWidth/2, (int)carro[1].y + Constants.carHeight/2);


                    Toolkit.getDefaultToolkit().sync();
                }catch(Exception e){
                    System.out.println("fodase");
                }
                
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

        
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(painel);
        pack();
        setVisible(true);
        
    }
}

class Cliente{
    public static void main(String[] str){
        new JogoBase();
    }
}

