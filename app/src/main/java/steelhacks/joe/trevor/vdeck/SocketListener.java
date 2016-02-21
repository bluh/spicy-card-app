package steelhacks.joe.trevor.vdeck;

import android.bluetooth.BluetoothSocket;

public interface SocketListener {
    public void SocketConnect(BluetoothSocket sock);
    public void SocketMessage(BluetoothSocket sock, String message);
    public void SocketDisconnect(BluetoothSocket sock);
}