import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread
{
  protected Socket socket;
  protected  Object socketIn;
  protected ChatClient chatClient;
  public boolean stopped = false;
  public ChatClientThread(ChatClient chatClient, Socket socket, 
                           ObjectInputStream socketIn)
  {
    this.socket = socket;
    this.socketIn = socketIn;
    this.chatClient = chatClient;
  }
  public void run()
  {
    Object line = null;
    while(!stopped){
      try{
        try {
			line = ((ObjectInputStream) socketIn).readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      catch(IOException e){
        break;
      }
      if (line == null){
        break;
      }
      processMessage(line);
    }
    chatClient.clientThreadStopped();
  }
  public void processMessage(Object line)
  {
    if (((MyString) line).getMessage().length() < 5){
      chatClient.insertText((MyString)line);
      return;
    }
    String command = ((MyString) line).getMessage().substring(0, 5);
    if (command.equals("/quit")){
      stopped = true;
    }
    else if (command.equals("/nick")){
      if (((MyString) line).getMessage().length() < 7){
        chatClient.insertText(new MyString("B��dna odpowied� serwera!", null));
        return;
      }
      String nick = ((MyString) line).getMessage().substring(6, ((MyString) line).getMessage().length());
      chatClient.addNick(nick);
    }
    else if(command.equals("/nonk")){
      chatClient.insertText(
        new MyString("Najpierw okre�l sw�j nick! (U�yj komendy /nick) \n", null));
    }
    else if(command.equals("/nkex")){
      chatClient.insertText(
        new MyString("Ten nick jest u�ywany przez innego u�ytkownika.\n", null));
    }
    else if(command.equals("/nkok")){
      chatClient.insertText(new MyString("Nick zaakceptowany!\n", null));
    }
    else if (command.equals("/nkrm")){
      if (((MyString) line).getMessage().length() < 7){
        chatClient.insertText(new MyString("B��dna odpowied� serwera!", null));
        return;
      }
      String nick = ((MyString) line).getMessage().substring(6, ((MyString) line).getMessage().length());
      chatClient.removeNick(nick);
    }
    else{
      chatClient.insertText((MyString)line);
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
}
