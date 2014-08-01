package constellation.app.functions.tools;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.lang.String.format;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ThreadPool;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.util.OSPlatformUtil;

/**
 * Constellation System Information Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class SystemInformationDialog extends CxDialog {
	// icon declarations
	private static final CxContentManager cmgr	= CxContentManager.getInstance();
	private static final Icon TRASH_ICON		= cmgr.getIcon( "images/informationbar/trashcan.gif" );
	private static final Icon APPLE_LOGO_ICON	= cmgr.getIcon( "images/logo/apple.png" );
	private static final Icon FREEBSD_LOGO_ICON	= cmgr.getIcon( "images/logo/freebsd.png" );
	private static final Icon JAVA_LOGO_ICON	= cmgr.getIcon( "images/logo/java.gif" );
	private static final Icon LINUX_LOGO_ICON	= cmgr.getIcon( "images/logo/linux.gif" );
	private static final Icon WINDOWS_LOGO_ICON	= cmgr.getIcon( "images/logo/windows.png" );
	
	// static constants
	private static final String BLANK 			= "--";
	private static SystemInformationDialog instance = null;
	private static final double MEGABYTE = 1024 * 1024;
	
	// internal fields
	private final ThreadPool threadPool;
	private CxMemoryMeter memoryMeter;
	private JLabel maxMemoryLabel;
	private JLabel totalMemoryLabel;

	/**
	 * Default constructor
	 */
	private SystemInformationDialog( final ApplicationController controller ) {
		super( controller, "System Information" );
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setContentPane( createContentPane() );
		super.setCursor( getPredefinedCursor( HAND_CURSOR ) );
		super.setResizable( true );
		super.pack();
		super.setLocation( controller.getLowerRightAnchorPoint( this ) );
		
		// get the thread pool instance
		this.threadPool = controller.getThreadPool();
		
		// create an update task
		threadPool.schedule( new MemoryUpdateTask(), 10000L );
		
		// update the dialog
		updateMemory();
	}
	
	/** 
	 * Returns the singleton instance of the class
	 * @param controller the given {@link ApplicationController function controller}
	 * @return the singleton instance of the {@link SystemInformationDialog class}
	 */
	public static SystemInformationDialog getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new SystemInformationDialog( controller );
		}
		return instance;
	}
	
	/** 
	 * Updates the system monitor
	 */
	public void updateMemory() {
		// get the memory information
		final Runtime runtime		= Runtime.getRuntime();
		final double maxMemory		= ( (double)runtime.maxMemory() / MEGABYTE );
		final double totalMemory 	= ( (double)runtime.totalMemory() / MEGABYTE );
		final double allocatedPct	= 100.0d * ( totalMemory / maxMemory );
		
		// update the labels
		maxMemoryLabel.setText( format( "%.1f MB", maxMemory ) );
		totalMemoryLabel.setText( format( "%.1f MB (%.1f%%)", totalMemory, allocatedPct ) );
		
		// update the memory meter
		memoryMeter.update();
	}
	
	/** 
	 * Constructs the content pane
	 */
	private JComponent createContentPane() {
		final JLabel osLabel;
		
		// get the host information
		final HostInfo hostInfo = getHostInformation();
		
		// create the content pane
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 3, 6, 3, 6 );
		cp.gbc.fill = GridBagConstraints.HORIZONTAL;
		int row = -1;
		
		// row #1
		cp.attach( 0, ++row, new JLabel( "Host name" ) );
		cp.attach( 1,   row, new JLabel( hostInfo.getHostName() ) );
		
		// row #1
		cp.attach( 0, ++row, new JLabel( "IP Address" ) );
		cp.attach( 1,   row, new JLabel( hostInfo.getHostIP() ) );
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Processors" ) );
		cp.attach( 1,   row, new JLabel( format( "%d", hostInfo.getAvailableProcessors() ) ) );
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Operating System" ) );
		cp.attach( 1,   row, osLabel = new JLabel( hostInfo.getOperatingSystemInfo() ) );
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Java Version" ) );
		cp.attach( 1,   row, new JLabel( hostInfo.getJavaVersion() ) );
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "JVM Information" ) );
		cp.attach( 1,   row, new JLabel( hostInfo.getJvmName() ) );
		
		// row #3
		cp.attach( 0, ++row, new JLabel( "Maximum Memory" ) );
		cp.attach( 1,   row, maxMemoryLabel = new JLabel( BLANK ) );
		
		// row #4
		cp.attach( 0, ++row, new JLabel( "Allocated Memory" ) );
		cp.attach( 1,   row, totalMemoryLabel = new JLabel( BLANK ) );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Memory Utilization" ) );
		cp.attach( 1,   row, memoryMeter = new CxMemoryMeter(), GridBagConstraints.NORTHWEST );
		
		// set the OS Logo
		setLogo( osLabel );
		
		return cp;
	}
	
	/**
	 * Sets the platform-specific image in the given label
	 * @param osLabel the given {@link JLabel label}
	 */
	private void setLogo( final JLabel osLabel ) {
		// is it FreeBSD?
		if( OSPlatformUtil.isFreeBsdOS() ) {
			osLabel.setIcon( FREEBSD_LOGO_ICON );
		}
		
		// is it LinuxOS?
		else if( OSPlatformUtil.isLinuxOS() ) {
			osLabel.setIcon( LINUX_LOGO_ICON );
		}
		
		// is it MacOS?
		else if( OSPlatformUtil.isMacOS() ) {
			osLabel.setIcon( APPLE_LOGO_ICON );
		}
		
		// is it Microsoft Windows?
		else if( OSPlatformUtil.isWindowsOS() ) {
			osLabel.setIcon( WINDOWS_LOGO_ICON );
		}
		
		// use the default Java icon
		else {
			osLabel.setIcon( JAVA_LOGO_ICON );
		}
	}
	
	/**
	 * Returns the system's host name
	 * @return the system's host name
	 */
	private HostInfo getHostInformation() {		
		final HostInfo info = new HostInfo();
		
		// get the runtime information
		final Runtime runtime = Runtime.getRuntime();
		info.setAvailableProcessors( runtime.availableProcessors() );
		
		// get operating system information
		info.setOperatingSystemInfo( format( "%s %s", 
				System.getProperty( "os.name" ),
				System.getProperty( "os.version" ) ) );
		
		// get java information
		info.setJavaVersion( System.getProperty( "java.version" ) );
		info.setJvmName( System.getProperty( "java.vm.name" ) );
		
		try {
			// request the local host's information
			final InetAddress inetAddress = InetAddress.getLocalHost();
			
			// get the host name and IP address
			info.setHostName( inetAddress.getHostName() );
			info.setHostIP( inetAddress.getHostAddress() );
		
			/*
			// get the network interfaces
			for( final Enumeration<NetworkInterface> netinfs = NetworkInterface.getNetworkInterfaces(); netinfs.hasMoreElements(); ) {
				final NetworkInterface netinf = netinfs.nextElement();
				System.err.printf( "interface name '%s'\n", netinf.getName() );
				System.err.printf( "display name '%s'\n", netinf.getDisplayName() );
				
				// get the IP addresses
				for( final Enumeration<InetAddress> inetAddresses = netinf.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
					final InetAddress ip = inetAddresses.nextElement();
					System.err.printf( "Canonical Host Name %s\n", ip.getCanonicalHostName() );
					System.err.printf( "Host Address %s\n", ip.getHostAddress() );
				}
				System.err.println();
			}*/
		} 
		catch( final Exception e ) {
			e.printStackTrace();
		}
	
		/*
		for( final Object key : System.getProperties().keySet() ) {
			System.err.printf( "## '%s' = '%s'\n", key, System.getProperties().get( key ) );
		}*/
		
		return info; 
	}
	
	/** 
	 * Constellation Memory Meter
	 * @author lawrence.daniels@gmail.com
	 */
	public class CxMemoryMeter extends CxPanel {
		private final JProgressBar memoryMeter;
		
		/** 
		 * Default constructor
		 */
		public CxMemoryMeter() {
			super.gbc.anchor = GridBagConstraints.CENTER;
			super.gbc.fill = GridBagConstraints.VERTICAL;
			
			// construct the memory meter component
			memoryMeter = new JProgressBar();
			
			// construct the garbage collection button
			final JButton gcButton = new JButton( TRASH_ICON );
			gcButton.addActionListener( new GarbageCollectionActionTask() );
			gcButton.setToolTipText( "Click to force garbage collection" );
			gcButton.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
			gcButton.setContentAreaFilled( false );
			gcButton.setBorderPainted( false );
			
			// construct the layout
			int col = -1;
			super.attach( ++col, 0, memoryMeter );
			super.attach( ++col, 0, gcButton );
		}
		
		/**
		 * Updates the memory meter
		 */
		public void update() {
			// get the memory information
			final Runtime runtime	= Runtime.getRuntime();
			final int freeMemory	= (int)( runtime.freeMemory() / 1024 );
			final int totalMemory 	= (int)( runtime.totalMemory() / 1024 );
			final int usedMemory  	= totalMemory - freeMemory;
			final double usedMemPct	= 100.0d * ( (double)usedMemory / (double)totalMemory );
			
			// update the memory meter
			memoryMeter.setMaximum( totalMemory );
			memoryMeter.setValue( usedMemory );
			memoryMeter.setToolTipText( format( "%d of %dK used (%.1f%%)", usedMemory, totalMemory, usedMemPct ) );
		}
	}
	
	/**
	 * Garbage Collection Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class GarbageCollectionActionTask implements ActionListener, Runnable {
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			threadPool.queue( this );
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// request a garbage collection
			System.gc();
			
			// update the memory meter
			updateMemory();
		}
	}
	
	/** 
	 * Memory Meter Update Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class MemoryUpdateTask extends TimerTask {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			updateMemory();
		}
	}
	
	/**
	 * Represents the local system's host information
	 * @author lawrence.daniels@gmail.com
	 */
	private class HostInfo {
		private int availableProcessors;
		private String hostName;
		private String hostIP;
		private String operatingSystemInfo;
		private String javaVersion;
		private String jvmName;
		
		/**
		 * Default Constructor
		 */
		public HostInfo() {
			super();
		}

		/**
		 * @return the availableProcessors
		 */
		public int getAvailableProcessors() {
			return availableProcessors;
		}

		/**
		 * @param availableProcessors the availableProcessors to set
		 */
		public void setAvailableProcessors(int availableProcessors) {
			this.availableProcessors = availableProcessors;
		}

		/**
		 * @return the hostName
		 */
		public String getHostName() {
			return hostName;
		}

		/**
		 * @param hostName the hostName to set
		 */
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}

		/**
		 * @return the hostIP
		 */
		public String getHostIP() {
			return hostIP;
		}

		/**
		 * @param hostIP the hostIP to set
		 */
		public void setHostIP(String hostIP) {
			this.hostIP = hostIP;
		}

		/**
		 * @return the javaVersion
		 */
		public String getJavaVersion() {
			return javaVersion;
		}

		/**
		 * @param javaVersion the javaVersion to set
		 */
		public void setJavaVersion(String javaVersion) {
			this.javaVersion = javaVersion;
		}

		/**
		 * @return the jvmName
		 */
		public String getJvmName() {
			return jvmName;
		}

		/**
		 * @param jvmName the jvmName to set
		 */
		public void setJvmName(String jvmName) {
			this.jvmName = jvmName;
		}

		/**
		 * @return the operatingSystemInfo
		 */
		public String getOperatingSystemInfo() {
			return operatingSystemInfo;
		}

		/**
		 * @param operatingSystemInfo the operatingSystemInfo to set
		 */
		public void setOperatingSystemInfo(String operatingSystemInfo) {
			this.operatingSystemInfo = operatingSystemInfo;
		}
		
	}
	
}
