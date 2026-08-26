import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Um Pong simples para um jogador, feito apenas com Swing. */
public class Pong extends JPanel implements ActionListener {
    private static final int LARGURA = 900;
    private static final int ALTURA = 520;
    private static final int LARGURA_RAQUETE = 14;
    private static final int ALTURA_RAQUETE = 92;
    private static final int TAMANHO_BOLA = 16;
    private static final int VELOCIDADE_RAQUETE = 7;

    private final Timer timer = new Timer(10, this);
    private int jogadorY;
    private int computadorY;
    private int bolaX;
    private int bolaY;
    private int velocidadeX;
    private int velocidadeY;
    private int pontosJogador;
    private int pontosComputador;
    private boolean sobe;
    private boolean desce;

    public Pong() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(12, 18, 30));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evento) {
                if (evento.getKeyCode() == KeyEvent.VK_W) sobe = true;
                if (evento.getKeyCode() == KeyEvent.VK_S) desce = true;
                if (evento.getKeyCode() == KeyEvent.VK_SPACE) reiniciarJogo();
            }

            @Override
            public void keyReleased(KeyEvent evento) {
                if (evento.getKeyCode() == KeyEvent.VK_W) sobe = false;
                if (evento.getKeyCode() == KeyEvent.VK_S) desce = false;
            }
        });

        reiniciarJogo();
        timer.start();
    }

    private void reiniciarJogo() {
        pontosJogador = 0;
        pontosComputador = 0;
        jogadorY = (ALTURA - ALTURA_RAQUETE) / 2;
        computadorY = jogadorY;
        colocarBolaNoCentro();
    }

    private void colocarBolaNoCentro() {
        bolaX = (LARGURA - TAMANHO_BOLA) / 2;
        bolaY = (ALTURA - TAMANHO_BOLA) / 2;
        velocidadeX = Math.random() < 0.5 ? 5 : -5;
        velocidadeY = Math.random() < 0.5 ? 3 : -3;
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        atualizarJogador();
        atualizarComputador();
        atualizarBola();
        repaint();
    }

    private void atualizarJogador() {
        if (sobe) jogadorY -= VELOCIDADE_RAQUETE;
        if (desce) jogadorY += VELOCIDADE_RAQUETE;
        jogadorY = limitar(jogadorY, 0, ALTURA - ALTURA_RAQUETE);
    }

    private void atualizarComputador() {
        int meioComputador = computadorY + ALTURA_RAQUETE / 2;
        int meioBola = bolaY + TAMANHO_BOLA / 2;
        if (meioComputador < meioBola - 12) computadorY += 4;
        if (meioComputador > meioBola + 12) computadorY -= 4;
        computadorY = limitar(computadorY, 0, ALTURA - ALTURA_RAQUETE);
    }

    private void atualizarBola() {
        bolaX += velocidadeX;
        bolaY += velocidadeY;

        if (bolaY <= 0 || bolaY + TAMANHO_BOLA >= ALTURA) {
            velocidadeY *= -1;
            bolaY = limitar(bolaY, 0, ALTURA - TAMANHO_BOLA);
        }

        if (colide(30, jogadorY, bolaX, bolaY) && velocidadeX < 0) {
            rebater(30, jogadorY, true);
        }
        if (colide(LARGURA - 30 - LARGURA_RAQUETE, computadorY, bolaX, bolaY) && velocidadeX > 0) {
            rebater(LARGURA - 30 - LARGURA_RAQUETE, computadorY, false);
        }

        if (bolaX < -TAMANHO_BOLA) {
            pontosComputador++;
            colocarBolaNoCentro();
        }
        if (bolaX > LARGURA) {
            pontosJogador++;
            colocarBolaNoCentro();
        }
    }

    private boolean colide(int raqueteX, int raqueteY, int x, int y) {
        return x < raqueteX + LARGURA_RAQUETE && x + TAMANHO_BOLA > raqueteX
                && y < raqueteY + ALTURA_RAQUETE && y + TAMANHO_BOLA > raqueteY;
    }

    private void rebater(int raqueteX, int raqueteY, boolean esquerda) {
        velocidadeX = esquerda ? Math.abs(velocidadeX) + 1 : -Math.abs(velocidadeX) - 1;
        velocidadeX = limitar(velocidadeX, -9, 9);
        int diferenca = (bolaY + TAMANHO_BOLA / 2) - (raqueteY + ALTURA_RAQUETE / 2);
        velocidadeY = limitar(diferenca / 10, -6, 6);
        if (velocidadeY == 0) velocidadeY = Math.random() < 0.5 ? -2 : 2;
        bolaX = esquerda ? raqueteX + LARGURA_RAQUETE : raqueteX - TAMANHO_BOLA;
    }

    private int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(230, 236, 245));
        for (int y = 12; y < ALTURA; y += 28) g.fillRect(LARGURA / 2 - 2, y, 4, 16);
        g.fillRect(30, jogadorY, LARGURA_RAQUETE, ALTURA_RAQUETE);
        g.fillRect(LARGURA - 30 - LARGURA_RAQUETE, computadorY, LARGURA_RAQUETE, ALTURA_RAQUETE);
        g.fillOval(bolaX, bolaY, TAMANHO_BOLA, TAMANHO_BOLA);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        g.drawString(String.valueOf(pontosJogador), LARGURA / 2 - 72, 52);
        g.drawString(String.valueOf(pontosComputador), LARGURA / 2 + 50, 52);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        g.drawString("W / S: mover    ESPAÇO: reiniciar", LARGURA / 2 - 145, ALTURA - 18);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Pong - 1 Jogador");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setResizable(false);
            janela.add(new Pong());
            janela.pack();
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
