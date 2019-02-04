package workers;

import org.apache.commons.io.FileUtils;
import panels.ProgressPanel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by macie on 27/04/2017.
 */
public class MoveFilesWorker extends SwingWorker<Void, Integer> {
    List<File> files;
    File destination;
    ProgressPanel pp;

    public MoveFilesWorker(ProgressPanel pp, File destination, List<File> files) {
        this.destination = destination;
        this.files = files;

        this.pp = pp;

        //unpackDirs(files);
        pp.setProgressBarMaximum(this.files.size());
    }

    @Override
    protected void process(List<Integer> chunks) {
        int i = chunks.get(chunks.size()-1);
        pp.setProgressBarValue(i);
        System.out.println("Loading progress: " + i);
    }

    @Override
    public Void doInBackground() {
        int i = 1;
        for(File f : files) {
            //f.delete();
            try {
                System.out.println("Moving " + f.getName() + " to " + destination);
                if (f.isDirectory()) {
                    File ff = new File(destination.getCanonicalPath() + "/" + f.getName());
                    FileUtils.copyDirectory(f, ff);
                    FileUtils.deleteDirectory(f);
                } else {
                    FileUtils.copyFile(f, destination);
                    f.delete();
                }

            } catch(IOException ioe) {
                System.out.println("Move file exception: " + ioe.getMessage());
                return null;
            }
            publish(i++);
        }
        return null;
    }


    @Override
    public void done() {
        pp.operationFinished();
    }


/*    private void unpackDirs(List<File> files) {
        List<File> result = new LinkedList<>();
        for(File f : files) {
            result.addAll(unpackDir(f));
        }
        System.out.println("Unpacked dirs result file count:" + result.size());
        this.files = result;
    }

    private List<File> unpackDir(File directory) {
        List<File> result = new LinkedList<>();
        if(directory.isFile()) {
            result.add(directory);
            return result;
        }
        File[] files = directory.listFiles();
        if(files != null) {
            for(File f : files) {
                result.addAll(unpackDir(f));
            }
        }
        return result;
    }*/
}

