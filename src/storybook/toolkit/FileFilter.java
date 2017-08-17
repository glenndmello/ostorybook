package storybook.toolkit;

import java.io.File;

public class FileFilter extends javax.swing.filechooser.FileFilter {
	public static String H2[] = {".h2.db", ".mv.db"},
		HTML[] = {".htm", ".html"},
		PNG = ".png",
		ODT = ".odt",
		XML = ".xml";

	private String extension;
    private String[] extensions;
	private String description;
	
	public FileFilter(String ext, String desc) {
		extension=ext;
		description=desc;
	}

	public FileFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description;
    }
	
	@Override
    public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		if (extension==null) {
			for (String x:extensions) {
				if (file.getName().endsWith(x)) return(true);
			}
			return(false);
		}
        return file.getName().endsWith(extension);
    }
	@Override
    public String getDescription() {
        return description;
    }
}
