import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MyTextPane extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6783701508735688680L;

	public void insert(String text, int a) {
		try {
			this.getDocument().insertString(this.getDocument().getLength(),
					text + "\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insert(MyString line, int a) {
		try {
			this.getDocument().insertString(this.getDocument().getLength(),
					line.getMessage() + "\n", MyTextPane.getAttributes(this, line));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static SimpleAttributeSet getAttributes(JTextPane jtp, MyString line) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		if (line.getForegroundColor()!=null) {
		attr.addAttribute(StyleConstants.CharacterConstants.Foreground,
				line.getForegroundColor());
		}
		if (line.getFont()!=null) {
		attr.addAttribute(StyleConstants.CharacterConstants.Family, line
				.getFont().getFamily());
		attr.addAttribute(StyleConstants.CharacterConstants.Size, line
				.getFont().getSize());
		attr.addAttribute(StyleConstants.CharacterConstants.Bold, line
				.getFont().isBold());
		attr.addAttribute(StyleConstants.CharacterConstants.Italic, line
				.getFont().isItalic());
		}
		return attr;
	}
}
