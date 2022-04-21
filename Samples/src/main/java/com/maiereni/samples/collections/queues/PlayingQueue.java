/**
 * ================================================================
 * Copyright (c) 2020-2021 Maiereni Software and Consulting Inc
 * ================================================================
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.samples.collections.queues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class is a sample for the use of the Queue interface. Two threads are using a common queue to add and remove objects
 * to it.
 *
 * @author Petre Maierean
 */
public class PlayingQueue {
    private static final Logger logger = LogManager.getLogger(PlayingQueue.class);
    private Queue<Integer> queue = new LinkedBlockingQueue<Integer>();
    private int max = 10;
    private boolean done;

    public PlayingQueue(String[] args) {
        if (args.length > 0) {
            try {
                max = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                logger.error("Could not parse the argument " + args[0], e);
            }
        }
        logger.info("The number of iterations is " + max);
    }

    public synchronized void setDone(boolean done) {
        this.done = done;
    }

    public synchronized boolean isDone() {
        return done;
    }

    /**
     * A simple testing
     * @param args
     */
    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("Sampler");
        PlayingQueue playingQueue = new PlayingQueue(args);

        new Thread(threadGroup, new Adding(playingQueue)).start();
        new Thread(threadGroup, new Removing(playingQueue)).start();

        while (threadGroup.activeCount() > 0) {
            try {
                Thread.sleep(1000);
            }
            catch (Exception e) {
                logger.error("Could wait", e);
            }
        }
        logger.debug("Done");
    }

    public static class Adding implements Runnable {
        private PlayingQueue parent;

        public Adding(PlayingQueue parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            logger.debug("Starting the Adding");
            for(int count = 0; count< parent.max; count++) {
                parent.queue.add(count);
                logger.debug("Adding thread: " + count);
                try {
                    Thread.sleep(2000);
                }
                catch (Exception e) {
                    logger.error("Could wait for the Adding thread", e);
                }
            }
            logger.debug("The Adding thread is done");
            parent.setDone(true);
        }
    }

    public static class Removing implements Runnable {
        private PlayingQueue parent;

        public Removing(PlayingQueue parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            logger.debug("Starting the Removing");
            while (!(parent.isDone() && parent.queue.isEmpty())) {
                if (!parent.queue.isEmpty()) {
                    Integer i = parent.queue.remove();
                    logger.debug("Removing thread: " + i);
                }
                else {
                    logger.debug("Removing thread nothing");
                }
                try {
                    Thread.sleep(500);
                }
                catch (Exception e) {
                    logger.error("Could wait for the Removing thread", e);
                }
            }
            logger.debug("The Removing thread is done");
        }
    }
}
