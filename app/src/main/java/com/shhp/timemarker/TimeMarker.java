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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
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
 * You can group marks by calling {@link TimeMarker#beginGroup()} and {@link TimeMarker#endGroup()}.
 * Time distribution will be calculated separately among groups.
 * <br/>
 *
 * You can call {@link TimeMarker#mark(String)} wherever in the project.
 * When you want to see the time distribution, just call {@link TimeMarker#report(LogUtil)} or {@link TimeMarker#reportSequentially(LogUtil)}.
 */
public class TimeMarker {

    public static final String TAG = "TimeMarker";

    private static LinkedHashMap<String, Long> sCurrentTimestampMap;
    private static List<LinkedHashMap<String, Long>> sTimestampMapList = new ArrayList<>();
    private static Map<String, Integer> sCurrentKeyCount;
    private static ArrayDeque<LinkedHashMap<String, Long>> sTimestampMapStack = new ArrayDeque<>();
    private static ArrayDeque<Map<String, Integer>> sKeyCountStack = new ArrayDeque<>();

    /**
     * Make a mark.
     * @param key A unique key to identify the mark.
     */
    public synchronized static void mark(String key) {
        if (sCurrentTimestampMap == null) {
            sCurrentTimestampMap = new LinkedHashMap<>();
        }
        if (sCurrentKeyCount == null) {
            sCurrentKeyCount = new HashMap<>();
        }
        long time = System.currentTimeMillis();
        if (sCurrentKeyCount.containsKey(key)) {
            int previousCount = sCurrentKeyCount.get(key);
            previousCount++;
            sCurrentKeyCount.put(key, previousCount);
            key += "("+previousCount+")";
        } else {
            sCurrentKeyCount.put(key, 0);
        }
        sCurrentTimestampMap.put(key, time);
    }

    /**
     * Begin a group.
     */
    public synchronized static void beginGroup() {
        if (sCurrentKeyCount != null) {
            sKeyCountStack.push(sCurrentKeyCount);
        }
        if (sCurrentTimestampMap != null) {
            sTimestampMapStack.push(sCurrentTimestampMap);
        }
        sCurrentTimestampMap = new LinkedHashMap<>();
        sCurrentKeyCount = new HashMap<>();
    }

    /**
     * End a group.
     */
    public synchronized static void endGroup() {
        sTimestampMapList.add(sCurrentTimestampMap);
        sCurrentTimestampMap = sTimestampMapStack.isEmpty() ? null : sTimestampMapStack.pop();
        sCurrentKeyCount = sKeyCountStack.isEmpty() ? null : sKeyCountStack.pop();
    }

    /**
     * Log the time distribution among marks by group in descending order.
     * @param logUtil
     */
    public synchronized static void report(LogUtil logUtil) {
        if (sCurrentTimestampMap != null) {
            sTimestampMapList.add(sCurrentTimestampMap);
        }
        if (sTimestampMapList.size() < 1) {
            logUtil.log("Nothing to report!");
        } else {
            int groupCount = 1;
            for (LinkedHashMap<String, Long> timestampMap : sTimestampMapList) {
                if (timestampMap.size() > 1) {
                    logUtil.log(String.format(Locale.US, "/************** group %d ****************/", groupCount++));
                    Iterator<String> iterator = timestampMap.keySet().iterator();
                    Map<Double, String> orderedTimeCost = new TreeMap<Double, String>(new Comparator<Double>() {
                        @Override
                        public int compare(Double aDouble, Double t1) {
                            double v1 = aDouble;
                            double v2 = t1;
                            return v1 < v2 ? 1
                                    : v1 == v2 ? 0 : -1;
                        }
                    });
                    String from = iterator.next();
                    String to = "";
                    long totalTime = 0;
                    Random random = new Random();
                    do {
                        to = iterator.next();
                        String key = from + " ---> " + to;
                        long cost = timestampMap.get(to) - timestampMap.get(from);
                        orderedTimeCost.put(cost+random.nextDouble(), key);
                        totalTime += cost;
                        from = to;
                    } while (iterator.hasNext());
                    printResult(orderedTimeCost, (int)totalTime, logUtil);
                }
            }
        }
        sTimestampMapList.clear();
        sTimestampMapStack.clear();
        sKeyCountStack.clear();
        sCurrentKeyCount = null;
        sCurrentTimestampMap = null;
    }

    /**
     * Log the time distribution among marks by group sequentially.
     * @param logUtil
     */
    public synchronized static void reportSequentially(LogUtil logUtil) {
        if (sCurrentTimestampMap != null) {
            sTimestampMapList.add(sCurrentTimestampMap);
        }
        if (sTimestampMapList.size() < 1) {
            logUtil.log("Nothing to report!");
        } else {
            int groupCount = 1;
            for (LinkedHashMap<String, Long> timestampMap : sTimestampMapList) {
                if (timestampMap.size() > 1) {
                    logUtil.log(String.format(Locale.US, "/************** group %d ****************/", groupCount++));
                    Iterator<String> iterator = timestampMap.keySet().iterator();
                    String from = iterator.next();
                    String to = "";
                    long totalTime = 0;
                    Random random = new Random();
                    Map<Double, String> orderedTimeCost = new LinkedHashMap<>();
                    do {
                        to = iterator.next();
                        String key = from + " ---> " + to;
                        long cost = timestampMap.get(to) - timestampMap.get(from);
                        orderedTimeCost.put(cost+random.nextDouble(), key);
                        totalTime += cost;
                        from = to;
                    } while (iterator.hasNext());

                    printResult(orderedTimeCost, (int)totalTime, logUtil);
                }
            }
        }
        sTimestampMapList.clear();
        sTimestampMapStack.clear();
        sKeyCountStack.clear();
        sCurrentKeyCount = null;
        sCurrentTimestampMap = null;
    }

    private static void printResult(Map<Double, String> map, int totalTime, LogUtil logUtil) {
        logUtil.log("=================================================");

        for (Map.Entry<Double, String> entry : map.entrySet()) {
            double timeCost = entry.getKey();
            logUtil.log(String.format("║ %s  cost: %d  percentage: %.2f%%", entry.getValue(), (int)timeCost, ((int)timeCost * 100f / totalTime)));
        }

        logUtil.log("=================================================");
    }
}
