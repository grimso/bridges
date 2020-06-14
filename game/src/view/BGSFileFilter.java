package view;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Customized file filter for bridges game file endings <i>*.bgs</i> to use with a
 * {@link JFileChooser}. Extends {@link FileFilter}
 * 
 * @author grimm
 *
 */
class BGSFileFilter extends FileFilter {
	public static final String FILE_ENDING = "bgs";

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals(FILE_ENDING)) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription() {
		return "*." + FILE_ENDING;
	}

	private static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
