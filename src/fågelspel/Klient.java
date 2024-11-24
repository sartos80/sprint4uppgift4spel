package fågelspel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class Klient extends JFrame implements ActionListener {
    private JPanel p = new JPanel();
    private JLabel fråga = new JLabel("Vad är huvudstad i Sverige?");
    private JRadioButton stockholm = new JRadioButton("Stockholm", false);
    private JRadioButton oslo = new JRadioButton("Oslo", false);
    private JRadioButton kopenhamn = new JRadioButton("Köpenhamn", false);
    private JRadioButton helsingfors = new JRadioButton("Helsingfors", false);
    private ButtonGroup group = new ButtonGroup();

    private String fromServer;
    private String correctAnswer;
    private String fromUser = "";  // Användarens val

    public Klient() {
//Skapar en socketanslutning till servern som kör på 127.0.0.1 (localhost) på port 55555

        try (Socket socket = new Socket("127.0.0.1", 55555);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);//Används för att skicka data till servern
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){ // används för att läsa data från servern

            // Skapa grafiskt gränssnitt
            p.setLayout(new GridLayout(5, 1));
            fråga.setFont(new Font("Arial", Font.BOLD, 20));
            fråga.setForeground(Color.red);
            p.add(fråga);
            p.add(stockholm);
            p.add(oslo);
            p.add(kopenhamn);
            p.add(helsingfors);
            setTitle("Frågespel");
            this.add(p);

            // Lägg till ActionListener för varje radioknapp
            stockholm.addActionListener(this);
            oslo.addActionListener(this);
            kopenhamn.addActionListener(this);
            helsingfors.addActionListener(this);

            // Gruppera radioknapparna för att endast ett kan väljas
            group.add(stockholm);
            group.add(oslo);
            group.add(kopenhamn);
            group.add(helsingfors);

            // Visa fönstret
            pack();
            setVisible(true);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Ta emot fråga från servern och sätt frågetexten
            fromServer = in.readLine();
            fråga.setText(fromServer);

            // Ta emot alternativ från servern och uppdatera radioknapparna
            String options = in.readLine();
            String[] answers = options.split(",");

            if (answers.length > 0) stockholm.setText(answers[0]);
            if (answers.length > 1) oslo.setText(answers[1]);
            if (answers.length > 2) kopenhamn.setText(answers[2]);
            if (answers.length > 3) helsingfors.setText(answers[3]);

            // Ta emot korrekt svar från servern (används ej för klienten, men tas emot)
            correctAnswer = in.readLine();

            // Vänta på användarens svar i 10 sekunder
            Thread.sleep(10000);  // Vänta 10 sekunder

            // Skicka användarens val till servern
            out.println(fromUser);

            // Ta emot resultat från servern och visa det
            String result = in.readLine();
            System.out.println("Mottaget resultat: " + result);

            // Visa resultatet i en popup-dialog
            JOptionPane.showMessageDialog(null, result);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ActionListener för radioknappar
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stockholm) {
            fromUser = "stockholm";  // Stockholm
        } else if (e.getSource() == oslo) {
            fromUser = "oslo";  // Oslo
        } else if (e.getSource() == kopenhamn) {
            fromUser = "kopenhamn";  // Köpenhamn
        } else if (e.getSource() == helsingfors) {
            fromUser = "helsingfors";  // Helsingfors
        }
    }

    public static void main(String[] args) {
        new Klient();  // Skapa och starta klienten
    }
}