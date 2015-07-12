
import java.net.*;
import java.io.*;

public class ChatServerNetThread extends Thread
{
  protected ServerSocket serverSocket = null;
  protected ChatServerConsole cscon;
  protected int port;
  public ChatServerNetThread(ChatServerConsole cscon, int port)
  {
    this.cscon = cscon;
    this.port = port;
  }
  public void run()
  {
    Socket socket = null;
    try{
      serverSocket = new ServerSocket(port);
    }
    catch(IOException e){
      cscon.log(
        "Błąd przy tworzeniu gniazda serwerowego " + e);
      return;
    }
    cscon.log("Inicjalizacja gniazda zakończona...");
    cscon.log("Parametry gniazda: " + serverSocket);
    while(true){
      try{
        socket = serverSocket.accept();
      }
      catch(IOException e){
        cscon.log("Błąd wejścia-wyjścia: " + e);
        return;
      }
      cscon.log("Nadeszło połączenie...");
      cscon.log("Parametry połączenia: " + socket);
      ChatServerCommThread thread = 
        new ChatServerCommThread(cscon, socket);
      cscon.addThread(thread);
      thread.start();
    }
  }
  public void interrupt()
  {
    super.interrupt();
    try{
      serverSocket.close();
    }
    catch(IOException e){}
  }
}
