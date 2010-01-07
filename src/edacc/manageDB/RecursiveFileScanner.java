/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author rretz
 */
public class RecursiveFileScanner {
	String fileExtension;

	/**
	 * Create a RecursiveFileScanner, which search for files with the given file-extension.
	 * @param fileExtension the file-extension which the has to search for
	 */
	public RecursiveFileScanner(String fileExtension){
		this.fileExtension = fileExtension;
	}

	/**
	 * Search recursive for Files with the fileExtension in all subdirectories of the root.
	 * @param path initiate the root of the search.
	 * @return LinkedList <File> of all filedirectories, which where found or null if no File is found.
	 */
	public Vector<File> searchFileExtension(File root)throws IOException, FileNotFoundException{
		Vector <File> files = new Vector<File>();
		try{
                    if(root.isDirectory()){
                            File[] children = root.listFiles();
                            for(int i = 0; i < children.length; i++){
                                    files.addAll(searchFileExtension(children[i]));
                            }
                            return files;
                    }
                    else if(root.isFile() && root.getAbsolutePath().endsWith(fileExtension)){
                            files.add(root);
                            return files;
                    }
                }catch ( FileNotFoundException e ){
                  throw new FileNotFoundException();
                }
                catch ( IOException e ){
                  throw new IOException();
                }
		return files;
	}
}
