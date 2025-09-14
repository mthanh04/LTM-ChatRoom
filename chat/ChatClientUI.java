package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatClientUI extends JFrame {
	private final JTextPane chatArea;
	private final JTextField inputField;
	private final JButton sendButton;
	private final JButton leaveButton;
	private ChatClient client;
	private String username;

	private final File historyFile = new File("chat.txt");

	// Map username -> Color
	private final Map<String, Color> userColors = new HashMap<>();
	private final Random random = new Random();

	// Bảng màu định sẵn
	private final Color[] palette = { new Color(244, 67, 54), // đỏ
			new Color(33, 150, 243), // xanh dương
			new Color(76, 175, 80), // xanh lá
			new Color(255, 152, 0), // cam
			new Color(156, 39, 176), // tím
			new Color(0, 188, 212), // cyan
			new Color(205, 220, 57), // lime
			new Color(121, 85, 72) // nâu
	};

	public ChatClientUI(ChatInterface server) {
		setTitle("💬 Group Chat IT");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		// Chat area
		chatArea = new JTextPane();
		chatArea.setEditable(false);
		chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
		chatArea.setBackground(Color.WHITE);
		chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JScrollPane scrollPane = new JScrollPane(chatArea);

		// Input field
		inputField = new JTextField();
		inputField.setFont(new Font("Arial", Font.PLAIN, 14));
		inputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Send button
		sendButton = new JButton("Send");
		styleSendButton(sendButton);

		// Leave button
		leaveButton = new JButton("Leave");
		styleLeaveButton(leaveButton);

		leaveButton.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn rời phòng chat?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				if (client != null) {
					client.sendMessage("[System] " + username + " đã rời phòng chat");
					client.disconnect();
				}
				dispose(); // đóng cửa sổ
			}
		});

		// Panel cho input và button
		JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
		inputPanel.setBackground(new Color(245, 245, 245));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
		buttonPanel.add(sendButton, BorderLayout.CENTER);
		buttonPanel.add(leaveButton, BorderLayout.EAST);

		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(buttonPanel, BorderLayout.EAST);

		add(scrollPane, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);

		setVisible(true);

		// Nhập tên
		username = JOptionPane.showInputDialog(this, "Nhập tên của bạn:");
		if (username == null || username.trim().isEmpty()) {
			username = "User" + System.currentTimeMillis();
		}

		try {
			client = new ChatClient(server, this, username);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Load lịch sử (chỉ hiển thị, không gửi lại server)
		loadChatHistory();

		sendButton.addActionListener(e -> sendMessage());
		inputField.addActionListener(e -> sendMessage());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (client != null) {
					client.sendMessage("[System] " + username + " đã rời phòng chat");
					client.disconnect();
				}
			}
		});
	}

	/** Gửi tin nhắn từ input */
	private void sendMessage() {
		String message = inputField.getText().trim();
		if (!message.isEmpty()) {
			client.sendMessage(message);
			inputField.setText("");
		}
	}

	/** Hiển thị tin nhắn chat */
	public void showMessage(String sender, String message) {
		SwingUtilities.invokeLater(() -> {
			Color color = getUserColor(sender);
			appendColoredText(sender + ": " + message + "\n", color, false);
			saveMessageToFile(sender + ": " + message);
		});
	}

	/** Hiển thị tin nhắn hệ thống (tham gia, rời phòng...) */
	public void showSystemMessage(String message) {
		SwingUtilities.invokeLater(() -> {
			appendColoredText("[System] " + message + "\n", Color.GRAY, true);
			saveMessageToFile("[System] " + message);
		});
	}

	private void appendColoredText(String text, Color color, boolean italic) {
		SwingUtilities.invokeLater(() -> {
			StyledDocument doc = chatArea.getStyledDocument();
			SimpleAttributeSet style = new SimpleAttributeSet();
			StyleConstants.setForeground(style, color);
			StyleConstants.setItalic(style, italic);

			try {
				doc.insertString(doc.getLength(), text, style);
				chatArea.setCaretPosition(doc.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}

	/** Lưu log vào file */
	private void saveMessageToFile(String message) {
		try (FileWriter writer = new FileWriter(historyFile, true)) {
			writer.write(message + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Load lịch sử chat (chỉ hiển thị, không broadcast lại) */
	private void loadChatHistory() {
		if (!historyFile.exists())
			return;

		try (Scanner scanner = new Scanner(historyFile)) {
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine()).append("\n");
			}
			if (sb.length() > 0) {
				appendColoredText(sb.toString(), Color.DARK_GRAY, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Trả về màu của user (cố định 1 màu) */
	private Color getUserColor(String user) {
		return userColors.computeIfAbsent(user, k -> palette[random.nextInt(palette.length)]);
	}

	/** Style nút Send */
	private void styleSendButton(JButton button) {
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setBackground(new Color(33, 150, 243));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(30, 136, 229));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(33, 150, 243));
			}
		});
	}

	private void styleLeaveButton(JButton button) {
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setBackground(new Color(244, 67, 54));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(211, 47, 47));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(244, 67, 54));
			}
		});
	}
}
