/**
 * AppWindow.java
 * GiveawayGrowl
 *
 * Created by Ed George on 30 Dec 2014
 *
 */
package uk.breedr.notifier.giveaway.gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.dean.jraw.http.NetworkException;
import uk.breedr.notifier.giveaway.Giveaway;
import uk.breedr.notifier.giveaway.gui.utils.TableUtils;
import uk.co.edgeorgedev.notifj.notification.exception.NotificationException;
import uk.co.edgeorgedev.notifj.notification.growl.GrowlNotification;

/**
 * @author edgeorge
 *
 */
public class AppWindow {

	private JFrame frmGiveawayNotifier;
	private Console console;
	private JTable table;

	private final static long DURATION = 2;
	private final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;
	private GiveawayTableModel model;
	private Date date, initialised;

	private Logger logger;

	public Console getConsole() {
		return console;
	}

	public JFrame getFrame() {
		return frmGiveawayNotifier;
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
		runScript();
	}

	/**
	 * 
	 */
	private void runScript() {
		final Timer timer = new Timer();
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				logger.write("Checking Giveaways - elapsed time " + TableUtils.get_duration(initialised, new Date()));

				try{
					updateList(Giveaway.getRecentGiveaways(), date);
				}catch(NetworkException e){
					logger.write("NETWORK ERROR  - " + e.getCode(), Logger.Level.ERROR);
					msgbox("Network Error - Please check your internet connection");
				}catch (Exception e) {
					logger.write(e.getMessage(), Logger.Level.ERROR);
					e.printStackTrace();
					timer.cancel();
				}
			}
		};
		initialised = new Date();
		timer.schedule(task, 0, TIME_UNIT.toMillis(DURATION));
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGiveawayNotifier = new JFrame();
		frmGiveawayNotifier.setResizable(false);
		frmGiveawayNotifier.setTitle("Giveaway Notifier");
		frmGiveawayNotifier.setBounds(100, 100, 520, 340);
		frmGiveawayNotifier.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmGiveawayNotifier.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);

		JMenu mnSettings = new JMenu("Settings");
		mnSettings.setMnemonic('s');
		mnFile.add(mnSettings);

		JMenuItem mntmNotificationOptions = new JMenuItem("Notification Options");
		mnSettings.add(mntmNotificationOptions);

		JCheckBoxMenuItem chckbxmntmVerboseLogging = new JCheckBoxMenuItem("Verbose Logging");
		mnSettings.add(chckbxmntmVerboseLogging);

		final JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("JSON File", "json");
		fileChooser.setFileFilter(filter);
		JMenuItem mntmExport = new JMenuItem("Export");
		mntmExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		mntmExport.addActionListener(new ActionListener() {

			private final String suffix = ".json";

			@Override
			public void actionPerformed(ActionEvent e) {

				if (fileChooser.showSaveDialog(frmGiveawayNotifier) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(suffix)){
						file = new File(fileChooser.getSelectedFile() + suffix);
					}
					boolean export = TableUtils.export(file, model.getGiveaways(), logger);
					if(!export){
						msgbox("Error writing to file " + file.getAbsolutePath());
					}else{
						msgbox("Exported to " + file.getAbsolutePath());
					}
				}
			}
		});
		mnFile.add(mntmExport);

		JMenuItem mntmAbout = new JMenuItem("About");
		menuBar.add(mntmAbout);
		frmGiveawayNotifier.getContentPane().setLayout(new BoxLayout(frmGiveawayNotifier.getContentPane(), BoxLayout.X_AXIS));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		frmGiveawayNotifier.getContentPane().add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("List", null, panel, null);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		final JTextArea lblNewLabel = new JTextArea("Select row for Info");
		lblNewLabel.setEditable(false);

		panel.add(new JScrollPane(lblNewLabel));

		table = new JTable();
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(new JScrollPane(table));

		JPanel consolePanel = new JPanel();
		tabbedPane.addTab("Console", null, consolePanel, null);
		consolePanel.setLayout(new BoxLayout(consolePanel, BoxLayout.PAGE_AXIS));

		console = new Console();
		logger = new Logger(console);
		console.setFont(new Font("Courier", Font.PLAIN, 13));
		console.setEditable(false);
		consolePanel.add(new JScrollPane(console));
		
		try{
			model = new GiveawayTableModel();
			table.setModel(model);
			TableUtils.setJTableColumnsPercentage(table, 480, new double[]{55, 5, 20, 20});
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent event) {
					lblNewLabel.setText(model.getGiveawayAtRow(table.getSelectedRow()).prettyPrint());
				}
			});
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					JTable list = (JTable) evt.getSource();
					if (evt.getClickCount() == 2) {
						if(Desktop.isDesktopSupported()){
							try {
								Desktop.getDesktop().browse(model.getGiveawayAtRow(list.getSelectedRow()).getUrl().toURI());
							} catch (IOException | URISyntaxException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}catch(Exception e){
			logger.write(e.getMessage(), Logger.Level.ERROR);
		}

		JButton btnClearConsole = new JButton("Clear Log");
		btnClearConsole.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnClearConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.clear();
			}
		});

		Component verticalStrut = Box.createVerticalStrut(2);
		consolePanel.add(verticalStrut);
		btnClearConsole.setMnemonic('c');
		consolePanel.add(btnClearConsole);
	}


	private void updateList(List<Giveaway> giveaways, Date date) throws NotificationException {

		GrowlNotification notification = new GrowlNotification("Breedr").sticky();
		notification.open();

		Date latest = null;
		for(int i = 0; i < giveaways.size(); i++){
			Giveaway giveaway = giveaways.get(i);
			if(i == 0){
				latest = giveaway.getSubmission().getCreated();
			}

			if(date == null || giveaway.getSubmission().getCreated().compareTo(date) > 0){
				Date now = new Date();
				String created = TableUtils.get_duration(giveaway.getSubmission().getCreatedUtc(), now);
				notification.callbackUrl(giveaway.getSubmission().getShortURL());
				notification.show("New Giveaway - " + created, giveaway.getSubmission().getTitle());

				model.addRow(giveaway);

				logger.write(Giveaway.DATE_FORMAT.format(now) +  " - " + giveaway.getType().toString() + " - " + giveaway.getSubmission().getTitle() + " ("+ created +")");

			}
		}
		notification.close();
		this.date = latest;
	}

	private void msgbox(String s){
		JOptionPane.showMessageDialog(null, s);
	}

}
