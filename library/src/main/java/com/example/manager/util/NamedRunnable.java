/*
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.manager.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class NamedRunnable implements Runnable {

    protected final String name;

    protected Thread mCurrentThread;

    final AtomicBoolean finished = new AtomicBoolean(false);

    public NamedRunnable(String name) {
        this.name = name;
    }

    @Override
    public final void run() {
        mCurrentThread = Thread.currentThread();
        Thread.currentThread().setName(name);
        if (isFinished()) {
            return;
        }
        try {
            execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            interrupted(e);
        }
    }

    public Thread getmCurrentThread() {
        return mCurrentThread;
    }

    public boolean isFinished() {
        return finished.get();
    }

    public boolean setFinished(boolean finish) {
        return finished.getAndSet(finish);
    }

    public void cancel() {
        if (getmCurrentThread() != null) {
            getmCurrentThread().interrupt();
        } else {
            setFinished(true);
        }
    }

    protected abstract void execute() throws InterruptedException;

    protected abstract void interrupted(InterruptedException e);

}
