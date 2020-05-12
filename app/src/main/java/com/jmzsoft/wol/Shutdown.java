package com.jmzsoft.wol;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

class Shutdown {

    static Boolean ShutdownServer() {
        int exitStatus = -1;
        try {
            JSch jsch = new JSch();
            Session session=jsch.getSession(Constants.USER, Constants.URL.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)",""), Constants.SSH_PORT);
            session.setPassword(Constants.PASSWORD);
            if (Constants.DISABLE_HOST_CHECKING) {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
            }

            session.connect();

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(Constants.COMMAND);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    exitStatus = channel.getExitStatus();
                    break;
                }
                try{Thread.sleep(1000);
                } catch(Exception ignored){}
            }
            channel.disconnect();
            session.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return exitStatus == 0;
    }
}