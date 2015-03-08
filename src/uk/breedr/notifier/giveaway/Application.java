/**
 * Application.java
 * GiveawayGrowl
 *
 * Created by Ed George on 25 Dec 2014
 *
 */
package uk.breedr.notifier.giveaway;

import java.awt.EventQueue;

import javax.swing.UIManager;

import uk.breedr.notifier.giveaway.gui.AppWindow;


/**
 * @author edgeorge
 *
 */
public class Application {

	public static void main(String[] args) {
		 
		// set the name of the application menu item
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Giveaway Notifier");

		// set the look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
