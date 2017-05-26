package client;

public class ClientGuiController extends Client {
    private ClientGuiModel model = new ClientGuiModel();;
    private ClientGuiView view = new ClientGuiView(this);;

    public static void main(String[] args){
        new ClientGuiController().run();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }
    @Override
    public void run() {
        getSocketThread().run();
    }
    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }
    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }
    @Override
    protected String getUserName() {
        return view.getUserName();
    }
    public ClientGuiModel getModel() {
        return model;
    }

    public class GuiSocketThread extends SocketThread{
        @Override
        public void processIncomingMessage(String message){
            model.setNewMessage(message);
            view.refreshMessages();
        }
        @Override
        public void informAboutAddingNewUser(String userName){
            model.addUser(userName);
            view.refreshUsers();
        }
        @Override
        public void informAboutDeletingNewUser(String userName){
            model.deleteUser(userName);
            view.refreshUsers();
        }
        @Override
        public void notifyConnectionStatusChanged(boolean clientConnected){
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
