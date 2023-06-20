import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

/* class JogoBase extends JFrame {
  Image img[] = new Image[3];
  Rectangle rect[] = new rect[3];
  Desenho des = new Desenho();

  class Desenho extends JPanel {

    Desenho() {
      try {
        setPreferredSize(new Dimension(1000, 600));
        img[0] = ImageIO.read(new File("RedCar.png"));
        img[1] = ImageIO.read(new File("BlueCar.png"));
        img[2] = ImageIO.read(new File("FinishLine.png"));
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.BLACK);
      g.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 100, 100);
      g.setColor(Color.LIGHT_GRAY);
      g.fillRoundRect(120, 100, getWidth() - 240, getHeight() - 200, 200, 200);
      g.drawImage(img[0], rect[0].x, rect[0].y, 50, 50, this);
      g.drawImage(img[1], rect[1].x, rect[1].y, 50, 50, this);
      //g.drawImage(img[2], 250, 250, 50, 50, this);
      Toolkit.getDefaultToolkit().sync();
    }
  }

  JogoBase() {
    super("Trabalho");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    add(des);
    pack();
    setVisible(true);
  } */
class JogoBase extends JFrame {
  JPanel painel;
  Rectangle[] rect = new Rectangle[3];
  int velocidade;
  double angulo;
  public JogoBase() {
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
        g.drawRect(rect[0].x, rect[0].y, rect[0].width, rect[0].height);
        //g.drawRect(rect[1].x, rect[1].y, rect[1].width, rect[1].height);
        //g.drawImage(img[2], 250, 250, 50, 50, this);
        Toolkit.getDefaultToolkit().sync();
      }
    };

    velocidade = 2;
    angulo = 0;
    rect[0] = new Rectangle(50, 0, 100, 50);
    rect[1] = new Rectangle(600, 0, 100, 50);
    painel.setFocusable(true);
    painel.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          System.out.println("left"); // Gira para a esquerda
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          System.out.println("right"); // Gira para a direita
        }
      }
    });

    Timer timer = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Atualiza a posição do retângulo com base no ângulo
            rect[0].x += (int) (Math.cos(angulo) * velocidade);
            rect[0].y += (int) (Math.sin(angulo) * velocidade);

            painel.repaint();
        }
    });
    timer.start();

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    add(painel);
    pack();
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