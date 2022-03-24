package server;

import java.sql.*;

public class DataBaseAuthService implements AuthService {
    private static Connection connection;
    private static PreparedStatement prepareGetNickname;
    private static PreparedStatement prepareRegistration;

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            prepareGetNickname = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?;");
            prepareRegistration = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        String nickname = null;
        try {
            prepareGetNickname.setString(1, login);
            prepareGetNickname.setString(2, password);

            ResultSet resultSet = prepareGetNickname.executeQuery();
            if (resultSet.next()) {
                nickname = resultSet.getString(1);
            }
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            prepareRegistration.setString(1, login);
            prepareRegistration.setString(2, password);
            prepareRegistration.setString(3, nickname);
            prepareRegistration.executeUpdate();
            return  true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void disconnect() {
        try {
            prepareGetNickname.close();
            prepareRegistration.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
