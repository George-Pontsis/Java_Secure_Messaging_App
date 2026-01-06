package com.securemessaging.obfuscation;
import java.util.*;
import java.util.concurrent.*;

public class Obfuscator {                                                                                   //Hides real traffic patterns from attackers

    private static final int FIXED_RATE_MS = 1000;                                                          //Interval for sending dummy traffic (1 second)
    private static final Random random = new Random();                                                      //Generates random delays
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);           //Runs scheduled tasks in the background
    private String anonymousId;                                                                             //temporary anonymous identifier

    public Obfuscator() {                                                                                   //Constructor initialize obfuscator
        this.anonymousId = generateAnonymousId();                                                           //Creates Random ID to avoid real identity
    }

    private String generateAnonymousId() {                                                                  //Creates a short anonymous identifier
        return UUID.randomUUID().toString().substring(0, 8);                                                //Creates globally unique value, convert UUID to string and using only the first 8 char
    }

    public String getAnonymousId() {                                                                        //Returns anonymous identifier
        return anonymousId;                                                                                 //Used instead of real identity
    }

    public void addTimingJitter(Runnable sendAction) {                                                      //Adds random delay before sending message
        int delay = random.nextInt(500) + 100;                                                        //Create random delay
        scheduler.schedule(sendAction, delay, TimeUnit.MILLISECONDS);                                       //Schedule message sending
    }

    public void startDummyTraffic(com.securemessaging.transport.SocketCommunicator comm) {                  //Starts sending fake traffic
        scheduler.scheduleAtFixedRate(() -> {                                                               //Run task at fixed time intervals
            try {
                String dummy = "DUMMY:" + System.currentTimeMillis();                                       //Create fake packet with time stamp
                comm.sendPacket(dummy);                                                                     //Send dummy to confuse observers
            } catch (Exception e) {                                                                         //Catch Network errors
                e.printStackTrace();                                                                        //Print error for debugging
            }
        }, 0, FIXED_RATE_MS, TimeUnit.MILLISECONDS);                                              //Start, repeat every second
    }

    public void shutdown() {                                                                                //Stops obfuscation tasks
        scheduler.shutdown();                                                                               //Clearly stop schedule thread
    }
}
