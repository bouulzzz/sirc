import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ClienteGUI extends JFrame {

    private JTextField nomeField, cpfField, enderecoField, telefoneField, buscaField;
    private JTextArea resultadoArea;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public ClienteGUI() {
        setTitle("Cadastro de Clientes");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Campos de cadastro
        nomeField = new JTextField(30);
        cpfField = new JTextField(15);
        enderecoField = new JTextField(30);
        telefoneField = new JTextField(15);

        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new CadastrarListener());

        JLabel buscaLabel = new JLabel("Buscar Cliente (Nome):");
        buscaField = new JTextField(20);
        JButton buscarButton = new JButton("Buscar");
        buscarButton.addActionListener(new BuscarListener());

        resultadoArea = new JTextArea(10, 30);
        resultadoArea.setEditable(false);

        // Adicionando componentes à interface
        add(new JLabel("Nome:"));
        add(nomeField);
        add(new JLabel("CPF:"));
        add(cpfField);
        add(new JLabel("Endereço:"));
        add(enderecoField);
        add(new JLabel("Telefone:"));
        add(telefoneField);
        add(cadastrarButton);
        add(buscaLabel);
        add(buscaField);
        add(buscarButton);
        add(new JScrollPane(resultadoArea));

        try {
            socket = new Socket("localhost", 12345);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Listener para cadastrar cliente
    private class CadastrarListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String nome = nomeField.getText();
                String cpf = cpfField.getText();
                String endereco = enderecoField.getText();
                String telefone = telefoneField.getText();

                output.println("CADASTRAR");
                output.println(nome);
                output.println(cpf);
                output.println(endereco);
                output.println(telefone);

                String response = input.readLine();
                JOptionPane.showMessageDialog(null, response);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Listener para buscar cliente
    private class BuscarListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String nomeConsulta = buscaField.getText();
                output.println("CONSULTAR");
                output.println(nomeConsulta);

                String response = input.readLine();
                resultadoArea.setText(response);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClienteGUI().setVisible(true);
        });
    }
}
