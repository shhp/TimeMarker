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

package com.shhp.timemarker.log;

import android.util.Log;

import com.shhp.timemarker.TimeMarker;

/**
 *
 */
public class AndroidLogUtil implements LogUtil {
    @Override
    public void log(String message) {
        Log.i(TimeMarker.TAG, message);
    }
}
