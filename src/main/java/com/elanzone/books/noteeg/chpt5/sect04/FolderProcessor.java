package com.elanzone.books.noteeg.chpt5.sect04;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderProcessor extends RecursiveTask<List<String>> {

    private static final long serialVersionUID = 1L;

    private String path;
    private String extension;

    public FolderProcessor(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }

    @Override
    protected List<String> compute() {
        List<String> list = new ArrayList<>();

        List<FolderProcessor> tasks = new ArrayList<>();

        File file = new File(path);
        File content[] = file.listFiles();

        if (content != null) {
            for (File aContent : content) {
                if (aContent.isDirectory()) {
                    FolderProcessor task = new FolderProcessor(aContent.getAbsolutePath(), extension);
                    task.fork();
                    tasks.add(task);
                } else {
                    if (checkFile(aContent.getName())) {
                        list.add(aContent.getAbsolutePath());
                    }
                }
            }
        }
        if (tasks.size() > 50) {
            System.out.printf("%s: %d tasks ran.\n", file.getAbsolutePath(), tasks.size());
        }
        addResultsFromTasks(list, tasks);
        return list;
    }

    private void addResultsFromTasks(List<String> list,
                                     List<FolderProcessor> tasks) {
        for (FolderProcessor item : tasks) {
            list.addAll(item.join());
        }
    }

    private boolean checkFile(String name) {
        return name.endsWith(extension);
    }

}
