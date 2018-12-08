package com.goldenideabox.patents.common;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {

    @Async
    public void executeCpquery() {
        try {
            for (int i = 0;i < 100;i++) {
                int num = 1000 * (int)(1+Math.random()*10);
                System.out.println(num);
                Thread.sleep(num );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
