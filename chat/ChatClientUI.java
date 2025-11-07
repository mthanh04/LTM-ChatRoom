package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Giao diện chat nhóm (UI) - bố cục đẹp, tên bên trái tin nhắn, nút bo góc hiện
 * đại
 */
public class ChatClientUI extends JFrame {
	private final JPanel chatPanel;
	private final JScrollPane scrollPane;
	private final JTextField inputField;
	private final JButton sendButton, leaveButton, kickButton, muteButton;
	private final JList<String> clientList;
	private final DefaultListModel<String> clientListModel;

	private ChatClient client;
	private String username;
	private boolean isMuted = false;

	private final File historyFile = new File("chat_history.txt");
	private final Map<String, Color> userColors = new HashMap<>();
	private final List<Color> usedColors = new ArrayList<>();
	private final Random random = new Random();
	private final Set<String> mutedUsers = new HashSet<>();

	// Bảng màu pastel dịu
	private final Color[] palette = { new Color(220, 248, 198), new Color(194, 230, 255), new Color(255, 229, 180),
			new Color(255, 204, 229), new Color(230, 230, 250), new Color(255, 255, 204), new Color(204, 255, 229),
			new Color(255, 240, 200) };

	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public ChatClientUI(ChatInterface server) {
		setTitle("💬 Group Chat IT");
		setSize(900, 580);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		// ===== KHU VỰC CHAT =====
		chatPanel = new JPanel();
		chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
		chatPanel.setBackground(Color.WHITE);

		scrollPane = new JScrollPane(chatPanel);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		// ===== KHU VỰC NHẬP =====
		inputField = new JTextField();
		inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		inputField.setBorder(new RoundedCornerBorder(18));

		sendButton = createRoundedButton("Send", new Color(46, 204, 113));
		muteButton = createRoundedButton("Mute", new Color(52, 152, 219));
		kickButton = createRoundedButton("Kick", new Color(243, 156, 18));
		leaveButton = createRoundedButton("Leave", new Color(231, 76, 60));

		kickButton.setVisible(false);
		muteButton.setVisible(false);

		JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
		inputPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		inputPanel.add(inputField, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setOpaque(false);
		buttonPanel.add(sendButton);
		buttonPanel.add(muteButton);
		buttonPanel.add(kickButton);
		buttonPanel.add(leaveButton);
		inputPanel.add(buttonPanel, BorderLayout.EAST);

		// ===== DANH SÁCH THÀNH VIÊN =====
		clientListModel = new DefaultListModel<>();
		clientList = new JList<>(clientListModel);
		clientList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		clientList.setSelectionBackground(new Color(200, 230, 250));
		clientList.setBorder(BorderFactory.createTitledBorder("👥 Thành viên"));
		JScrollPane listScroll = new JScrollPane(clientList);
		listScroll.setPreferredSize(new Dimension(180, 0));

		// ===== BỐ CỤC CHÍNH =====
		add(scrollPane, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		add(listScroll, BorderLayout.EAST);
		setVisible(true);

		// ===== ĐẶT TÊN NGƯỜI DÙNG =====
		username = JOptionPane.showInputDialog(this, "Nhập tên của bạn:");
		if (username == null || username.trim().isEmpty()) {
			username = "User" + System.currentTimeMillis();
		}

		try {
			client = new ChatClient(server, this, username);
			if (username.equals(client.getAdminName())) {
				kickButton.setVisible(true);
				muteButton.setVisible(true);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		loadChatHistory();

		// ===== SỰ KIỆN =====
		sendButton.addActionListener(e -> sendMessage());
		inputField.addActionListener(e -> sendMessage());

		leaveButton.addActionListener(e -> {
			if (client != null)
				client.disconnect();
			dispose();
		});

		kickButton.addActionListener(e -> handleKickUser());
		muteButton.addActionListener(e -> handleMuteUser());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (client != null)
					client.disconnect();
			}
		});
	}

	private JButton createRoundedButton(String text, Color bgColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Vẽ nền bo góc
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

				super.paintComponent(g);
				g2.dispose();
			}

			@Override
			public void setContentAreaFilled(boolean b) {
			}
		};

		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBackground(bgColor);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(95, 40));

		button.setBorderPainted(false); // ✅ TẮT BORDER – NGĂN LỖI VIỀN TRẮNG
		button.setOpaque(false); // ✅ ĐỂ PAINTCUSTOM LÀM CHỦ

		// Hover
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(bgColor.brighter());
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(bgColor);
			}
		});

		return button;
	}

	// ===== BORDER BO GÓC CHUẨN =====
	class RoundedCornerBorder extends EmptyBorder {

		private final int radius;

		public RoundedCornerBorder(int radius) {
			super(8, 12, 8, 12);
			this.radius = radius;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(new Color(200, 200, 200)); // viền xám nhạt
			g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

			g2.dispose();
		}
	}

	// ====== GỬI TIN NHẮN ======
	private void sendMessage() {
		if (isMuted) {
			JOptionPane.showMessageDialog(this, "Bạn đang bị cấm chat bởi admin!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String message = inputField.getText().trim();
		if (!message.isEmpty()) {
			client.sendMessage(message);
			inputField.setText("");
		}
	}

	// ====== KICK NGƯỜI DÙNG ======
	private void handleKickUser() {
		String target = clientList.getSelectedValue();
		if (target == null) {
			JOptionPane.showMessageDialog(this, "Hãy chọn người cần kick!");
			return;
		}
		target = target.replace(" (Admin)", "").trim();
		if (target.equals(client.getAdminName()) || target.equals(username)) {
			JOptionPane.showMessageDialog(this, "Không thể kick Admin hoặc chính bạn!");
			return;
		}
		client.kickUser(target);
	}

	// ====== MUTE / UNMUTE ======
	private void handleMuteUser() {
		String target = clientList.getSelectedValue();
		if (target == null) {
			JOptionPane.showMessageDialog(this, "Hãy chọn người cần cấm/mở chat!");
			return;
		}
		target = target.replace(" (Admin)", "").trim();
		if (target.equals(username) || target.equals(client.getAdminName())) {
			JOptionPane.showMessageDialog(this, "Không thể cấm Admin hoặc chính bạn!");
			return;
		}

		if (mutedUsers.contains(target)) {
			mutedUsers.remove(target);
			client.sendMessage("[System] " + target + " đã được mở chat trở lại");
		} else {
			mutedUsers.add(target);
			client.sendMessage("[System] " + target + " đã bị cấm chat");
		}
	}

	// ====== HIỂN THỊ TIN NHẮN ======
	public void showMessage(String sender, String message) {
		if (message.contains("bị cấm chat") && message.contains(username))
			setMuted(true);
		else if (message.contains("mở chat") && message.contains(username))
			setMuted(false);

		SwingUtilities.invokeLater(() -> {
			Color color = getUserColor(sender);
			addMessageBubble(sender, message, color, sender.equals(username));
			saveMessageToFile(sender + ": " + message);
		});
	}

	public void showSystemMessage(String message) {
		SwingUtilities.invokeLater(() -> {
			addSystemMessage(message);
			saveMessageToFile("[System] " + message);
		});
	}

	public void showKickDialog(String reason) {
		JOptionPane.showMessageDialog(this, reason, "Bạn bị kick", JOptionPane.WARNING_MESSAGE);
		dispose();
	}

	public void updateClientList(String[] clients) {
		SwingUtilities.invokeLater(() -> {
			clientListModel.clear();
			String admin = client.getAdminName();
			for (String c : clients) {
				clientListModel.addElement(c.equals(admin) ? c + " (Admin)" : c);
			}
		});
	}

	public void setMuted(boolean muted) {
		this.isMuted = muted;
		inputField.setEnabled(!muted);
		sendButton.setEnabled(!muted);
		showSystemMessage(muted ? "[System] Bạn đã bị admin cấm chat." : "[System] Bạn đã được bật chat trở lại.");
	}

	// ====== HIỂN THỊ BONG BÓNG CHAT ======
	private void addMessageBubble(String sender, String message, Color color, boolean isSelf) {

		JPanel wrapper = new JPanel(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
		wrapper.setBackground(Color.WHITE);

		// ===== Tên người gửi =====
		if (!isSelf) {
			JLabel nameLabel = new JLabel(sender);
			nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
			nameLabel.setForeground(new Color(60, 60, 60));

			JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
			namePanel.setOpaque(false);
			namePanel.add(nameLabel);
			chatPanel.add(namePanel);
		}

		// ===== Bong bóng =====
		JPanel bubble = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(color);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
			}
		};

		bubble.setLayout(new BorderLayout());
		bubble.setOpaque(false);
		bubble.setBorder(new EmptyBorder(10, 15, 10, 15));

		// ===== Nội dung tin nhắn =====
		JTextArea msg = new JTextArea(message);
		msg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		msg.setLineWrap(true);
		msg.setWrapStyleWord(true);
		msg.setOpaque(false);
		msg.setEditable(false);

		// ✅ Tự động đổi màu chữ dựa trên độ sáng của màu bong bóng
		int luminance = (int) ((0.299 * color.getRed()) + (0.587 * color.getGreen()) + (0.114 * color.getBlue()));

		if (luminance > 180) {
			msg.setForeground(Color.BLACK); // nền sáng → dùng chữ đen
		} else {
			msg.setForeground(Color.WHITE); // nền đậm → dùng chữ trắng
		}

		bubble.add(msg, BorderLayout.CENTER);
		wrapper.add(bubble);

		chatPanel.add(wrapper);
		chatPanel.revalidate();

		SwingUtilities.invokeLater(
				() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
	}

	private void addSystemMessage(String message) {
		JLabel sysLabel = new JLabel(message, SwingConstants.CENTER);
		sysLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		sysLabel.setForeground(Color.GRAY);
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		wrapper.setBackground(Color.WHITE);
		wrapper.add(sysLabel);
		chatPanel.add(wrapper);
		chatPanel.revalidate();
		chatPanel.repaint();
	}

	// ====== LƯU & TẢI LỊCH SỬ ======
	private void saveMessageToFile(String message) {
		try (FileWriter writer = new FileWriter(historyFile, true)) {
			writer.write(message + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadChatHistory() {
		if (!historyFile.exists())
			return;
		try (Scanner scanner = new Scanner(historyFile)) {
			while (scanner.hasNextLine()) {
				addSystemMessage(scanner.nextLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Color getUserColor(String user) {
		if (userColors.containsKey(user))
			return userColors.get(user);
		for (Color c : palette) {
			if (!usedColors.contains(c)) {
				usedColors.add(c);
				userColors.put(user, c);
				return c;
			}
		}
		Color randomColor = new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));
		userColors.put(user, randomColor);
		return randomColor;
	}

	// ====== BORDER BO TRÒN ======
	private static class RoundedBorder extends LineBorder {
		private final int arc;

		public RoundedBorder(int radius, Color color) {
			super(color, 1, true);
			this.arc = radius;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(8, 8, 8, 8);
		}
	}
}
