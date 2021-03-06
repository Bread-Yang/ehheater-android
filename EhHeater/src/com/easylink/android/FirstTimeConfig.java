package com.easylink.android;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FirstTimeConfig {
	private boolean stopSending;
	private String ip;
	private String ssid;
	private String key;
	private String mDnsAckString;
	private int nSetup;
	private int nSync;
	private String syncLString;
	private String syncHString;
	private byte[] encryptionKey;
	private FtcEncode ftcData;
	InetSocketAddress sockAddr;
	private MulticastSocket listenSocket;
	int localPort;
	int listenPort;
	int waitForAckSocketTimeout;
	private FirstTimeConfigListener m_listener = null;
	Thread sendingThread;
	Thread ackWaitThread;
	private boolean isListenSocketGracefullyClosed;
	Thread mDnsThread;

	public FirstTimeConfig(FirstTimeConfigListener listener, String Key,
			byte[] EncryptionKey, String Ip, String Ssid) throws Exception {
		this(listener, Key, EncryptionKey, Ip, Ssid, "EMW3161");
	}

	public FirstTimeConfig(FirstTimeConfigListener listener, String Key,
			byte[] EncryptionKey, String Ip, String Ssid, String ackString)
			throws Exception {
		this(listener, Key, EncryptionKey, Ip, Ssid, ackString, 5353);
	}

	public FirstTimeConfig(FirstTimeConfigListener listener, String Key,
			byte[] EncryptionKey, String Ip, String Ssid, String ackString,
			int notifyListenPort) throws Exception {
		this(listener, Key, EncryptionKey, Ip, Ssid, ackString,
				notifyListenPort, 15000);
	}

	public FirstTimeConfig(FirstTimeConfigListener listener, String Key,
			byte[] EncryptionKey, String Ip, String Ssid, String ackString,
			int notifyListenPort, int localPort) throws Exception {
		this(listener, Key, EncryptionKey, Ip, Ssid, ackString,
				notifyListenPort, localPort, 300000);
	}

	public FirstTimeConfig(FirstTimeConfigListener listener, String Key,
			byte[] EncryptionKey, String Ip, String Ssid, String ackString,
			int notifyListenPort, int localPort, int WaitForAckSocketTimeout)
			throws Exception {
		this(listener, Key, EncryptionKey, Ip, Ssid, ackString,
				notifyListenPort, localPort, WaitForAckSocketTimeout, 4, 10,
				"abc", "abcdefghijklmnopqrstuvw");
	}

	public FirstTimeConfig(FirstTimeConfigListener listener, String Key,
			byte[] EncryptionKey, String Ip, String Ssid, String ackString,
			int notifyListenPort, int LocalPort, int WaitForAckSocketTimeout,
			int numberOfSetups, int numberOfSyncs, String syncL, String syncH)
			throws Exception {
		int AES_LENGTH = 16;

		this.m_listener = listener;

		if ((EncryptionKey != null) && (EncryptionKey.length != 0)
				&& (EncryptionKey.length != 16)) {
			throw new Exception("Encryption key must have 16 characters!");
		}

		if (Key.length() > 32) {
			throw new Exception(
					"Password is too long! Maximum length is 32 characters.");
		}

		if (Ssid.length() > 32) {
			throw new Exception(
					"Network name (SSID) is too long! Maximum length is 32 characters.");
		}
		this.stopSending = true;

		this.isListenSocketGracefullyClosed = false;

		this.listenSocket = null;

		this.ip = Ip;
		this.ssid = Ssid;
		this.key = Key;
		this.nSetup = numberOfSetups;
		this.nSync = numberOfSyncs;

		this.syncLString = syncL;
		this.syncHString = syncH;

		this.encryptionKey = EncryptionKey;

		this.mDnsAckString = ackString;

		createBroadcastUDPSocket(notifyListenPort);
		this.localPort = new Random().nextInt(65535);

		this.listenPort = 5353;
		this.waitForAckSocketTimeout = WaitForAckSocketTimeout;

		this.sockAddr = new InetSocketAddress(this.ip, this.localPort);

		// this.key = "";
		// this.ssid = "EA-T08";
		
		byte[] keyData = new byte[this.key.length()];
		System.arraycopy(this.key.getBytes(), 0, keyData, 0, this.key.length());
		
		keyData = encryptData(keyData);

		this.ftcData = new FtcEncode(this.ssid, keyData);
	}

	private void createBroadcastUDPSocket(int port) throws Exception {
		InetAddress wildcardAddr = null;
		InetSocketAddress localAddr = null;
		localAddr = new InetSocketAddress(wildcardAddr, port);
		this.listenSocket = new MulticastSocket(null);
		this.listenSocket.setReuseAddress(true);
		this.listenSocket.bind(localAddr);
		this.listenSocket.setTimeToLive(255);
		this.listenSocket.joinGroup(InetAddress.getByName("224.0.0.251"));
		this.listenSocket.setBroadcast(true);
	}

	private void sendSync() throws SocketException, Exception {
		byte[] syncLBuffer = this.syncLString.getBytes();
		byte[] syncHBuffer = this.syncHString.getBytes();
		for (int k = 0; k < 10; k++) {
			sendData(new DatagramPacket(syncLBuffer, syncLBuffer.length,
					this.sockAddr), getRandomNumber());
			sendData(new DatagramPacket(syncHBuffer, syncHBuffer.length,
					this.sockAddr), getRandomNumber());
			//Thread.sleep(5L);
		}
	}

	private void send() throws Exception {
		int numberOfPackets = this.ftcData.getmData().size();

		ArrayList packets = new ArrayList();
		byte[] ftcBuffer = new byte[1600];
		byte[] syncLBuffer = this.syncLString.getBytes();
		byte[] syncHBuffer = this.syncHString.getBytes();

		ftcBuffer = makePaddedByteArray(ftcBuffer.length);

		packets = this.ftcData.getmData();

		while (!this.stopSending) {
			for (int i = 0; i < this.nSetup; i++) {

				sendSync();
				Thread.sleep(1L);
				for (int j = 0; j < numberOfPackets; j++) {
					sendData(
							new DatagramPacket(ftcBuffer,
									((Integer) packets.get(j)).intValue(),
									this.sockAddr), getRandomNumber());
					Thread.sleep(3L);
				}
			}
			Thread.sleep(300L);
		}
	}

	private void sendData(DatagramPacket packet, int localSendingPort)
			throws Exception {
		DatagramSocket sock = null;
		sock = new DatagramSocket(localSendingPort);
		sock.setReuseAddress(true);
		sock.setBroadcast(true);
		sock.send(packet);

		sock.close();
	}

	public void transmitSettings() throws Exception {
		this.stopSending = false;
		this.sendingThread = new Thread(new Runnable() {
			public void run() {
				try {
					FirstTimeConfig.this.send();
				} catch (Exception e) {
					new FirstTimeConfig.NotifyThread(
							FirstTimeConfig.this.m_listener, e);
				}
			}
		});
		this.sendingThread.start();

//		this.ackWaitThread = new Thread(new Runnable() {
//			public void run() {
//				try {
//					FirstTimeConfig.this.waitForAck();
//				} catch (Exception e) {
//					new FirstTimeConfig.NotifyThread(
//							FirstTimeConfig.this.m_listener, e);
//				}
//			}
//		});
//		this.ackWaitThread.start();
	}

	public void stopTransmitting() throws Exception {
		this.isListenSocketGracefullyClosed = true;
		this.listenSocket.close();

		this.stopSending = true;
		if (Thread.currentThread() != this.sendingThread)
			this.sendingThread.join();
		if (Thread.currentThread() != this.ackWaitThread)
			this.ackWaitThread.join();
	}

	private void waitForAck() throws Exception {
		int RECV_BUFFER_LENGTH = 16384;

		byte[] recvBuffer = new byte[16384];

		DatagramPacket listenPacket = new DatagramPacket(recvBuffer, 16384);
		int timeout = this.waitForAckSocketTimeout;

		label97: while (!this.stopSending) {
			long start = System.nanoTime();
			this.listenSocket.setSoTimeout(timeout);
			try {
				this.listenSocket.receive(listenPacket);
			} catch (InterruptedIOException e) {
				if (this.isListenSocketGracefullyClosed)
					break;
				new NotifyThread(this.m_listener,
						FirstTimeConfigListener.FtcEvent.FTC_TIMEOUT);
				break;
			} catch (Exception e) {
				if (!this.isListenSocketGracefullyClosed)
					break label97;
			}
			break;
		}
	}

	private boolean parseMDns(byte[] data) throws Exception {
		int MDNS_HEADER_SIZE = 12;
		int MDNS_HEADER_SIZE2 = 10;

		int pos = 12;

		if (data.length < pos + 1)
			return false;
		int len = data[pos] & 0xFF;
		pos++;

		while (len > 0) {
			if (data.length < pos + len) {
				return false;
			}
			pos += len;

			if (data.length < pos + 1)
				return false;
			len = data[pos] & 0xFF;
			pos++;
		}

		pos += 10;

		if (data.length < pos + 1)
			return false;
		len = data[pos] & 0xFF;
		pos++;

		if (data.length < pos + len)
			return false;
		String name = new String(data, pos, len);

		boolean bRet = name.equals(this.mDnsAckString);
		return bRet;
	}

	private byte[] encryptData(byte[] data) throws Exception {
		if (this.encryptionKey == null)
			return data;
		if (this.encryptionKey.length == 0) {
			return data;
		}
		int ZERO_OFFSET = 0;
		int AES_LENGTH = 16;
		int DATA_LENGTH = 32;

		Cipher c = null;
		byte[] encryptedData = null;
		byte[] paddedData = new byte[32];
		byte[] aesKey = new byte[16];

		for (int x = 0; x < 16; x++) {
			if (x < this.encryptionKey.length) {
				aesKey[x] = this.encryptionKey[x];
			} else {
				aesKey[x] = 0;
			}

		}

		int dataOffset = 0;
		if (data.length < 32) {
			paddedData[dataOffset] = ((byte) data.length);
			dataOffset++;
		}

		System.arraycopy(data, 0, paddedData, dataOffset, data.length);
		dataOffset += data.length;

		while (dataOffset < 32) {
			paddedData[dataOffset] = 0;
			dataOffset++;
		}

		c = Cipher.getInstance("AES/ECB/NoPadding");

		SecretKeySpec k = null;
		k = new SecretKeySpec(aesKey, "AES");

		c.init(1, k);

		encryptedData = c.doFinal(paddedData);

		return encryptedData;
	}

	private byte[] makePaddedByteArray(int length) throws Exception {
		byte[] paddedArray = new byte[length];

		for (int x = 0; x < length; x++) {
			paddedArray[x] = ((byte) "1".charAt(0));
		}

		return paddedArray;
	}

	private class NotifyThread implements Runnable {
		private FirstTimeConfigListener m_listener;
		private FirstTimeConfigListener.FtcEvent t_event;
		private Exception t_ex;

		public NotifyThread(FirstTimeConfigListener listener,
				FirstTimeConfigListener.FtcEvent event) {
			this.m_listener = listener;
			this.t_event = event;
			this.t_ex = null;
			//Thread t = new Thread(this);
			//t.start();
		}

		public NotifyThread(FirstTimeConfigListener listener, Exception ex) {
			this.m_listener = listener;
			this.t_event = FirstTimeConfigListener.FtcEvent.FTC_ERROR;
			this.t_ex = ex;
			//Thread t = new Thread(this);
			//t.start();
		}

		public void run() {
			try {
				//if (this.m_listener != null)
					//this.m_listener.onFirstTimeConfigEvent(this.t_event,
							//this.t_ex);
			} catch (Exception localException) {
			}
		}
	}

	private int getRandomNumber() {
		int num = new Random().nextInt(65536);
		if (num > 10000)
			return num;
		else
			return 15000;
	}

}