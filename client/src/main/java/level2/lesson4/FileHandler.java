package level2.lesson4;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileHandler {
    static BufferedWriter writer;

    public static void writeToFile(String msg, String login) throws IOException {
        writer = new BufferedWriter(new FileWriter("history_[" + login + "].txt", true));
        writer.write(msg + "\n");
        writer.close();
    }

    public static String showHistoryMsg(String login) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<String> historyMsg = Files.readAllLines(Paths.get("history_[" + login + "].txt"));
            int counter = 0;
            if (historyMsg.size() > 100) {
                counter = historyMsg.size() - 100;
            }
            for (int i = counter; i < historyMsg.size(); i++) {
                stringBuilder.append(historyMsg.get(i) + "\n");
            }
        } catch (IOException e) {
            System.out.println("История сообщений не найдена или повреждена");
        }
        return stringBuilder.toString();
    }
}
