import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import ZoeloeSoft.projects.JFontChooser.JFontChooser;

@SuppressWarnings("serial")
public class ChatClient extends JFrame implements ActionListener {
	protected DefaultListModel<String> dlm;
	@SuppressWarnings("rawtypes")
	protected JList lstNicks;
	protected MyTextPane taMain;
	protected JTextField tfCommand;
	protected JButton btnConnect;
	protected JButton btnDisconnect;
	protected JButton btnExit;
	protected JButton btnFont;
	protected JButton btnColor;
	protected JButton btnColor2;
	private Font font;
	protected boolean connected;
	protected Socket socket;
	protected ObjectInputStream socketIn;
	protected ObjectOutputStream socketOut;
	protected ChatClientThread clientThread;

	private Color foregroundColor;

	private Color backgroundColor;
	private ChatSettings settings;

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
		taMain.setForeground(foregroundColor);
		tfCommand.setForeground(foregroundColor);
		lstNicks.setForeground(foregroundColor);
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		taMain.setBackground(backgroundColor);
		tfCommand.setBackground(backgroundColor);
		lstNicks.setBackground(backgroundColor);
	}

	@Override
	public Font getFont() {
		return this.font;
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		this.font = f;
		System.out.println(f.getFamily());
		taMain.setFont(f);
		tfCommand.setFont(f);
		lstNicks.setFont(f);
	}

	public ChatClient() {
		super("ChatClient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitClicked();
				saveSettings();
			}
		});
		initLayout();
		setVisible(true);
	}
	protected void loadSettings() {
		try {
			FileInputStream input = new FileInputStream("client.ser");
			ObjectInputStream reader = new ObjectInputStream(input);
			this.settings = (ChatSettings) reader.readObject();
			setSettings(this.settings);
			reader.close();
			input.close();
		}
		catch (Exception e) {
			
		}
	}
	private void setSettings(ChatSettings settings2) {
		if (this.settings!=null) {
			this.setFont(this.settings.getFont());
			this.setForegroundColor(this.settings.getForegroundColor());
			this.setBackgroundColor(this.settings.getBackgroundColor());
		}
		
	}

	protected void saveSettings() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("client.ser"));
			if (this.settings==null) this.settings = new ChatSettings();
			this.settings.setBackgroundColor(backgroundColor);
			this.settings.setForegroundColor(foregroundColor);
			this.settings.setFont(font);
			out.writeObject(this.settings);
			out.close();
		}
		catch (Exception e) {
			
		}
	}

	public void initLayout() {
		setSize(650, 520);
		setResizable(false);
		setLayout(null);

		taMain = new MyTextPane();
		taMain.setEditable(false);
		JScrollPane panelLeft = new JScrollPane(taMain);
		panelLeft.setBounds(10, 20, 480, 360);
		add(panelLeft);

		dlm = new DefaultListModel<String>();
		lstNicks = new JList<String>(dlm);
		JScrollPane panelRight = new JScrollPane(lstNicks);
		panelRight.setBounds(500, 20, 130, 360);
		add(panelRight);

		JPanel panelBottom1 = new JPanel();
		panelBottom1.setLayout(new BorderLayout());
		panelBottom1.setBounds(10, 395, 620, 25);
		add(panelBottom1);

		JPanel panelBottom2 = new JPanel();
		panelBottom2.setLayout(new GridLayout(1, 3));
		panelBottom2.setBounds(10, 430, 620, 35);
		add(panelBottom2);

		tfCommand = new JTextField();
		tfCommand.addKeyListener(new KeyAdapter() {
			

	

			public void keyPressed(KeyEvent evt) {
				keyPressed(evt.getKeyCode());
			}

			public void keyPressed(int keyCode) {
				if (keyCode != KeyEvent.VK_ENTER) {
					return;
				}
				if (!connected) {
					insertText( new MyString("Najpierw nawiąż połączenie!\n", null));
					return;
				}
				MyString line = new MyString(tfCommand.getText(), getFont(),getForegroundColor());
				if (line.getMessage().equals(""))
					return;
				tfCommand.setText("");
				try {
					socketOut.writeObject(line);
					socketOut.flush();
				} catch (IOException e) {
					insertText( new MyString("Błąd przy wysyłaniu danych: " + e + "\n", null));
				}
			}
		});
		panelBottom1.add(tfCommand);

		btnConnect = new JButton("Połącz");
		btnConnect.setActionCommand("Connect");
		btnConnect.addActionListener(this);
		panelBottom2.add(btnConnect);

		btnDisconnect = new JButton("Rozłącz");
		btnDisconnect.setActionCommand("Disconnect");
		btnDisconnect.addActionListener(this);
		panelBottom2.add(btnDisconnect);

		btnFont = new JButton("Zmien czcionke");
		btnFont.setActionCommand("Font");
		btnFont.addActionListener(this);
		panelBottom2.add(btnFont);
		
		btnColor = new JButton("Kolor tekstu");
		btnColor.setActionCommand("Color");
		btnColor.addActionListener(this);
		panelBottom2.add(btnColor);
		
		btnColor2 = new JButton("Kolor tla");
		btnColor2.setActionCommand("Color2");
		btnColor2.addActionListener(this);
		panelBottom2.add(btnColor2);

		btnExit = new JButton("Wyjdź");
		btnExit.setActionCommand("Exit");
		btnExit.addActionListener(this);
		panelBottom2.add(btnExit);
		loadSettings();
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ChatClient();
			}
		});
	}

	public void actionPerformed(ActionEvent evt) {
		String tmp = evt.getActionCommand();
		if (tmp.equals("Exit")) {
			exitClicked();
			saveSettings();
		}
		if (tmp.equals("Font")) {
			changeFontClicked();
		} else if (tmp.equals("Disconnect")) {
			disconnectClicked();
		} else if (tmp.equals("Connect")) {
			connectClicked();
		} else if (tmp.equals("Color")) {
			changeForegroundClicked();
		} else if (tmp.equals("Color2")) {
			changeBackgroundClicked();
		}
	}

	private void changeBackgroundClicked() {
		Color c = JColorChooser.showDialog(this, "Wybierz kolor tla",
				this.getBackgroundColor());
		if (c != null) {
			this.setBackgroundColor(c);
		}
	}

	private void changeForegroundClicked() {
		Color c = JColorChooser.showDialog(this, "Wybierz kolor tekstu",
				this.getForegroundColor());
		if (c != null) {
			this.setForegroundColor(c);	
		}
	}

	private Color getForegroundColor() {
		return this.foregroundColor;
	}

	private Color getBackgroundColor() {
		return this.backgroundColor;
	}

	private void changeFontClicked() {
		JFontChooser fc = new JFontChooser(this);
		fc.setFont(this.getFont());
		int result = fc.showDialog();
		if (result == JFontChooser.OK_OPTION) {
			this.setFont(fc.getFont());
		}
	}

	public void connectClicked() {
		if (connected) {
			insertText( new MyString("Najpierw zakoncz biezace polaczenie!", null));
			return;
		}
		ConnectionDialog cd = new ConnectionDialog(this);
		if (!cd.OKClicked) {
			return;
		}
		int port;
		try {
			port = Integer.parseInt(cd.port);
		} catch (NumberFormatException e) {
			insertText( new MyString("Nieprawidlowy numer portu: " + cd.port + "\n", null));
			return;
		}
		connect(cd.host, port);
	}

	public void connect(String host, int port) {
		connected = false;
		insertText( new MyString("Połączenie z hostem " + host + "\n", null));
		try {
			socket = new Socket(host, port);
		} catch (IOException e) {
			insertText( new MyString("Bląd  gniazda: " + e + "\n", null));
			return;
		}
		insertText( new MyString("Zakończona inicjalizacja gniazda...\n", null));
		try {
			socketOut = new ObjectOutputStream(socket.getOutputStream());
			socketIn = new ObjectInputStream(
					socket.getInputStream());
		} catch (IOException e) {
			insertText( new MyString("Błąd przy tworzeniu strumieni: " + e + "\n", null));
			return;
		}
		clientThread = new ChatClientThread(this, socket, socketIn);
		clientThread.start();
		insertText( new MyString("Połączono z serwerem.\n", null));
		connected = true;
	}

	public void disconnectClicked() {
		if (!connected) {
			insertText( new MyString("Brak połączenia!\n", null));
			return;
		}
		clientThread.interrupt();
		connected = false;
	}

	public void clientThreadStopped() {
		removeAllstNicks();
		insertText( new MyString("Rozłączono!\n", null));
		connected = false;
		try {
			if (!socket.isClosed())
				socket.close();
		} catch (IOException e) {
		}
	}

	public void insertText(final MyString line) {
		//TODO Tutaj można zrobić coś z czionkami
		//Font f = line.getFont(); // Może tu sypnąć nullem :)
		if (SwingUtilities.isEventDispatchThread()) {
			taMain.insert(line, 0);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					taMain.insert(line, 0);
				}
			});
		}
	}

	public void addNick(String nick) {
		if (dlm.size()==0) nick = "@"+nick;
		dlm.addElement(nick);
	}

	public void removeNick(String nick) {
		dlm.removeElement(nick);
		dlm.removeElement("@"+nick);
		if (dlm.size()==1) nick = dlm.get(0);
		dlm.remove(0);
		dlm.addElement("@"+nick);
	}

	public void removeAllstNicks() {
		dlm.removeAllElements();
	}

	public void exitClicked() {
		if (connected)
			this.insertText( new MyString("/quit", null));
			clientThread.interrupt();
		dispose();
	}
}
