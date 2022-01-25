package com.feverbrainstudios.ffae_installer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

public class FFAEInstaller {
	private static JFrame mainWindow;		

	public static void main(String[] arg) {
		
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e3) {
			e3.printStackTrace();
		} catch (InstantiationException e3) {
			e3.printStackTrace();
		} catch (IllegalAccessException e3) {
			e3.printStackTrace();
		} catch (UnsupportedLookAndFeelException e3) {
			e3.printStackTrace();
		}
		
		mainWindow = new JFrame("FFAE Installer");
		mainWindow.setLayout(new GridLayout(1,2));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(2,2));
		mainWindow.add(leftPanel);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(2,2));
		mainWindow.add(rightPanel);

		JTextField ffZipPathTextField = new JTextField();
		ffZipPathTextField.setSize(460, 24);
		leftPanel.add(ffZipPathTextField);

		JTextField sfa3zipPathTextField = new JTextField();
		sfa3zipPathTextField.setSize(460, 24);
		leftPanel.add(sfa3zipPathTextField);

		JButton selectFFZipButton = new JButton("Select FF Zip");
		rightPanel.add(selectFFZipButton);
		
		JButton patchButton = new JButton("Patch");
		rightPanel.add(patchButton);

		JButton selectSFA3ZipButton = new JButton("Select SFA3 Zip");
		rightPanel.add(selectSFA3ZipButton);

		selectFFZipButton.addActionListener(new ZipFileChooserListener(ffZipPathTextField));	
		selectSFA3ZipButton.addActionListener(new ZipFileChooserListener(sfa3zipPathTextField));	
		
		patchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File ffZipFile = new File(ffZipPathTextField.getText());
				if (!ZIP_FILTER.accept(ffZipFile)) {
					JOptionPane.showMessageDialog(mainWindow, "Please select a valid Final Fight zip file.");
					return;
				} else if (!ffZipFile.exists()) {
					JOptionPane.showMessageDialog(mainWindow, "Final Fight zip file does not exist.");
					return;
				}

				File sfa3ZipFile = new File(sfa3zipPathTextField.getText());
				if (!ZIP_FILTER.accept(sfa3ZipFile)) {
					JOptionPane.showMessageDialog(mainWindow, "Please select a valid Street Fighter Alpha 3 zip file.");
					return;
				} else if (!sfa3ZipFile.exists()) {
					JOptionPane.showMessageDialog(mainWindow, "Street Fighter Alpha 3 zip file does not exist.");
					return;
				}

				try {
					String workDir = System.getProperty("user.dir");
					
					execAndPrintToConsole(workDir + "\\make_work_directory.bat");
					
					String workDirString = workDir + "\\build1234abcd\\";
					
					boolean foundAll =  unzipMatchingCRCS(ffZipFile.getAbsolutePath(), workDirString, FF_ROM_CRCS_SET, FF_ROM_CRCS_TO_NAMES);
					foundAll &=  unzipMatchingCRCS(sfa3ZipFile.getAbsolutePath(), workDirString, SFA3_ROM_CRCS_SET, SFA3_ROM_CRCS_TO_NAMES);

					if (!foundAll) {
						JOptionPane.showMessageDialog(mainWindow, "The following CRCs were incorrect:\r\n");
						execAndPrintToConsole("delete_work_directory.bat");
						return;
					}

					execAndPrintToConsole("java -jar RomMangler.jar combine split_cfgs\\final_fight_split.cfg " + workDirString + "combined\\ffight.bin");
					execAndPrintToConsole("java -jar RomMangler.jar combine split_cfgs\\final_fight_gfx_split.cfg " + workDirString + "combined\\ffight_gfx.bin");
					execAndPrintToConsole("java -jar RomMangler.jar combine split_cfgs\\sfa3_audio_split.cfg " + workDirString + "combined\\sfa3_audio.bin");

					execAndPrintToConsole("java -jar RomMangler.jar split split_cfgs\\final_fight_separate_gfx_layers_split.cfg " + workDirString + "combined\\ffight_gfx.bin");
					execAndPrintToConsole("java -jar RomMangler.jar combine split_cfgs\\final_fight_recombine_gfx_cps2.cfg " + workDirString + "combined\\cps2_gfx\\ffight_gfx_cps2_base.bin");

					execAndPrintToConsole("java -jar RomMangler.jar cps2_reshuffle " + workDirString + "combined\\cps2_gfx\\ffight_gfx_cps2_base.bin " + workDirString + "combined\\cps2_gfx\\ffight_gfx_cps2_base_shuffled.bin");
					
					execAndPrintToConsole("java -jar RomMangler.jar split split_cfgs\\final_fight_gfx_cps2_base_mister_split.cfg " + workDirString + "combined\\cps2_gfx\\ffight_gfx_cps2_base_shuffled.bin");
					
					execAndPrintToConsole("java -jar RomMangler.jar zipdir "+ workDirString + "\\mister\\gfx " + workDir + "\\ffightae_cps2_gfx.zip");

					
					execAndPrintToConsole(workDir + "\\copy_combined_files.bat");
					
					execAndPrintToConsole("liteips.exe ips\\ffae_cps2_prg_patch.ips " + workDirString + "combined_patched\\ffight.bin");
					execAndPrintToConsole("liteips.exe ips\\ffae_cps2_gfx_patch.ips " + workDirString + "combined_patched\\ffight_gfx_cps2.bin");
					execAndPrintToConsole("liteips.exe ips\\ffae_cps2_audio_prg_patch.ips " + workDirString + "combined_patched\\ffight_audio.bin");

					execAndPrintToConsole("java -jar RomMangler.jar split split_cfgs\\mame\\final_fight_cps2_mame_split.cfg " + workDirString + "combined_patched\\ffight.bin");
					execAndPrintToConsole("java -jar RomMangler.jar split split_cfgs\\mame\\final_fight_gfx_cps2_mame_split.cfg " + workDirString + "combined_patched\\ffight_gfx_cps2.bin");
					execAndPrintToConsole("java -jar RomMangler.jar split split_cfgs\\mame\\final_fight_snd_prg_cps2_mame_split.cfg " + workDirString + "combined_patched\\ffight_audio.bin");
					
					execAndPrintToConsole("java -jar RomMangler.jar combine split_cfgs\\darksoft\\final_fight_cps2_darksoft_split.cfg " + workDirString + "darksoft\\ffae.02");
					execAndPrintToConsole("java -jar RomMangler.jar split split_cfgs\\darksoft\\final_fight_gfx_cps2_darksoft_split.cfg " + workDirString + "combined_patched\\ffight_gfx_cps2.bin");
					execAndPrintToConsole("java -jar RomMangler.jar combine split_cfgs\\darksoft\\final_fight_snd_prg_cps2_darksoft_split.cfg " + workDirString + "darksoft\\ffae.01");
					
					
					/*

					execAndPrintToConsole("liteips.exe ffight_hack.ips " + workDirString + "ffight.bin");
					execAndPrintToConsole("liteips.exe ffight_gfx_new.ips " + workDirString + "ffight_gfx.bin");
					
					execAndPrintToConsole("java -jar RomMangler.jar split final_fight_out_split.cfg " + workDirString + "ffight.bin");
					execAndPrintToConsole("java -jar RomMangler.jar split final_fight_gfx_split.cfg " + workDirString + "ffight_gfx.bin");
					
					
					execAndPrintToConsole("java -jar RomMangler.jar zipdir " + workDirString + " " + ffZipFile.getParent() + "\\ffightae.zip");
*/					
				//	execAndPrintToConsole("delete_work_directory.bat");
					
					JOptionPane.showMessageDialog(mainWindow, "Patch created successfully!\r\n\r\nThe patch is located:\r\n\r\n" + ffZipFile.getParent() + "\\ffightae.zip"); 
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainWindow, "There was an error, please check the console.");
					try {
						execAndPrintToConsole("delete_work_directory.bat");
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
		});
		  
		mainWindow.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) { System.exit(0); }
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});

		mainWindow.setVisible(true);
		mainWindow.setSize(600, 112);
		mainWindow.setResizable(false);
	}
	
	private static boolean unzipMatchingCRCS(String zipFile, String path, Set<String> crcsSet, Map<String, String> crcsToNames) {
		int count = 0;
		path = new File(path).getAbsolutePath();
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(zipFile);
			ZipInputStream zipStream = new ZipInputStream(inputStream);
			ZipEntry nextEntry = zipStream.getNextEntry();
			while (nextEntry != null) {
				if (!nextEntry.isDirectory()) {
					String hexString = "0x" + String.format("%08X", nextEntry.getCrc()).toUpperCase();
					if (crcsSet.contains(hexString)) {
						File newFile = new File(path, crcsToNames.get(hexString));
						extractFile(zipStream, newFile);
						count ++;
					}
				}
				zipStream.closeEntry();
				nextEntry = zipStream.getNextEntry();
			}
			zipStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return count == crcsSet.size();
	}
	
    private static void extractFile(ZipInputStream zipIn, File file) throws IOException {
        BufferedOutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            outstream.write(bytesIn, 0, read);
        }
        outstream.flush();
        outstream.close();
    }
	
	private static void execAndPrintToConsole(String command) throws IOException {
		Process exec = Runtime.getRuntime().exec(command);
		InputStreamReader reader = new InputStreamReader(exec.getInputStream());
		BufferedReader buffReader = new BufferedReader(reader);
		String read = buffReader.readLine();
		while(read != null) {
			System.out.println(read);
			read = buffReader.readLine();
		}
	}
	
	private static String execAndReturn(String command) throws IOException {
		String results = "";
		Process exec = Runtime.getRuntime().exec(command);
		InputStreamReader reader = new InputStreamReader(exec.getInputStream());
		BufferedReader buffReader = new BufferedReader(reader);
		String read = buffReader.readLine();
		while(read != null) {
			results += read;
			read = buffReader.readLine();
		}
		return results;
	}
	
	private static final Map<String, String> FF_ROM_CRCS_TO_NAMES = new HashMap<String, String>() {{
        // Program
		put("0xF9A5CE83","ff_36.11f");
        put("0x65F11215","ff_42.11h");
        put("0xE1033784","ff_37.12f");
        put("0x995E968A","ffe_43.12h");
        put("0xC747696E","ff-32m.8h");

        // Graphics
        put("0x9C284108","ff-5m.7a");
        put("0xA7584DFB","ff-7m.9a");
        put("0x0B605E44","ff-1m.3a");
        put("0x52291CD2","ff-3m.5a");
	}};
	
	private static final Set<String> FF_ROM_CRCS_SET = new HashSet<String>() {{
		addAll(FF_ROM_CRCS_TO_NAMES.keySet());
	}};

	private static final Map<String, String> SFA3_ROM_CRCS_TO_NAMES = new HashMap<String, String>() {{
        // Audio Program
		put("0xDE810084","sz3.01");
        put("0x72445DC4","sz3.02");
	}};

	private static final Set<String> SFA3_ROM_CRCS_SET = new HashSet<String>() {{
		addAll(SFA3_ROM_CRCS_TO_NAMES.keySet());
	}};
	
	private static FileFilter ZIP_FILTER = new FileFilter() {
		
		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "Zip File";
		}
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().contains(".zip"); 
		}
	};
	
	private static class ZipFileChooserListener implements ActionListener {
		private JTextField textField;

		public ZipFileChooserListener(JTextField textField) {
			this.textField = textField;
		}
		
		public void actionPerformed(ActionEvent e) {
			JFileChooser zipChooser = new JFileChooser();
			zipChooser.setFileFilter(ZIP_FILTER);
			int returnVal = zipChooser.showOpenDialog(mainWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
			  File file = zipChooser.getSelectedFile();
			  textField.setText(file.getAbsolutePath());
			}
		}
	}
}
