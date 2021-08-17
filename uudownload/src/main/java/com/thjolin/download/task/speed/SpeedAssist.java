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

package com.thjolin.download.task.speed;

import android.os.SystemClock;

import java.util.Locale;

public class SpeedAssist {

    long timestamp;
    long increaseBytes;

    long bytesPerSecond;

    long beginTimestamp;
    long endTimestamp;
    long allIncreaseBytes;

    public synchronized void reset() {
        timestamp = 0;
        increaseBytes = 0;
        bytesPerSecond = 0;

        beginTimestamp = 0;
        endTimestamp = 0;
        allIncreaseBytes = 0;
    }

    long nowMillis() {
        return SystemClock.uptimeMillis();
    }

    public synchronized void uu_downloading(long increaseBytes) {
        if (timestamp == 0) {
            this.timestamp = nowMillis();
            this.beginTimestamp = timestamp;
        }
        this.increaseBytes += increaseBytes;
        this.allIncreaseBytes += increaseBytes;
    }

    public synchronized void flush() {
        final long nowMillis = nowMillis();
        final long sinceNowIncreaseBytes = increaseBytes;
        final long durationMillis = Math.max(1, nowMillis - timestamp);

        increaseBytes = 0;
        timestamp = nowMillis;
        bytesPerSecond = (long) ((float) sinceNowIncreaseBytes / durationMillis * 1000f);
    }

    /**
     * Get instant bytes per-second.
     */
    public long getInstantBytesPerSecondAndFlush() {
        flush();
        return bytesPerSecond;
    }

    public synchronized long getBytesPerSecondAndFlush() {
        final long interval = nowMillis() - timestamp;
        if (interval < 1000 && bytesPerSecond != 0) return bytesPerSecond;
        if (bytesPerSecond == 0 && interval < 500) return 0;

        return getInstantBytesPerSecondAndFlush();
    }

    public synchronized long getBytesPerSecondFromBegin() {
        final long endTimestamp = this.endTimestamp == 0 ? nowMillis() : this.endTimestamp;
        final long sinceNowIncreaseBytes = allIncreaseBytes;
        final long durationMillis = Math.max(1, endTimestamp - beginTimestamp);

        // precision loss
        return (long) ((float) sinceNowIncreaseBytes / durationMillis * 1000f);
    }

    public synchronized void endTask() {
        endTimestamp = nowMillis();
    }

    public String instantSpeed() {
        return getSpeedWithSIAndFlush();
    }


    public String speed() {
        return humanReadableSpeed(getBytesPerSecondAndFlush(), true);
    }

    public String lastSpeed() {
        return humanReadableSpeed(bytesPerSecond, true);
    }

    public synchronized long getInstantSpeedDurationMillis() {
        return nowMillis() - timestamp;
    }

    public String getSpeedWithBinaryAndFlush() {
        return humanReadableSpeed(getInstantBytesPerSecondAndFlush(), false);
    }

    /**
     * With wikipedia: https://en.wikipedia.org/wiki/Kilobyte
     * <p>
     * 1KB = 1000B
     * 1MB = 1000KB
     */
    public String getSpeedWithSIAndFlush() {
        return humanReadableSpeed(getInstantBytesPerSecondAndFlush(), true);
    }

    public String averageSpeed() {
        return speedFromBegin();
    }

    public String speedFromBegin() {
        return humanReadableSpeed(getBytesPerSecondFromBegin(), true);
    }

    private static String humanReadableSpeed(long bytes, boolean si) {
        return humanReadableBytes(bytes, si) + "/s";
    }

    private static String humanReadableBytes(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
