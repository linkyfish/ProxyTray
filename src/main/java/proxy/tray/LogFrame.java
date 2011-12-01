package proxy.tray;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
public class LogFrame extends JFrame {

	JTextArea text;

	public LogFrame() {
		setTitle("Proxy Tray Log");
		setBounds(100, 100, 800, 600);

		try {
			setIconImage(ImageIO.read(new File("icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setLayout(new BorderLayout());
		text = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(text);

		add(scrollPane, BorderLayout.CENTER);
		JButton refresh = new JButton("刷新");
		refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshLog();
			}
		});
		add(refresh, BorderLayout.SOUTH);
	}

	public void refreshLog() {
		FileReader reader = null;
		try {
			reader = new FileReader("proxy.log");
			text.setText(IOUtils.toString(reader));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
