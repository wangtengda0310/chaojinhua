package test;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author xym
 */
public class MyClient extends JFrame implements IEventListener {

    //private static final int uid = 10001303;   //pid = 1000179 玩家_1000179
    //private static final int uid = 10001302;   //pid = 1000178 玩家_1000178
    private static final int uid = 10001356;   //pid = 1000133 玩家_1000133
    private static final String pwd = "1";
    private static final int serverId = 1;
    private static final String zoneName = "server";
//    private static final String ip = "localhost";
    private static final String ip = "192.168.2.234";
    private static int index = 1;

    public static void main(String[] args) {

        new MyClient().start();
        //new MyClient().myGui();

    }





    private SmartFox sfsClient;

    private JLabel label=new JLabel("");

    private JTextArea sendTextArea =new JTextArea(5,50);
    private JTextArea recTextArea =new JTextArea(8,50);
    private JTextArea hisTextArea =new JTextArea(10,50);
    private JTextField proTextField=new JTextField(10);

    private List<String> his = new ArrayList<>();

    public void start() {

        sfsClient = new SmartFox();
        sfsClient.addEventListener(SFSEvent.CONNECTION, this);//连接
        sfsClient.addEventListener(SFSEvent.LOGIN, this);// 登陆
        sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);//加入room
        sfsClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);//监听服务器自己定义协议内容
        sfsClient.addEventListener(SFSEvent.UDP_INIT, this);
        sfsClient.addEventListener(SFSEvent.PRIVATE_MESSAGE, this); //私聊
        sfsClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this); //公聊

        sendTextArea.setEditable(true);
        sendTextArea.setLineWrap(true);        //激活自动换行功能
        sendTextArea.setWrapStyleWord(true);            // 激活断行不断字功能

        recTextArea.setEditable(false);
        recTextArea.setLineWrap(true);        //激活自动换行功能
        recTextArea.setWrapStyleWord(true);            // 激活断行不断字功能

        hisTextArea.setEditable(false);
        hisTextArea.setLineWrap(true);        //激活自动换行功能
        hisTextArea.setWrapStyleWord(true);            // 激活断行不断字功能

        sfsClient.connect(ip, 9933);

        myGui();
    }

    private void myGui(){

        this.setTitle("文本框使用");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //用户单击窗口的关闭按钮时程序执行的操作
        this.setLocation(600,100);  //离显示屏上边缘300像素，里显示屏左边缘300像素
        this.setSize(600,800);            //设置窗体的大小为300*200像素大小
        this.setResizable(false);       //设置窗体是否可以调整大小，参数为布尔值

        //添加主容器
        JPanel contentPane=new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new GridLayout(4,1,5,5));
        this.setContentPane(contentPane);

        //第一行第一列
        JPanel pane1=new JPanel();
        pane1.add(new JLabel("协议号："));
        pane1.add(proTextField);

        //第一行第二列
        JPanel bPane=new JPanel( );
        bPane.setBorder(new EmptyBorder(5,5,5,5));

        JButton tt = new JButton("发送消息");
        tt.addActionListener(e -> actionPerformed());

        bPane.add(label);
        bPane.add(tt);
        pane1.add(bPane);

        //第二行
        JPanel pane2=new JPanel();
        pane2.add(new JLabel("发送："));
        pane2.add(new JScrollPane(sendTextArea));

        //第三行
        JPanel pane3=new JPanel();
        pane3.add(new JLabel("接收："));
        pane3.add(new JScrollPane(recTextArea));

        //第四行
        JPanel pane4=new JPanel();
        pane4.add(new JLabel("历史："));
        pane4.add(new JScrollPane(hisTextArea));

        contentPane.add(pane1);
        contentPane.add(pane2);
        contentPane.add(pane3);
        contentPane.add(pane4);
        this.setVisible(true);  //设置窗体可见

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                sfsClient.send(new LogoutRequest());
                System.out.println("window closing");
                System.exit(0);
            }
        });
    }

    //点击事件
    private void actionPerformed() {

        label.setText("");
        his.clear();
        recTextArea.setText("");
        hisTextArea.setText("");

        ISFSObject req = new SFSObject();
        String pro = proTextField.getText();
        String content = sendTextArea.getText();

        System.out.println("sendClient:"+content);
        System.out.println();

        if (pro.equals("800")){
            req.putInt("type",Integer.parseInt(content));
            sfsClient.send(new PublicMessageRequest("test公聊",req));
        }else if (pro.equals("801")){
            sfsClient.send(new PrivateMessageRequest("test私聊",Integer.parseInt(content),req));
        }else {
            req.putUtfString("infor", content);
            req.putInt("index", index++);
            sfsClient.send(new ExtensionRequest(pro,req));
        }

        label.setText("发送成功");
    }

    @Override
    public void dispatch(final BaseEvent event) throws SFSException {
        if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {

            if (event.getArguments().get("success").equals(true)) {
                System.out.println("Connection Successful");
                ISFSObject req = new SFSObject();
                req.putBool("isRepeat",false);
                sfsClient.send(new LoginRequest(uid+"", pwd, zoneName,req));
            }else {
                System.out.println("ERROR: Connection Not Successful");
            }

        } else if (event.getType().equals(SFSEvent.LOGIN)){// 处理用户登陆事件

            JSONObject send = new JSONObject();
            send.put("userId", uid);
            send.put("serverId", serverId);

            ISFSObject req = new SFSObject();
            req.putUtfString("infor", send.toString());
            req.putInt("index", index++);

            proTextField.setText("1003");
            sendTextArea.setText(send.toString());
            sfsClient.send(new ExtensionRequest("1003",req));

            System.out.println("LOGIN success");

        } else if (event.getType().equals(SFSEvent.ROOM_JOIN)){// 处理用户JOIN ROOM

            System.out.println("ROOM_JOIN success");
            label.setText("启动成功！");

        } else if (event.getType().equals(SFSEvent.EXTENSION_RESPONSE)){// 监听服务端拓展传递给客户端的数据

            // 打印传递过来的全部数据
            String res = "res:"+event.getArguments() + "\n"
                    + "params:"+((SFSObject)event.getArguments().get("params")).getUtfString("infor")
                    + "\n";
            his.add(res);
            System.out.println(res);
            System.out.println();
            recTextArea.setText(res);
            hisTextArea.setText(StringUtils.strip(his.toString(),"[]"));

            Object cmd = event.getArguments().get("cmd");
            if(cmd.equals("1003")){//第三步（自定义的进入游戏协议）
                sfsClient.send(new JoinRoomRequest(6));
            }

        } else if (event.getType().equals(SFSEvent.PUBLIC_MESSAGE)){// 监听公聊

            // 打印传递过来的全部数据
            String res = "res: 公聊"+event.getArguments() + "\n";

            his.add(res);
            System.out.println(res);
            System.out.println();
            proTextField.setText("公聊");
            recTextArea.setText(res);
            hisTextArea.setText(StringUtils.strip(his.toString(),"[]"));

        } else if (event.getType().equals(SFSEvent.PRIVATE_MESSAGE)){// 监听私聊

            // 打印传递过来的全部数据
            String res = "res: 私聊"+event.getArguments() + "\n";

            his.add(res);
            System.out.println(res);
            System.out.println();
            proTextField.setText("私聊");
            recTextArea.setText(res);
            hisTextArea.setText(StringUtils.strip(his.toString(),"[]"));

        }
    }
}