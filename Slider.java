import javax.swing.*;
import java.awt.*;


class CustomSlider extends JSlider {
    public CustomSlider() {
        super(0, 100);
        setMajorTickSpacing(10);
        setPaintTicks(true);
    }

    public synchronized void increaseValue(int increment) {
        setValue(getValue() + increment);
    }
}

class SliderControlThread extends Thread {
    private final int incrementValue;
    private final CustomSlider customSlider;


    public SliderControlThread(CustomSlider customSlider, int incrementValue) {
        this.customSlider = customSlider;
        this.incrementValue = incrementValue;

        setPriority(Thread.NORM_PRIORITY);
    }

    @Override
    public void run() {
        int count = 0;
        int maxIterations = 1000000;
        while (!isInterrupted()) {
            int value = customSlider.getValue();
            count++;
            if (count > maxIterations) {
                customSlider.increaseValue(incrementValue);
                count = 0;
            }
        }
    }

    public JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        SpinnerModel spinnerModel = new SpinnerNumberModel(getPriority(), Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, 1);
        JSpinner prioritySpinner = new JSpinner(spinnerModel);
        prioritySpinner.addChangeListener(e -> setPriority((int) prioritySpinner.getValue()));

        panel.add(prioritySpinner, gbc);

        return panel;
    }
}

public class Slider {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = createAndShowFrame();
            frame.setVisible(true);
        });
    }

    private static JFrame createAndShowFrame() {
        JFrame frame = new JFrame("Slider Control App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        CustomSlider customSlider = new CustomSlider();
        SliderControlThread thread1 = new SliderControlThread(customSlider, -1);
        SliderControlThread thread2 = new SliderControlThread(customSlider, 1);

        JPanel controlPanel = createControlPanel(customSlider, thread1, thread2);

        frame.add(controlPanel);
        return frame;
    }

    private static JPanel createControlPanel(CustomSlider customSlider, SliderControlThread thread1, SliderControlThread thread2) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        controlPanel.add(customSlider, BorderLayout.NORTH);

        JPanel threadPanel = new JPanel();
        threadPanel.setLayout(new GridLayout(1, 2));

        threadPanel.add(thread1.createControlPanel());
        threadPanel.add(thread2.createControlPanel());

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            thread1.start();
            thread2.start();
            startButton.setEnabled(false);
        });

        controlPanel.add(threadPanel, BorderLayout.CENTER);
        controlPanel.add(startButton, BorderLayout.SOUTH);

        return controlPanel;
    }
}
