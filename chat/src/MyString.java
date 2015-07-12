import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;


public class MyString implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String message;
	private Color foregroundColor;
	private Font font;
	
	public MyString(String message, Font font){
		this.setMessage(message);
		this.setFont(font);
		this.setForegroundColor(null);
	}
	public MyString(String message, Font font, Color foregroundColor){
		this.setMessage(message);
		this.setFont(font);
		this.setForegroundColor(foregroundColor);
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public Color getForegroundColor() {
		return foregroundColor;
	}
	
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	public int length() {
		return this.message.length();
	}
}