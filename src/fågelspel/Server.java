package fågelspel;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Server extends Thread {
    private Socket player1Socket;
    private Socket player2Socket;

    public Server(Socket player1Socket, Socket player2Socket) {
        this.player1Socket = player1Socket;
        this.player2Socket = player2Socket;
    }

    @Override
    public void run() {
        // Använd try-with-resources för att hantera strömmar och sockets
        try (PrintWriter outPlayer1 = new PrintWriter(player1Socket.getOutputStream(), true);
             BufferedReader inPlayer1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
             PrintWriter outPlayer2 = new PrintWriter(player2Socket.getOutputStream(), true);
             BufferedReader inPlayer2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()))) {

            // Skicka fråga och alternativ till båda spelarna
            String question = "Vad är huvudstaden i Sverige?";
            String options = "Stockholm,Oslo,Köpenhamn,Helsingfors";

            outPlayer1.println(question);
            outPlayer1.println(options);
            outPlayer1.println("stockholm"); // Skicka korrekt svar till servern

            outPlayer2.println(question);
            outPlayer2.println(options);
            outPlayer2.println("stockholm"); // Skicka korrekt svar till servern

            // Vänta på svar från båda spelarna
            String answer1 = inPlayer1.readLine();
            String answer2 = inPlayer2.readLine();

            System.out.println("Player 1 answer: " + answer1);
            System.out.println("Player 2 answer: " + answer2);

            // Jämför svaren och skicka resultatet
            String result = hanteraSvar(answer1, answer2);

            // Skicka resultatet till båda spelarna
            System.out.println("Skickar resultat till Player 1: " + result);
            System.out.println("Skickar resultat till Player 2: " + result);
            outPlayer1.println(result);
            outPlayer2.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  metod för att hantera svaren och returnera resultat
    private String hanteraSvar(String answer1, String answer2) {
        String correctAnswer = "stockholm"; // Korrekt svar är "Stockholm"

        // Kontrollera om båda svaren är null eller tomma
        if ((answer1 == null || answer1.trim().isEmpty()) && (answer2 == null || answer2.trim().isEmpty())) {
            return " ingen av er har svarat på frågan och ingen vinner."; // Båda spelarna svarade inte
        }

        // Om spelare 1 inte svarade
        if (answer1 == null || answer1.trim().isEmpty()) {
            if (answer2.equals(correctAnswer)) {
                return " spelare 1 : du har inte svarat på frågan , spelare 2 : du har svarat rätt svar och du vann . ";
            } else {
                return " spelare 1 : du har inte svarat på frågan , spelare 2 : du har svarat fel svar och ingen av er vinner.";
            }
        }

        // Om spelare 2 inte svarade
        if (answer2 == null || answer2.trim().isEmpty()) {
            if (answer1.equals(correctAnswer)) {
                return "spelare 2 : du har inte svarat på  frågan, spelare 1 : du har svarat rätt svar och du vinner.";
            } else {
                return "spelare 2  : du har inte svarat på frågan, spelare 1 : du har svarat fel svar och ingen av er vinner.";
            }
        }

        // Båda spelarna har svarat, nu kollar vi deras svar
        if (answer1.equals(correctAnswer) && answer2.equals(correctAnswer)) {
            return "ni båda vinner !";
        } else if (answer1.equals(correctAnswer)) {
            return "spelare 1  : du har svarat rätt svar och du vinner!";
        } else if (answer2.equals(correctAnswer)) {
            return "spelare 2 : du har svarat rätt svar och du vinner!";
        } else {
            return " ni båda har svarat fel svar och ingen av er  vinner!";
        }
    }
}