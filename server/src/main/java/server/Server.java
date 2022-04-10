package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    private final static Logger logger = Logger.getLogger(Server.class.getName());

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        if (!DataBaseAuthService.connect()) {
            throw new RuntimeException("База данных не найдена");
        }

        authService = new DataBaseAuthService();

        try {
            server = new ServerSocket(PORT);
            logger.info("Сервер запущен");

            while (true) {
                socket = server.accept();
                logger.info("Клиент присоединился");
                new ClientHandler(this, socket);
            }


        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            DataBaseAuthService.disconnect();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    public void broadcastClientList() {
        StringBuilder stringBuilder = new StringBuilder("/clientlist");

        for (ClientHandler client : clients) {
            stringBuilder.append(" ").append(client.getNickname());
        }

        String msg = stringBuilder.toString();

        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);

        for (ClientHandler client : clients) {
            if (client.getNickname().equals(receiver)) {
                client.sendMsg(message);
                if (!sender.getNickname().equals(receiver)) {
                    sender.sendMsg(message);
                }
                return;
            }
        }
        sender.sendMsg("Не найден пользователь" + receiver);
    }

    public boolean isLoginAuthenticated(String login) {
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }
}
