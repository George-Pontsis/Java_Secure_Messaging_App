package com.securemessaging.session;
import com.securemessaging.Encryption.KeyManager;
import com.securemessaging.transport.SocketCommunicator;
import javax.crypto.*;
import java.util.concurrent.*;

public class SessionManager {
    private static final String DH_ALGO = "DH";
    private static final int DH_KEY_SIZE = 2048;
    private SecretKey sharedAESKey;
    private ScheduledExecutorService refresher = Executors.newScheduledThreadPool(1);

    public void performKeyExchange(SocketCommunicator comm) throws Exception {
        // Simplified DH exchange (in practice, exchange public keys securely)
        sharedAESKey = KeyManager.generateAESKey(); // Placeholder
    }

    public SecretKey getSharedAESKey() {
        return sharedAESKey;
    }

    public void startKeyRefresh(SocketCommunicator comm) {
        refresher.scheduleAtFixedRate(() -> {
            try {
                performKeyExchange(comm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 10, 10, TimeUnit.MINUTES);
    }

    public void shutdown() {
        refresher.shutdown();
    }
}
