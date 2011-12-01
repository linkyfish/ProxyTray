package proxy.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ProxyTray {

	private PlinkProcess plink;

	public ProxyTray(PlinkProcess plink) {
		this.plink = plink;
	}

	private PopupMenu createMenu() {
		PopupMenu menu = new PopupMenu();
		MenuItem log = new MenuItem("查看日志");
		MenuItem restart = new MenuItem("重启代理");
		MenuItem exit = new MenuItem("退出");

		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plink.exit();
				System.exit(0);
			}
		});

		restart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					plink.restart();
					plink.monitor();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"错误信息", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		log.addActionListener(new ActionListener() {

			LogFrame log = new LogFrame();

			@Override
			public void actionPerformed(ActionEvent e) {
				log.refreshLog();
				log.setVisible(true);
			}
		});

		menu.add(restart);
		menu.add(log);
		menu.add(exit);

		return menu;
	}

	public void tray() throws AWTException, IOException {
		if (!SystemTray.isSupported()) {
			throw new AWTException("SystemTray is not supported");
		}

		Image image = ImageIO.read(new File("icon.png"));
		TrayIcon trayIcon = new TrayIcon(image.getScaledInstance(16, 16,
				Image.SCALE_AREA_AVERAGING), "ProxyTray", createMenu());
		SystemTray.getSystemTray().add(trayIcon);
	}

	public static void main(String[] args) {
		PlinkProcess plink = null;
		try {
			plink = new PlinkProcess();
			plink.monitor();
			ProxyTray proxyTray = new ProxyTray(plink);
			proxyTray.tray();
		} catch (Exception e) {
			e.printStackTrace();
			if (plink != null) {
				plink.exit();
			}
			JOptionPane.showMessageDialog(null, e.getMessage(), "错误信息",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
