import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;


public class ChatSettings{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6683660589889204654L;
	private Color foregroundColor;
	private Color backgroundColor;
	private Font font;
	public Color getForegroundColor() {
		return foregroundColor;
	}
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
}
