package constellation.util;

import java.awt.Toolkit;

/**
 * This class contains utilities for Operating System
 * depending settings, information, and tasks
 * @author lawrence.daniels@gmail.com
 */
public class OSPlatformUtil {
	private static final String osName;
	private static final boolean macOS;
	private static final boolean freeBsdOS;
	private static final boolean linuxOS;
	private static final boolean windowsOS;
	private static final boolean windowsXP;
		
	/**
	 * Default constructor
	 */
	static {
		osName 		= System.getProperty( "os.name" );
		freeBsdOS	= ( osName != null ) && osName.toLowerCase().contains( "freebsd" );
		linuxOS		= ( osName != null ) && osName.toLowerCase().contains( "linux" );
		macOS 		= ( osName != null ) && osName.toLowerCase().startsWith( "mac" ); 
		windowsOS	= ( osName != null ) && osName.toLowerCase().contains( "windows" );
		windowsXP	= ( osName != null ) && osName.toLowerCase().contains( "windowsxp" );
	}
	
	/**
	 * Returns the meta key
	 * @return  the meta key
	 */
	public static int getMetaKey() {
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}
	
	/**
	 * Returns the operating system name
	 * @return the operating system name
	 */
	public static String getOperatingSystemName() {
		return osName;
	}
	
	/**
	 * Indicates whether the host operating system is FreeBSD 
	 * @return true, if the host operating system is FreeBSD
	 */
	public static boolean isFreeBsdOS() {
		return freeBsdOS;
	}
	
	/**
	 * Indicates whether the host operating system is LinuxOS
	 * @return true, if the host operating system is LinuxOS
	 */
	public static boolean isLinuxOS() {
		return linuxOS;
	}
	
	/**
	 * Indicates whether the host operating system is MacOS
	 * @return true, if the host operating system is MacOS
	 */
	public static boolean isMacOS() {
		return macOS;
	}
	
	/**
	 * Indicates whether the host operating system is Microsoft Windows
	 * @return true, if the host operating system is Microsoft Windows
	 */
	public static boolean isWindowsOS() {
		return windowsOS;
	}
	
	/**
	 * Indicates whether the host operating system is Microsoft Windows XP
	 * @return true, if the host operating system is Microsoft Windows XP
	 */
	public static boolean isWindowsXP() {
		return windowsXP;
	}
	
}
