import java.io.*;
import java.net.*;
import java.sql.*;

public class ServidorClientes {

    private static final String URL = "jdbc:mysql://localhost:3306/LojaClientes";
    private static final String USER = "root";
    private static final String PASSWORD = "sua_senha";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado. Aguardando conexões...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Cria nova thread para lidar com o cliente
                new Thread(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException, SQLException {
        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)
        ) {
            String command = input.readLine();
            
            if (command.equalsIgnoreCase("CADASTRAR")) {
                String nome = input.readLine();
                String cpf = input.readLine();
                String endereco = input.readLine();
                String telefone = input.readLine();

                String sql = "INSERT INTO Cliente (nome, cpf, endereco, telefone) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, nome);
                    stmt.setString(2, cpf);
                    stmt.setString(3, endereco);
                    stmt.setString(4, telefone);
                    stmt.executeUpdate();
                    output.println("Cadastro realizado com sucesso!");
                }
            } else if (command.equalsIgnoreCase("CONSULTAR")) {
                String nomeConsulta = input.readLine();
                String sql = "SELECT * FROM Cliente WHERE nome LIKE ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, "%" + nomeConsulta + "%");
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        String clienteInfo = String.format("ID: %d | Nome: %s | CPF: %s | Endereço: %s | Telefone: %s",
                                rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                                rs.getString("endereco"), rs.getString("telefone"));
                        output.println(clienteInfo);
                    } else {
                        output.println("Cliente não encontrado.");
                    }
                }
            }
        }
    }
}
