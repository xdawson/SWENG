//import com.oracle.tools.packager.IOUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class WebServer {

    int port = 0;
    int aCount = 0;
    int bCount = 0;
    int cCount = 0;
    int dCount = 0;

    int happyCount = 0;
    int confusedCount = 0;
    int sadCount = 0;

    String question = "";
    String aAnswer = "";
    String bAnswer = "";
    String cAnswer = "";
    String dAnswer = "";

    boolean isQuestion = false;

    String formData = "{\"type\":\"none\"}";

    public WebServer() {
        try {
            port = 80;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("server started at " + port);
            server.createContext("/", new RootHandler());
            server.createContext("/echoHeader", new EchoHeaderHandler());
            server.createContext("/echoGet", new EchoGetHandler());
            server.createContext("/echoPost", new EchoPostHandler());
            server.createContext("/responses", new ResponsesHandler());
            server.createContext("/questions", new QuestionsHandler());
            server.createContext("/answer1Image", new Answer1ImageHandler());
            server.createContext("/answer2Image", new Answer2ImageHandler());
            server.createContext("/answer3Image", new Answer3ImageHandler());
            server.createContext("/answer4Image", new Answer4ImageHandler());
            server.createContext("/happyImage", new HappyImageHandler());
            server.createContext("/confusedImage", new ConfusedImageHandler());
            server.createContext("/sadImage", new SadImageHandler());
            server.createContext("/favicon", new FaviconImageHandler());
            server.createContext("/loading", new LoadingImageHandler());
            server.createContext("/font", new FontHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setQuestion(String question) {this.question = question;}
    public void setAAnswer(String answer) {this.aAnswer = answer;}
    public void setBAnswer(String answer) {this.bAnswer = answer;}
    public void setCAnswer(String answer) {this.cAnswer = answer;}
    public void setDAnswer(String answer) {this.dAnswer = answer;}

    public class RootHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html")));

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public class EchoHeaderHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            Headers headers = he.getRequestHeaders();
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
            String response = "";
            for (Map.Entry<String, List<String>> entry : entries)
                response += entry.toString() + "\n";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public class EchoGetHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            // parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
            parseQuery(query, parameters);

            // send response
            String response = formData;
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());

            os.close();
        }
    }

    public class EchoPostHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            // parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            System.out.println(query);
            switch(query) {
                case "a":
                    aCount++;
                break;
                case "b":
                    bCount++;
                break;
                case "c":
                    cCount++;
                break;
                case "d":
                    dCount++;
                break;
                case "happy":
                    happyCount++;
                break;
                case "confused":
                    confusedCount++;
                break;
                case "sad":
                    sadCount++;
                break;
            }

            parseQuery(query, parameters);

            // send response
            String response = "";
            for (String key : parameters.keySet())
                response += key + " = " + parameters.get(key) + "\n";

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

    public class ResponsesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {

            String response;
            if(isQuestion) {
                response = "a=" + Integer.toString(aCount) +
                           ", b=" +  Integer.toString(bCount) +
                           ", c=" + Integer.toString(cCount) +
                           ", d=" + Integer.toString(dCount);
            } else {
                response = "happy=" + Integer.toString(happyCount) +
                           ", confused=" + Integer.toString(confusedCount) +
                           ", sad=" + Integer.toString(sadCount);
            }

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public class QuestionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            formData = query;

            if(formData.contains("question")) {
                isQuestion = true;
                happyCount = 0;
                confusedCount = 0;
                sadCount= 0;
            } else {
                isQuestion = false;
                aCount = 0;
                bCount = 0;
                cCount = 0;
                dCount = 0;
            }
            System.out.println(query);

            // send response
            String response = "ack";

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    public class Answer1ImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/answer_1.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();

        }
    }
    public class Answer2ImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/answers_2.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class Answer3ImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/answers_3.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class Answer4ImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/answers_4.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class HappyImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/smiling.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class ConfusedImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/confused.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class SadImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/sad.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class FaviconImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/favicon.png"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    public class LoadingImageHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/queston_loading.gif"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    public class FontHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get("../resources/fonts/BebasNeue-Regular.ttf"));
//                String response = new String(Files.readAllBytes(Paths.get("../resources/html_test.html").toURI())));

            he.sendResponseHeaders(200, response.length);
            OutputStream os = he.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
