import org.json.simple.JSONObject;

import javax.swing.*;

public class Controller {
    //当前的模式, 1 本地PVP, 2是联网 PVP
    public static int mode = 1;
    //用于标记游戏是否开始
    public static boolean startGame = false;

    //用于联机的类
    public static Connect connect;

    //用联机判断是否轮到自己下棋
    public static boolean turnMe = false;

    /**
     * 传入棋子的坐标，并传入对应的模式
     *
     * @param row 棋子的横坐标
     * @param col 棋子的纵坐标
     */
    public static void play(int row, int col) {
        if (!startGame) {
            writeTextArea("Cannot Play, Please Start New Game");
            return;
        }

        if (mode == 1) {
            mode1(row, col);
        } else if (mode == 2) {
            mode2(row, col);
        }
    }

    /**
     * 本地PVP模式 ,要传入棋子的坐标
     *
     * @param row 棋子的横坐标
     * @param col 棋子的纵坐标
     */
    private static void mode1(int row, int col) {
        int res = BoardStatus.getInstance().addChess(row, col);
        BoardFrame.boardFrame.drawBoardChess();
        // 根据 res 返回的结果，进行处理
        if (res == 1 || res == -1) {
            int t = 0;
            if (res == 1) {
                t = JOptionPane.showConfirmDialog(null, "black chess is win, want to restart?", null, JOptionPane.OK_OPTION);
                writeTextArea("Black Chess is Winner");
            } else {
                t = JOptionPane.showConfirmDialog(null, "white chess is win, want to restart?", null, JOptionPane.OK_OPTION);
                writeTextArea("White Chess is Winner");
            }
            if (t == 0) {
                Controller.reStart();
            }
        } else if (res == 0) {
            whoPlayInfoUpdate();
        }
    }

    /**
     * 用于本地PVP，开始新一局游戏
     */
    public static void reStart() {
        mode = 1;
        startGame = true;
        whoPlayInfoUpdate();
        //Model清空
        BoardStatus.getInstance().clearBoard();
        //视图更新
        BoardFrame.boardFrame.clearBoard();
        BoardFrame.boardFrame.drawBoard();
        BoardFrame.boardFrame.drawBoardChess();
    }

    /**
     * 用于本地PVP，进行悔棋操作
     */
    public static void withdraw() {
        BoardStatus.getInstance().withdraw();
        int r = BoardStatus.getInstance().nextChess;
        whoPlayInfoUpdate();
        //视图更新
        BoardFrame.boardFrame.clearBoard();
        BoardFrame.boardFrame.drawBoard();
        BoardFrame.boardFrame.drawBoardChess();
    }


    /**
     * 联网PVP模式 ,要传入棋子的坐标
     *
     * @param row 棋子的横坐标
     * @param col 棋子的纵坐标
     */
    private static void mode2(int row, int col) {
        if (!Controller.turnMe) {
            return;
        }

        int res = BoardStatus.getInstance().addChess(row, col);
        if (res == -2) {
            return;
        }
        sendXY(row, col);
        BoardFrame.boardFrame.drawBoardChess();
        if (res == 1) {
            JOptionPane.showMessageDialog(null, "Black chess is win");
            writeTextArea("Black Chess is Winner");
        } else if (res == -1) {
            JOptionPane.showMessageDialog(null, "White chess is win");
            writeTextArea("White Chess is Winner");
        }
        Controller.turnMe = false;
        if (res == 0) {
            whoPlayInfoUpdate();
        }
    }

