package com.elanzone.books.noteeg.chpt4.sect11;


import java.util.concurrent.CompletionService;

public class ReportRequest implements Runnable {

    private String name;

    private CompletionService<String> service;

    public ReportRequest(String name, CompletionService<String> service) {
        this.name = name;
        this.service = service;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            ReportGenerator reportGenerator = new ReportGenerator(name, "Report");
            service.submit(reportGenerator);
        }
    }

}
