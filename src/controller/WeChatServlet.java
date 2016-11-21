package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 接受微信服务器的信息,验证并返回信息
 * Created by Administrator on 2016/11/21.
 */
@WebServlet(name = "WeChatServlet")
public class WeChatServlet extends HttpServlet {
    //唯一的token值
    private final static String  token = "";
    //返回给微信服务器的echostr
    private String echostr;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        connect(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        message(request,response);
    }
    private void connect(HttpServletRequest request,HttpServletResponse response) throws IOException {
        if(!acceing(request,response)){
            return;
        }
        String echostr = getEchoStr();
        if(echostr != null && !"".equals(echostr)){
            response.getWriter().print(echostr);
        }
    }

    private String getEchoStr() {
        return echostr;
    }

    /**
     * 用来接收微信平台的验证
     * @param request
     * @param response
     * @return
     */
    private boolean acceing(HttpServletRequest request, HttpServletResponse response) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if(isEmpty(signature)){
            return false;
        }
        if(isEmpty(timestamp)){
            return false;
        }
        if(isEmpty(nonce)){
            return false;
        }
        if(isEmpty(echostr)){
            return false;
        }
        String[] strArray = {timestamp,nonce,token};
        Arrays.sort(strArray);
        StringBuffer buffer = new StringBuffer();
        for (String str: strArray) {
            buffer.append(str);
        }
        String pwd = getPwd(buffer.toString());
        if(trim(pwd).equals(trim(signature))){
            this.echostr = echostr;
            return  true;
        }else {
            return  false;
        }
    }

    private  void message(HttpServletRequest request,HttpServletResponse response){

    }

    private String getPwd(String str){
        MessageDigest md = null;
        String digest = "";
        byte[] bytes = str.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(bytes);
            digest = getHexStr(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return digest;
    }

    private String getHexStr(byte[] bytes){
        String des = "";
        String tmp = null;
        for(int i = 0; i < bytes.length;i++){
            tmp = Integer.toHexString(bytes[i] & 0xFF);
            if(tmp.length() == 1){
                des += "0";
            }
            des += tmp;
        }
        return  des;
    }

    private boolean isEmpty(String str){
        return null == str || "".equals(str) ? true :false;
    }

    private String trim(String str){
        return null != str ? str.trim() : null;
    }
}
