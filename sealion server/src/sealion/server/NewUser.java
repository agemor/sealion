package sealion.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sealion.server.security.crypto.SHA256;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewUser extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField userNameInput;
	private JTextField passwordInput;
	private JTextField characterNameInput;
	private JTextField emailInput;

	private JDialog self = this;

	public Server server;

	/**
	 * Create the dialog.
	 */
	public NewUser() {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("\uC0C8 \uC720\uC800 \uCD94\uAC00");
		setBounds(100, 100, 304, 199);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JButton checkUserNameButton = new JButton("\uC911\uBCF5 \uD655\uC778");
		checkUserNameButton.setBounds(206, 10, 85, 23);
		contentPanel.add(checkUserNameButton);

		JLabel userNameLabel = new JLabel("\uC720\uC800 \uC774\uB984:");
		userNameLabel.setBounds(22, 14, 57, 15);
		contentPanel.add(userNameLabel);

		userNameInput = new JTextField();
		userNameInput.setBounds(83, 11, 116, 21);
		contentPanel.add(userNameInput);
		userNameInput.setColumns(10);

		JLabel passwordLabel = new JLabel("\uBE44\uBC00\uBC88\uD638:");
		passwordLabel.setBounds(26, 39, 52, 15);
		contentPanel.add(passwordLabel);

		passwordInput = new JTextField();
		passwordInput.setBounds(83, 36, 116, 21);
		contentPanel.add(passwordInput);
		passwordInput.setColumns(10);

		JLabel characterNameLabel = new JLabel("\uCE90\uB9AD\uD130 \uC774\uB984:");
		characterNameLabel.setBounds(12, 81, 68, 15);
		contentPanel.add(characterNameLabel);

		characterNameInput = new JTextField();
		characterNameInput.setBounds(83, 78, 116, 21);
		contentPanel.add(characterNameInput);
		characterNameInput.setColumns(10);

		JLabel emailLabel = new JLabel("\uC774\uBA54\uC77C \uC8FC\uC18C:");
		emailLabel.setBounds(12, 106, 68, 15);
		contentPanel.add(emailLabel);

		emailInput = new JTextField();
		emailInput.setBounds(83, 103, 116, 21);
		contentPanel.add(emailInput);
		emailInput.setColumns(10);

		JButton checkCharacterNameButton = new JButton("\uC911\uBCF5 \uD655\uC778");
		checkCharacterNameButton.setBounds(206, 77, 85, 23);
		contentPanel.add(checkCharacterNameButton);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\uD655\uC778");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {

						if (server.database.signup(userNameInput.getText().trim(),
								SHA256.digest(passwordInput.getText().trim()), characterNameInput.getText().trim(),
								emailInput.getText().trim())) {

							JOptionPane.showMessageDialog(contentPanel, "새로운 유저 정보가 생성되었습니다.", "새 유저 추가됨",
									JOptionPane.INFORMATION_MESSAGE);

							userNameInput.setText("");
							passwordInput.setText("");
							characterNameInput.setText("");
							emailInput.setText("");
							self.setVisible(false);
						} else {
							JOptionPane.showMessageDialog(contentPanel, "새로운 유저 정보 생성에 실패하였습니다.", "새 유저 추가 실패",
									JOptionPane.WARNING_MESSAGE);
						}

					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\uCDE8\uC18C");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						userNameInput.setText("");
						passwordInput.setText("");
						characterNameInput.setText("");
						emailInput.setText("");
						self.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
