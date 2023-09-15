import javax.swing.*;
import java.awt.*;

class WorkerThread extends Thread {
    private final int increment;
    private final JSlider slider;
    private final JLabel statusLabel;

    public WorkerThread(int increment, JSlider slider, JLabel statusLabel) {
        this.increment = increment;
        this.slider = slider;
        this.statusLabel = statusLabel;
    }

    @Override
    public void run() {
        Main.SEMAPHORE = 1;
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Thread Busy");
        while (!interrupted()) {
            int val = slider.getValue();
            if ((val > 10 && increment < 0) || (val < 90 && increment > 0))
                slider.setValue(val + increment);
        }
        Main.SEMAPHORE = 0;
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setText("Thread Free");
    }
}

class ThreadControlPanel extends JPanel {
    private WorkerThread thread;

    public ThreadControlPanel(String threadName, int inc, int priority, JSlider slider, JLabel statusLabel) {


        JButton startButton = new JButton("Start " + threadName);
        JButton stopButton = new JButton("Stop " + threadName);
        startButton.setBackground(Color.GREEN);
        stopButton.setBackground(Color.RED);
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> {
            if (thread == null || !thread.isAlive()) {
                thread = new WorkerThread(inc, slider, statusLabel);
                thread.setPriority(priority);
                thread.start();
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
            }
        });

        stopButton.addActionListener(e -> {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
            }
        });

        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(startButton);
        add(stopButton);
    }
}

public class Main {
    static volatile int SEMAPHORE = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = createFrame();
            JSlider slider = createSlider();
            JLabel statusLabel = createStatusLabel();

            JPanel panel = createMainPanel(slider, statusLabel);
            panel.add(createThreadControlPanel("Thread 1", 1, Thread.MIN_PRIORITY, slider, statusLabel));
            panel.add(createThreadControlPanel("Thread 2", -1, Thread.MAX_PRIORITY, slider, statusLabel));

            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Threaded Slider");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        return frame;
    }

    private static JSlider createSlider() {
        JSlider slider = new JSlider(0, 100);
        slider.setBackground(Color.WHITE);
        return slider;
    }

    private static JLabel createStatusLabel() {
        JLabel statusLabel = new JLabel("Thread Free");
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return statusLabel;
    }

    private static JPanel createMainPanel(JSlider slider, JLabel statusLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(slider);
        panel.add(statusLabel);
        return panel;
    }

    private static ThreadControlPanel createThreadControlPanel(
            String threadName, int inc, int priority, JSlider slider, JLabel statusLabel) {
        return new ThreadControlPanel(threadName, inc, priority, slider, statusLabel);
    }
}
