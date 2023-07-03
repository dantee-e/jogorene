import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;



class JogoBase extends JFrame {
  Image img[] = new Image[3];
  int lap = 0;
  JPanel painel;
  JogoBase() {
    try {
        img[0] = ImageIO.read(new File("sprites/RedCar.png"));
        img[1] = ImageIO.read(new File("sprites/BlueCar.png"));
        img[2] = ImageIO.read(new File("sprites/FinishLine.png"));
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    setPreferredSize(new Dimension(1200, 800));
    Carro carro  = new Carro(650, 50, 70, 40);
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
        g2d.rotate(carro.theta, (int)carro.x + carro.width/2, (int)carro.y + carro.height/2);
        g2d.drawImage(img[0], (int)carro.x, (int)carro.y, carro.width, carro.height, this);
        g2d.rotate(-carro.theta, (int)carro.x + carro.width/2, (int)carro.y + carro.height/2);
        Toolkit.getDefaultToolkit().sync();
      }
    };

    painel.setFocusable(true);
    painel.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          carro.theta -= Math.toRadians(5);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          carro.theta += Math.toRadians(5);
        } 
      }
    });

    Timer timer = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        carro.setBounds((int)carro.x, (int)carro.y, carro.width, carro.height);
          //logica da velocidade em relacao a pista
          if (carro.intersects(pista)) {
            carro.velocidade = 0.2;
          }
          else {
            carro.velocidade = 4;
          }
          //logica das voltas
          if (lap == 0 && carro.intersects(checkpoint))
            lap = 1;
          System.out.println(lap);
          if (lap == 1 && carro.intersects(chegada)) {
              lap = 2;
          }
          if (lap == 2 && carro.intersects(checkpoint))
            lap = 3;
          if (lap == 3 && carro.intersects(chegada)) {
            JOptionPane.showMessageDialog(painel, "Jogo encerrado");
            System.exit(0);
          }

          //logica de impedir que o carro saia da janela
          if (carro.x < 0) {
              carro.x = 0; // Limite esquerdo da janela
          } else if (carro.x + carro.width > getWidth()) {
              carro.x = getWidth() - carro.width; // Limite direito da janela
          }

          if (carro.y < 0) {
              carro.y = 0; // Limite superior da janela
          } else if (carro.y + carro.height > getHeight()) {
              carro.y = getHeight() - carro.height; // Limite inferior da janela
          }

          //logica da rotacao do carro
            double dx = Math.cos(carro.theta);
            double dy = Math.sin(carro.theta);
            double magnitude = Math.sqrt(dx * dx + dy * dy); // Calcula a magnitude do vetor de velocidade

            if (magnitude != 0.0) {
                dx /= magnitude; // Normaliza a componente X
                dy /= magnitude; // Normaliza a componente Y
            }

            carro.x += carro.velocidade * dx;
            carro.y += carro.velocidade * dy;

            repaint();
        }
    });
    timer.start();

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    add(painel);
    pack();
    setResizable(false);
    setVisible(true);
  }
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JogoBase();
            }
        });
  }
}
