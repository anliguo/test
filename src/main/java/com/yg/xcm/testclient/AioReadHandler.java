package com.yg.xcm.testclient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class AioReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel socket;

	public AioReadHandler(AsynchronousSocketChannel socket) {
		this.socket = socket;
	}

	public void cancelled(ByteBuffer attachment) {
		System.out.println("cancelled" + System.nanoTime());
	}

	private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

	@Override
	public void completed(Integer i, ByteBuffer buf) {
		if (i > 0) {
			buf.flip();
			try {
				System.out.println("收到异步回复" + socket.getRemoteAddress().toString() + "的消息:" + decoder.decode(buf) + " " + System.nanoTime());
				buf.compact();
			} catch (CharacterCodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket.read(buf, buf, this);
		} else if (i == -1) {
			try {
				System.out.println("对端断线:" + socket.getRemoteAddress().toString());
				buf = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer buf) {
		System.out.println(exc);
	}

	public static void main(String[] args) {

	}
}