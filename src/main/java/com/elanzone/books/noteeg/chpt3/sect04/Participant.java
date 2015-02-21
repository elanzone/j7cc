package com.elanzone.books.noteeg.chpt3.sect04;


import java.util.concurrent.TimeUnit;

public class Participant implements Runnable {

    private VideoConference conference;
    private String name;

    public Participant(VideoConference conference, String name) {
        this.conference = conference;
        this.name = name;
    }

    @Override
    public void run() {
        long duration = (long) (Math.random() * 10);
        System.out.printf("%s: wait me for %d seconds\n", name, duration);

        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        conference.arrive(name);
    }

}
