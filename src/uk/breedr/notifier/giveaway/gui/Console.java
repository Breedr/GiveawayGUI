/**
 * Console.java
 * GiveawayGrowl
 *
 * Created by Ed George on 30 Dec 2014
 *
 */
package uk.breedr.notifier.giveaway.gui;

import javax.swing.JTextArea;

/**
 * @author edgeorge
 *
 */
public class Console extends JTextArea{

	private String prefix = "> ";

	public Console() {
		super();
	}


	@Override
	public void append(String str) {
		String[] strings = str.split("\n");
		for(String string : strings){
			super.append(prefix + string + "\n");
		}
	}

	public void clear(){
		setText("");
	}

	private static final long serialVersionUID = -2841235241190722611L;

}
