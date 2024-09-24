import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardFrame extends JFrame {
    //单例模式，只有一个下棋的窗口
    public static BoardFrame boardFrame;
    public JTextArea textArea;

    TextField input;

    /**
     * 设置窗口的基本信息，并布置相关组件
     */
    public BoardFrame() {
        super.setLocation(100, 100);
        super.setSize(Value.WINDOW_W, Value.WINDOW_H);
        super.setTitle("BoardGame");
        super.setBackground(Color.ORANGE);
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        super.setResizable(false);
        super.setLayout(null);
        super.setVisible(true);

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                //检测是否在棋盘内点击
                if (x > Value.MOV - Value.GRID_H / 2 && x < Value.MOV + Value.BOARD_W + Value.GRID_W / 2
                        && y > Value.MOV - Value.GRID_H / 2 && y < Value.MOV + Value.BOARD_W + Value.GRID_W / 2) {
                    int xx = x - (Value.MOV - Value.GRID_W / 2);
                    int yy = y - (Value.MOV - Value.GRID_W / 2);
                    int row = xx / Value.GRID_W;
                    int col = yy / Value.GRID_H;
                    Controller.play(row, col);
                }
            }
        });

        //增加重新开始的按键
        Button resetBut = new Button("Start New Game");
        resetBut.setBounds(550, 0, 100, 50);

        resetBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.reStart();
            }
        });
        super.add(resetBut);

        //增加悔棋按钮
        Button withdrawBut = new Button("Withdraw");
        withdrawBut.setBounds(550, 50, 100, 50);
        withdrawBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.withdraw();

            }
        });
        super.add(withdrawBut);

        //增加消息框
        this.textArea = new JTextArea();
        textArea.setBounds(550, 250, 240, 100);
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        textArea.setEditable(false);
        scrollableTextArea.setBackground(Color.white);
        scrollableTextArea.setBounds(550, 250, 240, 100);
        textArea.setText("");
//        scrollableTextArea.setViewport();
        super.getContentPane().add(scrollableTextArea);

        //增加创建房间按钮
        Button createHome = new Button("Create Home");
        createHome.setBounds(650, 0, 100, 50);
        createHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.createHome();
            }
        });
        super.add(createHome);

        //增加加入房间按钮
        Button addHome = new Button("Join Home");
        addHome.setBounds(650, 50, 100, 50);
        addHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.joinHome();
            }
        });
        super.add(addHome);

        //增加退出房间按钮
        Button exitHome = new Button("Exit Home");
        exitHome.setBounds(650, 100, 100, 50);
        exitHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.exitHome();
            }
        });
        super.add(exitHome);

        Button netStart = new Button("Net Start");
        netStart.setBounds(650, 150, 100, 50);
        netStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.netStart();
            }
        });
        super.add(netStart);

        Button netWithdraw = new Button("Net Withdraw");
        netWithdraw.setBounds(650, 200, 100, 50);
        netWithdraw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.netWithdraw();
            }
        });
        super.add(netWithdraw);


        //真加发送消息框
        this.input = new TextField();
        input.setBounds(550, 400, 200, 40);
        input.setEditable(true);
        super.add(input);
        Button sendBut = new Button("Send");
        sendBut.setBounds(550, 450, 200, 40);
        sendBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Controller.connect == null) {
                    Controller.writeTextArea("Not join home");
                    return;
                }
                String text = input.getText();
                Controller.writeTextArea("You: " + text);
                input.setText("");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 3);
                jsonObject.put("meg", text);
                Controller.connect.sendObject(jsonObject);
            }
        });
        super.add(sendBut);
    }


    @Override
    public void paint(Graphics g) {
        this.drawBoard();
        this.drawBoardChess();
    }


    /**
     * 清空棋盘
     */
    public void clearBoard() {
        Graphics g = getGraphics();
        g.clearRect(0, 0, Value.MOV + Value.BOARD_W + Value.CHESS_R, Value.MOV + Value.BOARD_W + Value.CHESS_R);
    }

    /**
     * 画出棋盘
     */
    public void drawBoard() {
        Graphics g = getGraphics();
        g.setColor(Color.BLACK);
        for (int i = 0; i <= Value.ROW_NUM; i++) {
            g.drawLine(Value.MOV, Value.MOV + i * Value.GRID_H, Value.MOV + Value.BOARD_W, Value.MOV + i * Value.GRID_H);
            g.drawLine(Value.MOV + i * Value.GRID_H, Value.MOV, Value.MOV + i * Value.GRID_H, Value.MOV + Value.BOARD_W);
        }
    }

    /**
     * 画出棋盘上所有的棋子
     */
    public void drawBoardChess() {
        Graphics g = getGraphics();
        for (int[] e : BoardStatus.getInstance().playChessQueue) {
            int res = BoardStatus.getInstance().chess[e[0]][e[1]];
            if (res == 1) {
                g.setColor(Color.BLACK);
                g.fillOval(Value.MOV + e[0] * Value.GRID_W - Value.CHESS_R, Value.MOV + e[1] * Value.GRID_H - Value.CHESS_R, 2 * Value.CHESS_R, 2 * Value.CHESS_R);
            } else if (res == -1) {
                g.setColor(Color.WHITE);
                g.fillOval(Value.MOV + e[0] * Value.GRID_W - Value.CHESS_R, Value.MOV + e[1] * Value.GRID_H - Value.CHESS_R, 2 * Value.CHESS_R, 2 * Value.CHESS_R);
            }
        }
    }
}
