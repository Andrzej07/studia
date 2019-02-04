package workers;

import org.apache.commons.io.FileUtils;
import panels.ProgressPanel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macie on 27/04/2017.
 */
public class RemoveFilesWorker extends SwingWorker<Void, Integer> {
    List<File> files;
    ProgressPanel pp;
    List<File> startList;

    public RemoveFilesWorker(ProgressPanel pp, List<File> files) {
        startList = files;
        this.pp = pp;
        unpackDirs(files);
        if(this.files.size() > 0) pp.setProgressBarMaximum(this.files.size());
        else pp.setProgressBarMaximum(0);
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
            f.delete();
            /*try {
                Thread.sleep(500);
            } catch(InterruptedException e1){
                return null;
            }*/
            publish(i++);
        }
        for(File f : startList) {
            if(f.exists() && f.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException ioe) {
                    System.out.println("Exception on dir delete");
                    return null;
                }
            }
        }
        return null;
    }


    @Override
    public void done() {
        pp.operationFinished();
    }


    private void unpackDirs(List<File> files) {
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
    }
}

