import java.net.*;
import java.io.*;

public class ChatServerCommThread extends Thread
{
  protected Socket socket;
  protected ChatServerConsole cscon;
  protected ObjectInputStream brinp = null;
  protected ObjectOutputStream out = null;
  public String nick = null;
  public String threadName;
  public boolean stopped = false;
  public ChatServerCommThread(ChatServerConsole cscon, 
                              Socket socket)
  {
    this.socket = socket;
    this.cscon = cscon;
    threadName = getName();
  }
  public void run()
  {    
    //inicjalizacja strumieni
    try{
      brinp = new ObjectInputStream( socket.getInputStream());
      out = new ObjectOutputStream(socket.getOutputStream());
    }
    catch(IOException e){
      cscon.log(threadName + "| Blad przy tworzeniu strumieni " + e);
      cscon.removeThread(this);
      return;
    }
    Object line = null;
    
    //p�tla g��wna
    while(!stopped){
      try{
        try {
			line = brinp.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cscon.log(threadName + "| Odczytano linie: " + line);

        //osi�gni�ty koniec strumienia (brak po��czenia)
        if(line == null){
          cscon.log(threadName + 
            "| Zakonczenie pracy z klientem: " + socket);
          break;
        }
        else{
          processMessage((MyString)line);
        }
      }
      catch(IOException e){
        cscon.log(threadName + "| Blad wejscia-wyjscia: " + e);
        break;
      }
    }
    
    //ko�czenie pracy w�tku
    try{
      if(!socket.isClosed()) socket.close();
    }
    catch(IOException e){}
    stopped = true;
    cscon.removeThread(this);
    sendToAll( new MyString("/nkrm " + this.nick, null) );
    cscon.log(threadName + "| Wątek zatrzymany.");
  }
  
  public void processMessage(Object line)
  {
    cscon.log(threadName + "| Przetwarzam linie: " + line);
    if (((MyString) line).getMessage().length() < 5){
      if (nick != null){
    	String mes = ((MyString)line).getMessage().toString();
        sendToAll( new MyString(nick + "> " + mes, ((MyString) line).getFont(), ((MyString)line).getForegroundColor()) );
      }
      else{
        send( new MyString("/nonk", null));
      }
      return;
    }
    String command = ((MyString) line).getMessage().substring(0, 5);
    if (command.equals("/quit")){
      send(new MyString( "/quit", null));
      stopped = true;
    }
    else if (command.equals("/nick")){
      if (((MyString) line).getMessage().length() < 7){
        send( new MyString("/nonk", null) );
        return;
      }
      String nick = ((MyString) line).getMessage().substring(6, ((MyString) line).getMessage().length());
      if (!nickExists(nick)){
        send(new MyString("/nkok", null));
        if (this.nick != null){
          sendToAll(new MyString("/nkrm " + this.nick, null));
        }
        else{
          sendAllNicks();
        }
        this.nick = nick;
        sendToAll(new MyString("/nick " + nick, null));
      }
      else{
        send(new MyString("/nkex", null));
      }
    }
    else{
      if (nick != null){
      	String mes = ((MyString)line).getMessage().toString();
        sendToAll(new MyString(nick + "> " + mes, ((MyString) line).getFont(),((MyString)line).getForegroundColor()));
      }
      else{
        send(new MyString("/nonk", null));
      }
    }
  }
  public boolean nickExists(String nick)
  {
    synchronized(cscon.threadList){
      for (int i = 0; i < cscon.threadList.size(); i++){
        ChatServerCommThread st = cscon.threadList.elementAt(i);
        if((st.nick != null) && st.nick.equals(nick)){
          return true;
        }
      }
      return false;
    }
  }
  
  public void sendAllNicks()
  {
    synchronized(cscon.threadList){
      for (int i = 0; i < cscon.threadList.size(); i++){
        String nick = cscon.threadList.elementAt(i).nick;
        if ((nick != null) && !nick.equals(this.nick)){
          send(new MyString("/nick " + nick, null));
        }
      }
    }
  }

  public void send(MyString line)
  {
    try{
      out.writeObject(line);
      cscon.log(threadName + "| Wyslano: " + line.getMessage());
    }
    catch(IOException e){
      cscon.log(threadName + "| Blad wej�cia-wyj�cia: " + e);
    }
  }
  public void sendToAll(MyString line){
    synchronized(cscon.threadList){
      for (int i = 0; i < cscon.threadList.size(); i++){
        if (cscon.threadList.elementAt(i).nick != null)
          cscon.threadList.elementAt(i).send(line);
      }
    }
  }
  public void interrupt()
  {
    super.interrupt();
    try{
      socket.close();
    }
    catch(IOException e){}
  }
  public String getInfo()
  {
    String info = this.getName() + "| ";
    info += "IP " + socket.getInetAddress().getHostAddress() + " ";
    info += "Port " + socket.getPort() + " ";
    info += "nick:" + ((nick != null)?nick:"-");
    return info;
  }
}

