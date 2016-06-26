/*
 * Copyright 2016 shhp
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */


package com.shhp.timemarker;

import com.shhp.timemarker.log.LogUtil;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * TimeMarker can be used to inspect execution time distribution in a java or Android project.
 * Sometimes if we want to see how much time it costs to execute a block of codes, we may try this:
 * <br/>
 * <pre>
 * {@code
 * long start = System.currentTimeMillis();
 *  // do something
 *  long cost = System.currentTimeMillis() - start;
 * }
 * </pre>
 * With TimeMarker, it is simpler:
 * <br/>
 * <pre>
 * {@code
 * TimeMarker.mark("start");
 *  // do something
 *  TimeMarker.mark("end");
 * }
 * </pre>
 *
 * You can call {@link TimeMarker#mark(String)} wherever in the project.
 * When you want to see the time distribution, just call {@link TimeMarker#report(LogUtil)}.
 */
public class TimeMarker {

    public static final String TAG = "TimeMarker";

    private static LinkedHashMap<String, Long> sTimestampMap = new LinkedHashMap<String, Long>();

    /**
     * Make a mark.
     * @param key A unique key to identify the mark.
     */
    public synchronized static void mark(String key) {
        sTimestampMap.put(key, System.currentTimeMillis());
    }

    /**
     * Log the time distribution among marks in descending order.
     * @param logUtil
     */
    public synchronized static void report(LogUtil logUtil) {
        if (sTimestampMap.size() <= 1) {
            logUtil.log("Nothing to report!");
        } else {
            Iterator<String> iterator = sTimestampMap.keySet().iterator();
            Map<Long, String> orderedTimeCost = new TreeMap<Long, String>(new Comparator<Long>() {
                @Override
                public int compare(Long aLong, Long t1) {
                    long v1 = aLong;
                    long v2 = t1;
                    return v1 < v2 ? 1
                            : v1 == v2 ? 0 : -1;
                }
            });
            String from = iterator.next();
            String to = "";
            long totalTime = 0;
            do {
                to = iterator.next();
                String key = from + " ---> " + to;
                long cost = sTimestampMap.get(to) - sTimestampMap.get(from);
                orderedTimeCost.put(cost, key);
                totalTime += cost;
                from = to;
            } while (iterator.hasNext());

            printResult(orderedTimeCost, totalTime, logUtil);
        }
        sTimestampMap.clear();
    }

    private static void printResult(Map<Long, String> map, long totalTime, LogUtil logUtil) {
        logUtil.log("=================================================");

        for (Map.Entry<Long, String> entry : map.entrySet()) {
            long timeCost = entry.getKey();
            logUtil.log(String.format(" %s  cost: %d  percentage: %.2f%%", entry.getValue(), timeCost, (timeCost * 100f / totalTime)));
        }

        logUtil.log("=================================================");
    }
}
