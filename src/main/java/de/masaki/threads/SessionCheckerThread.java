package de.masaki.threads;

import de.masaki.KeyAuth;

public class SessionCheckerThread extends Thread{
    private final KeyAuth keyAuth;
    private final long interval;

    private final String exitMessage;

    public SessionCheckerThread(KeyAuth keyAuth, long interval, String exitMessage) {
        this.keyAuth = keyAuth;
        this.interval = interval;
        this.exitMessage = exitMessage;
    }

    public SessionCheckerThread(KeyAuth keyAuth, long interval) {
        this(keyAuth, interval, "Session invalid! Please restart");
    }

    @Override
    public void run() {
        while (true) {
            boolean status = keyAuth.checkSession().isSuccess();

            if(!status){
                System.out.println(exitMessage);
                System.exit(0);
            }

            try {
                Thread.sleep(this.interval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
