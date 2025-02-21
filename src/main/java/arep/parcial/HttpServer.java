package arep.parcial;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleRequest(clientSocket)).start();
        }

    }

    public static void handleRequest(Socket clientSocket){
        try(PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));){

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (inputLine.startsWith("GET /compreflex?comando=")) {
                    String comando = inputLine.split("=")[1].split(" ")[0];
                    String respuesta = ejecutarComando(comando);
                    String httpResponse = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: application/json\r\n"
                            + "Access-Control-Allow-Origin: *\r\n"
                            + "Content-Length: " + respuesta.length() + "\r\n"
                            + "\r\n"
                            + respuesta;
                    out.println(httpResponse);
                    break;
                }
            }

        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public static String ejecutarComando(String comando) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String[] parts = comando.substring(0,comando.length()-1).split("\\(");
        String functionName = parts[0].trim();
        double[] params = new double[parts.length-1];
        for (int i =1; i< parts.length; i++){
            params[i-1] = Double.parseDouble(parts[i]);
        }
        return mathOperation(functionName, params);
    }

    // sirve con uno y dos parámetros :)
    private static String mathOperation(String functionName, double[] params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<Math> classMath = Math.class;
        Method method;
        if (params.length == 1){
            method = classMath.getMethod(functionName, double.class);
            double respuesta = (double) method.invoke(null, params[0]);
            return "{ \"state\" : \"success\", \"data\" : \"" + respuesta + "\"}";
        } else if (params.length == 2) {
            method = classMath.getMethod(functionName, double.class, double.class);
            double respuesta = (double) method.invoke(null, params[0], params[1]);
            return "{ \"state\" : \"success\", \"data\" : \"" + respuesta + "\"}";
        }
        return "{ \"state\" : \"error\", \"data\": \"Parametros incorrectos\" \"}";
    }

    private static Integer[] bubbleSort(Integer[] lista){
        for (int i = lista.length; i>0; i++){
            for (int j = 0; j < lista.length -1; j++){
                if (lista[j] > lista[j+1]) {
                    int actual = lista[j];
                    lista[j] = lista[j+1];
                    lista[j+1] = actual;
                }
            }
        }
        return lista;
    }
}