package arep.parcial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class FacadeServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleRequest(clientSocket)).start();
        }
    }

    public static void handleRequest(Socket clientSocket) {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (inputLine.startsWith("GET /calculadora")) {
                    String htmlResponse = getClientHTML();
                    String httpResponse = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/html\r\n"
                            + "Content-Length: " + htmlResponse.length() + "\r\n"
                            + "\r\n"
                            + htmlResponse;
                    out.println(httpResponse);
                    break;
                } else if (inputLine.startsWith("GET /compreflex?comando=")) {
                    String comando = inputLine.split("=")[1].split(" ")[0];
                    String response = redireccionServer(comando);
                    String httpResponse = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: application/json\r\n"
                            + "Content-Length: " + response.length() + "\r\n"
                            + "\r\n"
                            + response;
                    out.println(httpResponse);
                    break;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String getClientHTML() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Form Example</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Operation</h1>\n" +
                "<form>\n" +
                "    <label>Name:</label><br>\n" +
                "    <input type=\"text\" id=\"funcion\"><br><br>\n" +
                "    <input type=\"button\" value=\"Submit\" onclick=\"enviarFuncion()\">\n" +
                "</form>\n" +
                "\n" +
                "<div id=\"respuesta\"></div>\n" +
                "\n" +
                "<script>\n" +
                "    function enviarFuncion(){\n" +
                "        const funcion = document.getElementById('funcion').value\n" +
                "        let url = \"http://localhost:36000/compreflex?comando=\" + funcion;\n" +
                "\n" +
                "        fetch (url)\n" +
                "            .then(response => {\n" +
                "                return response.json();\n" +
                "            })\n" +
                "            .then(data => { document.getElementById('respuesta').innerHTML = JSON.stringify(data, null, 2)\n" +
                "            });\n" +
                "    }\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }
    private static String redireccionServer(String comando) throws IOException {
        System.out.println("COMANDOOOO" + comando);
        Socket socket = new Socket("localhost",36000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String res = "GET /compreflex?comando=" + comando + "HTTP/1.1\r\n Host:localhost\r\n\r\n";
        out.println(res);
        StringBuilder response = new StringBuilder();
        String line;

        while((line=in.readLine()) != null){
            response.append(line);
        }
        return response.toString().split("\r\n\r\n")[1];
    }
}
