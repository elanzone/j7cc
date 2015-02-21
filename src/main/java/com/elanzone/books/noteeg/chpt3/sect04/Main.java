package com.elanzone.books.noteeg.chpt3.sect04;


public class Main {

    public static void main(String[] args) {
        // 创建一个 VideoConference 对象并等待 10 个与会者
        VideoConference conference = new VideoConference(10);

        Thread threadConference = new Thread(conference);
        threadConference.start();

        for (int i = 0; i < 10; i++) {
            Participant p = new Participant(conference, "Participant" + i);
            Thread t = new Thread(p);
            t.start();
        }
    }

}
