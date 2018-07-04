
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class RenameFile {
	public static void main(String[] args) {

		RenameFile rename = new RenameFile();
		rename.renameFileList(rename.fileList("D:\\novel\\download"));

	}

	public File[] fileList(String filename) {
		File folder = new File(filename);
		return folder.listFiles();
	}

	public void renameFileList(File[] listOfFiles) {
		for (File file : listOfFiles) {
			if (file.isFile()) {
				try {
					renameFile(file.getAbsolutePath());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	public void renameFile(String filename) throws UnsupportedEncodingException {
		File file = new File(filename);
		File fileNew = new File(URLDecoder.decode(filename, "UTF-8"));
		if (file.exists())
			file.renameTo(fileNew);
	}
}
