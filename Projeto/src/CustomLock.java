public class CustomLock {
    private boolean isLocked = false;
    private Thread lockingThread = null;

    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait(); // Espera até que a tranca seja liberada
        }
        isLocked = true;
        lockingThread = Thread.currentThread();
    }

    public synchronized void unlock() {
        if (Thread.currentThread() != lockingThread) {
            throw new IllegalMonitorStateException("A thread que está tentando liberar a tranca não é a que a adquiriu.");
        }
        isLocked = false;
        lockingThread = null;
        notify(); // Notifica uma thread em espera
    }
}