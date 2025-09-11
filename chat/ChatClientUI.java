package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientUI extends JFrame {
	private JTextArea chatArea;
	private JTextField inputField;
	private JButton sendButton;
	private ChatClient client;

	public ChatClientUI(ChatInterface server) {
		setTitle("RMI Chat Client");
		setSize(400, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatArea);

		inputField = new JTextField();
		sendButton = new JButton("Send");

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(inputField, BorderLayout.CENTER);
		panel.add(sendButton, BorderLayout.EAST);

		add(scrollPane, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);

		try {
			client = new ChatClient(server, this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = inputField.getText().trim();
				if (!message.isEmpty()) {
					client.sendMessage(message);
					inputField.setText("");
				}
			}
		});
	}

	public void showMessage(String message) {
		chatArea.append(message + "\n");
	}
}
