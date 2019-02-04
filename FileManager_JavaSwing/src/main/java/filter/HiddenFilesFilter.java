package filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by macie on 26/04/2017.
 */
public class HiddenFilesFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return !file.isHidden();
    }
}
