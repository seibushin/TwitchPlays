/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

public class Semaphore {
    private int count;

    public synchronized void P() throws InterruptedException {
        while (count <= 0) wait();
        System.out.println(count);
        count--;
    }

    public synchronized void V() {
        System.out.println("inc");
        count++;
        notify();
    }

    public Semaphore(int count) {
        this.count = count;
    }

    public static void main(String[] args) {
        Semaphore s = new Semaphore(2);

        new Thread(() -> {
            try {
                s.P();
                s.P();
                s.P();
                s.P();
                s.P();
                s.P();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {
            try {
                s.V();
                Thread.sleep(1000);
                s.V();
                Thread.sleep(1000);
                s.V();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
