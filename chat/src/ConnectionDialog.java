
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class 
ConnectionDialog extends JDialog implements ActionListener{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
protected JButton btnOK;
  protected JButton btnCancel;

  protected JTextField tfHost;
  protected JTextField tfPort;

  protected JLabel lblHost;
  protected JLabel lblPort;

  public boolean OKClicked;
  public String host;
  public String port;

  public ConnectionDialog(JFrame parent)
  {
    super(parent, true);
    setTitle("Parametry połaczenia");
    addWindowListener(new WindowAdapter(){
        public void windowClosing(WindowEvent evt)
        {
          dispose();
        }
      }
    );
    initLayout();
    setVisible(true);
  }
  public void initLayout()
  {
    setLayout(new GridLayout(3, 2));
    setSize(250, 100);

    tfHost = new JTextField();
    tfHost.setSize(100, 20);
    tfPort = new JTextField();
    tfPort.setSize(100, 20);

    lblHost = new JLabel("Host");
    lblHost.setSize(100, 20);
    lblPort = new JLabel("Port");
    lblPort.setSize(100, 20);

    btnOK = new JButton("OK");
    btnOK.setSize(100, 20);
    btnOK.addActionListener(this);

    btnCancel = new JButton("Anuluj");
    btnCancel.setSize(100, 20);
    btnCancel.addActionListener(this);

    add(lblHost);
    add(tfHost);
    add(lblPort);
    add(tfPort);
    add(btnOK);
    add(btnCancel);
  }
  public void actionPerformed(ActionEvent evt)
  {
    String tmp = evt.getActionCommand();
    if (tmp.equals("OK")){
      host = tfHost.getText();
      port = tfPort.getText();
      OKClicked = true;
      setVisible(false);
    }
    else if (tmp.equals("Anuluj")){
      OKClicked = false;
      setVisible(false);
    }
  }
}
