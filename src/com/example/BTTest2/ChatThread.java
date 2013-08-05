package com.example.BTTest2;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: loredan
 * Date: 05.08.13
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class ChatThread extends Thread
{
    public Handler handler;
    public InputStream in;
    public OutputStream out;

    public ChatThread(Handler _handler)
    {
        Log.i("ChatThread", "Started");
        handler = _handler;
        try
        {
            in = Ref.btSocket.getInputStream();
            out = Ref.btSocket.getOutputStream();
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run()
    {
        byte[] buffer = new byte[1024];
        int bytes;
        while(true)
        {
            try
            {
                bytes = in.read(buffer);
                handler.obtainMessage(Ref.INCOMING_MESSAGE, bytes, 0, buffer);
            } catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void write(String msg)
    {
        try
        {
            out.write(msg.getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
