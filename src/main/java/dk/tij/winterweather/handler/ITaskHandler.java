package dk.tij.winterweather.handler;

public interface ITaskHandler {
    void start();
    void stop();
    default void restart() {
        stop();
        start();
    }
}
