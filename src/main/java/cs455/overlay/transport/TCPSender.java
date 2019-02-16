package cs455.overlay.transport;

import cs455.overlay.transport.TCPSender;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
	private Socket socket;
	private DataOutputStream dout;
	public TCPSender(Socket socket) throws IOException {
		this.socket = socket;
		dout = new DataOutputStream(socket.getOutputStream());
	}
	public void sendData(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length;
		dout.writeInt(dataLength);
		dout.write(dataToSend, 0, dataLength);
		dout.flush();
	}

	public static void sendMessage(String key, int type, int numEntries, byte[][] message) throws IOException{
        String[] temp = key.split(":");
        String Address = temp[0];
        int Port = (Integer.parseInt(temp[1]));
        Socket REG_SOCKET = new Socket(Address, Port);
        TCPSender sender = new TCPSender(REG_SOCKET);
		///creates Request message byte array
		byte[] marshaledBytes;

		//Initialize used streams
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =
			new DataOutputStream(new BufferedOutputStream(baOutputStream));

		//insert the deregister request protocol
		dout.writeByte(type);

		dout.writeInt(numEntries);

		for(int i = 0; i < message.length; i++) {
		    if(message[i] != null) {
                dout.writeInt(message[i].length);
                dout.write(message[i]);
            }
		}


		dout.flush();
		marshaledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		//sends request
		sender.sendData(marshaledBytes);
        REG_SOCKET.close();
    }
}

