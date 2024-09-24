import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connect {
    //用于通信的 Socket
    private Socket socket;
    // socket 的对象输出流，用于发送消息
    private ObjectOutputStream outputStream;

    //ServerSocket，用于创建房间
    private ServerSocket serverSocket;

    //创建房间
    public void createHome() {
        String res = "";
        try {
            this.serverSocket = new ServerSocket(7777);
            int port = serverSocket.getLocalPort();
            String ip = InetAddress.getLocalHost().getHostAddress();
            res += ip + ":" + port;
            Controller.writeTextArea("Please ask other connect to \n" + res);
            socket = serverSocket.accept();
            Controller.writeTextArea("Connect Success");
            Controller.mode = 2;
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            //Lister
            createLister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Socket, ServerSocket, 以及 Socket 的输出流
     */
    public void close() {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
            this.outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用 ip和端口加入房间
     *
     * @param ip   IP地址
     * @param port 端口号
     */
    public void joinHome(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            Controller.writeTextArea("Connect Success");
            Controller.mode = 2;
            createLister();
        } catch (Exception e) {
            Controller.writeTextArea("Connect Fail");
            Controller.writeTextArea("IP and port error, try again");
            e.printStackTrace();
        }

    }

    /**
     * 创建新线程，用于接受消息
     */
    private void createLister() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                ObjectInputStream objectInputStream = null;
                try {
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        Object obj = objectInputStream.readObject();
                        System.out.println("obj = " + obj);
                        Controller.netReceiveSolve(obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        objectInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 用于发送消息
     *
     * @param obj 发送的消息对象
     */
    public void sendObject(Object obj) {
        if (socket == null) {
            return;
        }
        try {
            this.outputStream.writeObject(obj);
            this.outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
