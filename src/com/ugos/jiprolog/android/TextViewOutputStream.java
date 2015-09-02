package com.ugos.jiprolog.android;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;

import android.widget.TextView;

import com.ugos.util.ValueEncoder;


public class TextViewOutputStream extends OutputStream
{
    private TextView m_txtArea;
    private ByteArrayOutputStream buffer;
    private static final int BUFFER_SIZE = 10;
    private String encoding = Charset.defaultCharset().name();

    public TextViewOutputStream(TextView txtArea, String encoding)
    {
    	this(txtArea);
    	setEncoding(encoding);
    }

    public TextViewOutputStream(TextView txtArea)
    {
        m_txtArea = txtArea;
        buffer = new ByteArrayOutputStream();
    }

    public void write(int b) throws IOException
    {
        // Modifica per IE4.0
        if(b == 0x0d)
            return;

        //System.out.println("b " + Encoder.intToHexString(b));
        buffer.write(b);
        //strBuffer.app+= Character.to

        if(b == 0x0a || buffer.size() == BUFFER_SIZE)
        {
            flush();
        }
    }

    public void flush()
    {
        try {
			m_txtArea.append(new String(buffer.toByteArray(), encoding));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        buffer.reset();
    }

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
