package com.securemessaging.session;
import com.securemessaging.Encryption.KeyManager;
import com.securemessaging.transport.SocketCommunicator;
import javax.crypto.*;
import java.util.concurrent.*;

public class SessionManager {                                                                           //Manages Session keys
    private static final String DH_ALGO = "DH";                                                         //Diffie-Hellman algorithm name
    private static final int DH_KEY_SIZE = 2048;                                                        //Secure key size for DH
    private SecretKey sharedAESKey;                                                                     //Shared AES key used for message encryption
    private ScheduledExecutorService refresher = Executors.newScheduledThreadPool(1);        //Background task for key refreshing

    public void performKeyExchange(SocketCommunicator comm) throws Exception {                          //Establishes or refreshes session key
        sharedAESKey = KeyManager.generateAESKey();                                                     //Generate new random AES session key
    }

    public SecretKey getSharedAESKey() {                                                                //Provides current AES key to other classes
        return sharedAESKey;                                                                            //Used by MessageEncryptor
    }

    public void startKeyRefresh(SocketCommunicator comm) {                                              //Periodically refreshes encryption key
        refresher.scheduleAtFixedRate(() -> {                                                           //Schedule task at fixed intervals
            try {
                performKeyExchange(comm);                                                               //Generate a new AES key
            } catch (Exception e) {                                                                     //Catches crypto or runtime errors
                e.printStackTrace();                                                                    //Print error for debugging
            }
        }, 10, 10, TimeUnit.MINUTES);                                                   //Refresh every 10 minutes
    }

    public void shutdown() {                                                                            //Stops key refresh service
        refresher.shutdown();                                                                           //Cleanly shut down background thread
    }
}
