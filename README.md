# TimeMarker
TimeMarker can be used to inspect execution time distribution in a java or Android project.

Sometimes if we want to see how much time it costs to execute a block of codes, we may try this:

```
long start = System.currentTimeMillis();
// do something
long cost = System.currentTimeMillis() - start;
```

With TimeMarker, it is simpler:

```
TimeMarker.mark("start");
// do something
TimeMarker.mark("end");
```

You can group marks by calling `TimeMarker.beginGroup()` and `TimeMarker.endGroup()`.
Time distribution will be calculated separately among groups.

You can call `TimeMarker.mark()` wherever in the project.
When you want to see the time distribution, just call `TimeMarker.report()` to log the time distribution among marks in descending order or call `TimeMarker.reportSequentially()` to log time distribution sequentially.

# Example

This example shows how to use `TimeMarker` in an Android project.

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TimeMarker.mark("onCreate_start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimeMarker.mark("onCreate_setContentView");
        doSomethingOnCreate();
        TimeMarker.mark("onCreate_finish");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeMarker.report(new AndroidLogUtil());
    }

    @Override
    protected void onResume() {
        TimeMarker.mark("onResume_start");
        super.onResume();

        doSomethingOnResume();

        TimeMarker.mark("onResume_finish");
    }

    private void doSomethingOnCreate() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doSomethingOnResume() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

The log is like:

```
06-26 14:45:44.343 7648-7648/com.shhp.timemarker I/TimeMarker: =================================================
06-26 14:45:44.345 7648-7648/com.shhp.timemarker I/TimeMarker:  onResume_start ---> onResume_finish  cost: 1000  percentage: 52.41%
06-26 14:45:44.345 7648-7648/com.shhp.timemarker I/TimeMarker:  onCreate_setContentView ---> onCreate_finish  cost: 500  percentage: 26.21%
06-26 14:45:44.345 7648-7648/com.shhp.timemarker I/TimeMarker:  onCreate_start ---> onCreate_setContentView  cost: 406  percentage: 21.28%
06-26 14:45:44.348 7648-7648/com.shhp.timemarker I/TimeMarker:  onCreate_finish ---> onResume_start  cost: 2  percentage: 0.10%
06-26 14:45:44.348 7648-7648/com.shhp.timemarker I/TimeMarker: =================================================
```

# License

    Copyright 2016 shhp

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
