/*******************************************************************************
 * Copyright (c) 2013 Marian-Andrei RIZOIU.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Marian-Andrei RIZOIU - initial API and implementation
 ******************************************************************************/
package lab.eric.datafetcher.utils;

import java.io.File;

/**
 * Gathers different / old methods that are still in use.
 * 
 * @author Marian-Andrei RIZOIU
 *
 */
public class Miscellaneous {

	/**
	 * Verifies if a folder exists. If not, it creates it.
	 * @param name name of folder to verify and/or create
	 */
	public static void verifyCreateFolder(String name) {
		File folder = new File(name);
		if (!folder.exists())
			folder.mkdir();
		// if a file exists with the same name, rename it and create the folder
		if (!folder.isDirectory()) {
			File renamed = new File(name + "_rename");
			folder.renameTo(renamed);
			folder = new File(name);
			folder.mkdir();
		}
	}

	/**
	 * Deletes a file or a folder recursively (for folders) for a name
	 * 
	 * @param name
	 *            absolute path of the file or folder to delete
	 */
	public static void deleteFileOrFolderRecursively(String name) {
		File folder = new File(name);
		Miscellaneous.deleteFileOrFolderRecursively(folder);
	}

	/**
	 * Deletes a file or a folder recursively (for folders) for a File denoted
	 * file
	 * 
	 * @param folder
	 *            File instance to delete
	 */
	public static void deleteFileOrFolderRecursively(File folder) {
		
		if (folder.exists()) {
			// if the folder exists, check if it is a file and delete it
			if ( folder.isFile() ) {
				folder.delete();
				return;
			}
			
			// if it is a folder, get the filelist and delete everything in it
			File[] files = folder.listFiles();
			for ( int i = 0; i < files.length ; i++)
				deleteFileOrFolderRecursively(files[i]);
			
			// and now delete the folder himself
			folder.delete();
		}
	}

}
