package sealion.server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.UIManager;
import javax.swing.JScrollPane;

import sealion.server.util.Logger;

import java.awt.Toolkit;

public class Launcher {

	private static JTextField commandLine;
	private static JTextArea output;
	private static JLabel connectionCount;
	private static NewUser newUserWindow;

	public static void show(String message) {
		output.append(message);
	}

	public static void updateConnectionCount(int num) {
		connectionCount.setText("Users: " + num);
	}

	public Server server;

	private JFrame frmLionseServer;
	private JTextField commandLineTextField;
	private JButton serverStartButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Launcher window = new Launcher();
					window.frmLionseServer.setVisible(true);

					Logger.initialize();
					window.server = new Server("server.config");
					window.newUserWindow.server = window.server;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Launcher() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		frmLionseServer = new JFrame();
		frmLionseServer.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Launcher.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		frmLionseServer.setResizable(false);
		frmLionseServer.setTitle("Sealion 2.0");
		frmLionseServer.setBounds(100, 100, 600, 500);
		frmLionseServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLionseServer.getContentPane().setLayout(null);

		newUserWindow = new NewUser();

		serverStartButton = new JButton("서버 시작");
		serverStartButton.setFont(UIManager.getFont("Label.font"));
		serverStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// 대기 상태일 경우
				if (server.status == Server.Status.PENDING) {

					server.start();

					serverStartButton.setText("실행 중...");
					serverStartButton.setEnabled(false);

					JOptionPane.showMessageDialog(frmLionseServer, "서버가 " + server.config.port + "번 포트에서 실행되었습니다.",
							"서버 시작", JOptionPane.INFORMATION_MESSAGE);

				}
			}
		});

		serverStartButton.setBounds(10, 10, 97, 28);
		frmLionseServer.getContentPane().add(serverStartButton);

		JLabel totalConnectionLabel = new JLabel("접속자 수: 312");
		totalConnectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalConnectionLabel.setBounds(485, 17, 97, 15);
		frmLionseServer.getContentPane().add(totalConnectionLabel);

		connectionCount = totalConnectionLabel;

		commandLineTextField = new JTextField();
		commandLineTextField.setBounds(10, 440, 459, 21);
		frmLionseServer.getContentPane().add(commandLineTextField);
		commandLineTextField.setColumns(10);

		JButton executeButton = new JButton("명령 실행");
		executeButton.setFont(UIManager.getFont("Label.font"));
		executeButton.setBounds(485, 439, 97, 23);
		frmLionseServer.getContentPane().add(executeButton);

		JButton dataUploadButton = new JButton("데이터 업로드");
		dataUploadButton.setFont(UIManager.getFont("Label.font"));
		dataUploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server.status != Server.Status.RUNNING) {
					JOptionPane.showMessageDialog(frmLionseServer, "서버를 먼저 실행하세요.", "실행 오류", JOptionPane.ERROR_MESSAGE);
					return;
				}

				server.update();

				JOptionPane.showMessageDialog(frmLionseServer, "접속중인 모든 유저 정보를 데이터베이스로 업로드했습니다.", "데이터 업로드",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		dataUploadButton.setBounds(119, 10, 119, 28);
		frmLionseServer.getContentPane().add(dataUploadButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 48, 572, 382);
		frmLionseServer.getContentPane().add(scrollPane);

		JTextArea outputTextArea = new JTextArea();
		scrollPane.setViewportView(outputTextArea);
		outputTextArea.setFont(UIManager.getFont("Label.font"));
		outputTextArea.setEditable(false);

		frmLionseServer.setTitle("Sealion 2.0 - " + Server.getIP());

		output = outputTextArea;

		JButton addNewUserButton = new JButton("새 유저 추가");
		addNewUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server.status != Server.Status.RUNNING) {
					JOptionPane.showMessageDialog(frmLionseServer, "서버를 먼저 실행하세요.", "실행 오류", JOptionPane.ERROR_MESSAGE);
					return;
				}

				newUserWindow.setVisible(true);
			}
		});
		addNewUserButton.setBounds(250, 10, 113, 28);
		frmLionseServer.getContentPane().add(addNewUserButton);
	}
}
