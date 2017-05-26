package client;

import Util.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client{
    public static void main(String[] args){
        BotClient client = new BotClient();
        client.run();
    }
    @Override
    protected SocketThread getSocketThread(){
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole(String str) {
        return false;
    }

    @Override
    protected String getUserName()
    {
        int num = (int)(Math.random() * 99);
        return "date_bot_" + num;
    }

    public class BotSocketThread extends SocketThread{
        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] data = message.split(": ");
            SimpleDateFormat dateFormat = null;
            if(data.length == 2) {
                switch (data[1]) {
                    case "дата":
                        dateFormat = new SimpleDateFormat("d.MM.YYYY");
                        break;
                    case "день":
                        dateFormat = new SimpleDateFormat("d");
                        break;
                    case "месяц":
                        dateFormat = new SimpleDateFormat("MMMM");
                        break;
                    case "год":
                        dateFormat = new SimpleDateFormat("YYYY");
                        break;
                    case "время":
                        dateFormat = new SimpleDateFormat("H:mm:ss");
                        break;
                    case "час":
                        dateFormat = new SimpleDateFormat("H");
                        break;
                    case "минуты":
                        dateFormat = new SimpleDateFormat("m");
                        break;
                    case "секунды":
                        dateFormat = new SimpleDateFormat("s");
                        break;
                }
                if(dateFormat != null)sendTextMessage("Информация для " +  data[0] + ": " + dateFormat.format(Calendar.getInstance().getTime()));
            }
        }

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {

            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }
    }
}