    /**
     * 用于联网模式，发送棋子的x, y坐标
     *
     * @param x 棋子横坐标
     * @param y 棋子纵坐标
     */
    public static void sendXY(int x, int y) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 2);
        jsonObject.put("X", x);
        jsonObject.put("Y", y);
        Controller.connect.sendObject(jsonObject);
    }


    /**
     * 用于联网模式，开始新一局的联网游戏
     */
    public static void netStart() {
        if (connect == null) {
            Controller.writeTextArea("Not Create Home or Join Home");
        }
        JSONObject jsonObject = new JSONObject();
        // type 1表示开始新游戏
        jsonObject.put("type", 1);
        Controller.connect.sendObject(jsonObject);
        BoardStatus.getInstance().clearBoard();
        Controller.writeTextArea("You is Black Chess");
        Controller.turnMe = true;
        startGame = true;
        BoardFrame.boardFrame.clearBoard();
        BoardFrame.boardFrame.drawBoard();
        BoardFrame.boardFrame.drawBoardChess();
    }

    /**
     * 用于联网模式，创建房间，等待别人的加入
     */
    public static void createHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect = new Connect();
                connect.createHome();
            }
        }).start();
    }


    /**
     * 用于联网模式，加入别人创建好房间
     */
    public static void joinHome() {
        connect = new Connect();
        String res = JOptionPane.showInputDialog("input home ip like 192.168.1.1:7777");

        if (res.indexOf(":") == -1) {
            Controller.writeTextArea("IP and port not match");
            return;
        }

        String[] t = res.split(":");
        if (!t[0].matches("(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}")) {
            Controller.writeTextArea("IP error, try again");
            return;
        }
        if (!t[1].matches("[0-9][0-9][0-9][0-9]")) {
            Controller.writeTextArea("port error, try again");
            return;
        }


        Controller.writeTextArea("IP and port error, try again");
        connect.joinHome(t[0], Integer.valueOf(t[1]));
    }

    /**
     * 用于在文本框中更新现在轮到哪一方下棋
     */
    private static void whoPlayInfoUpdate() {
        int res = BoardStatus.getInstance().nextChess;
        if (res == 1) {
            writeTextArea("Black Chess Play");
        } else if (res == -1) {
            writeTextArea("White Chess Play");
        }
    }


    /**
     * 接收发送来的消息
     * type 1表示开始新游戏
     * type 2是每下一个棋子时发送的消息
     * type 3是普通消息
     * type 4 表示是撤回棋子
     * type 5 表示对方已经退出房间
     *
     * @param obj 接收到的消息，是一个JSONObject 类，其中的type表示接收到的消息的类型
     */
    public static void netReceiveSolve(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        if ((int) jsonObject.get("type") == 1) {
            // type 1表示开始新游戏
            BoardStatus.getInstance().clearBoard();
            Controller.writeTextArea("Start new Game");
            Controller.writeTextArea("You is White Chess");
            Controller.turnMe = false;
            startGame = true;
            BoardFrame.boardFrame.clearBoard();
            BoardFrame.boardFrame.drawBoard();
            BoardFrame.boardFrame.drawBoardChess();
        } else if ((int) jsonObject.get("type") == 2) {
            // type 2是每下一个棋子时发送的消息
            int x = (int) jsonObject.get("X");
            int y = (int) jsonObject.get("Y");
            int res = BoardStatus.getInstance().addChess(x, y);
            BoardFrame.boardFrame.clearBoard();
            BoardFrame.boardFrame.drawBoard();
            BoardFrame.boardFrame.drawBoardChess();
            Controller.turnMe = true;
            if (res == 1) {
                JOptionPane.showMessageDialog(null, "Black chess is win");
                writeTextArea("Black Chess is Winner");
            } else if (res == -1) {
                JOptionPane.showMessageDialog(null, "White chess is win");
                writeTextArea("White Chess is Winner");
            }
            if (res == 0) {
                whoPlayInfoUpdate();
            }
        } else if ((int) jsonObject.get("type") == 3) {
            //type 3是普通消息
            String text = (String) jsonObject.get("meg");
            Controller.writeTextArea("He: " + text);
        } else if ((int) jsonObject.get("type") == 4) {
            //type 4 表示是撤回棋子
            Controller.turnMe = !turnMe;
            whoPlayInfoUpdate();
            BoardStatus.getInstance().withdraw();
            BoardFrame.boardFrame.clearBoard();
            BoardFrame.boardFrame.drawBoard();
            BoardFrame.boardFrame.drawBoardChess();
        } else if ((int) jsonObject.get("type") == 5) {
            //type 5 表示对方已经退出房间
            Controller.writeTextArea("opponent exit home");
            connect.close();
            connect = null;
            mode = 0;
        }
    }

    /**
     * 用于联网悔棋`
     */
    public static void netWithdraw() {
        if (connect == null) {
            Controller.writeTextArea("Not join home");
            return;
        }
        Controller.turnMe = !turnMe;
        whoPlayInfoUpdate();
        BoardStatus.getInstance().withdraw();
        BoardFrame.boardFrame.clearBoard();
        BoardFrame.boardFrame.drawBoard();
        BoardFrame.boardFrame.drawBoardChess();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 4);
        Controller.connect.sendObject(jsonObject);
    }

    /**
     * 用于退出房间
     */
    public static void exitHome() {
        if (connect == null) {
            Controller.writeTextArea("Not Create Home or Join Home");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 5);
        Controller.connect.sendObject(jsonObject);
        connect.close();
        connect = null;
        mode = 0;
        Controller.writeTextArea("Exit Home Success");
    }

    /**
     * 用于在文本框中增加消息
     *
     * @param text 要加入的文本
     */
    public static void writeTextArea(String text) {
        BoardFrame.boardFrame.textArea.append("\n" + text);
        BoardFrame.boardFrame.textArea.selectAll();
    }


}
