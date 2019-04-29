/*
 * Copyright (c)  2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.execution.timeseries;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.query.output.callback.QueryCallback;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.util.EventPrinter;
import io.siddhi.core.util.SiddhiTestHelper;
import org.apache.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for lengthTimeOutlier Extension.
 */
public class LengthTimeLinearRegressionOutlierTestcase {
    static final Logger LOGGER = Logger.getLogger(LengthTimeLinearRegressionTestcase.class);
    private static SiddhiManager siddhiManager;
    private AtomicInteger count = new AtomicInteger();
    private int waitTime = 2000;
    private int timeout = 30000;
    private boolean outlier;

    @BeforeMethod
    public void init() {
        count.set(0);
    }

    @Test
    public void simpleOutlierTest1() throws Exception {
        LOGGER.info("Simple Outlier TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        // Limit number of events based on length window (query):
        String siddhiApp = ("@info(name = 'query1') " +
                " from InputStream#timeseries:lengthTimeOutlier(20 min, 20, 1, y, x) "
                + "select * " + "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                outlier = (Boolean) inEvents[inEvents.length - 1].getData(5);
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());

        // Limit number of events based on length window (test case):
        inputHandler.send(new Object[]{3300.00, 31.00});
        inputHandler.send(new Object[]{2600.00, 18.00});
        inputHandler.send(new Object[]{2500.00, 17.00});
        inputHandler.send(new Object[]{2475.00, 12.00});
        inputHandler.send(new Object[]{2313.00, 8.00});
        inputHandler.send(new Object[]{2175.00, 26.00});
        inputHandler.send(new Object[]{600.00, 14.00});
        inputHandler.send(new Object[]{460.00, 3.00});
        inputHandler.send(new Object[]{240.00, 1.00});
        inputHandler.send(new Object[]{200.00, 10.00});
        inputHandler.send(new Object[]{177.00, 0.00});
        inputHandler.send(new Object[]{140.00, 6.00});
        inputHandler.send(new Object[]{117.00, 1.00});
        inputHandler.send(new Object[]{115.00, 0.00});
        inputHandler.send(new Object[]{2600.00, 19.00});
        inputHandler.send(new Object[]{1907.00, 13.00});
        inputHandler.send(new Object[]{1190.00, 3.00});
        inputHandler.send(new Object[]{990.00, 16.00});
        inputHandler.send(new Object[]{925.00, 6.00});
        inputHandler.send(new Object[]{365.00, 0.00});
        inputHandler.send(new Object[]{302.00, 10.00});
        inputHandler.send(new Object[]{300.00, 6.00});
        inputHandler.send(new Object[]{129.00, 2.00});
        inputHandler.send(new Object[]{111.00, 1.00});
        inputHandler.send(new Object[]{6100.00, 18.00});
        inputHandler.send(new Object[]{4125.00, 19.00});
        inputHandler.send(new Object[]{3213.00, 1.00});
        inputHandler.send(new Object[]{2319.00, 38.00});
        inputHandler.send(new Object[]{2000.00, 10.00});
        inputHandler.send(new Object[]{1600.00, 0.00});
        inputHandler.send(new Object[]{1394.00, 4.00});
        inputHandler.send(new Object[]{935.00, 4.00});
        inputHandler.send(new Object[]{850.00, 0.00});
        inputHandler.send(new Object[]{775.00, 5.00});
        inputHandler.send(new Object[]{760.00, 6.00});
        inputHandler.send(new Object[]{629.00, 1.00});
        inputHandler.send(new Object[]{275.00, 6.00});
        inputHandler.send(new Object[]{120.00, 0.00});
        inputHandler.send(new Object[]{2567.00, 12.00});
        inputHandler.send(new Object[]{2500.00, 28.00});
        inputHandler.send(new Object[]{2350.00, 21.00});
        inputHandler.send(new Object[]{2317.00, 3.00});
        inputHandler.send(new Object[]{2000.00, 12.00});
        inputHandler.send(new Object[]{715.00, 1.00});
        inputHandler.send(new Object[]{660.00, 9.00});
        inputHandler.send(new Object[]{650.00, 0.00});
        inputHandler.send(new Object[]{260.00, 0.00});
        inputHandler.send(new Object[]{250.00, 1.00});
        inputHandler.send(new Object[]{200.00, 13.00});
        inputHandler.send(new Object[]{180.00, 6.00});

        SiddhiTestHelper.waitForEvents(waitTime, 50, count, timeout);

        AssertJUnit.assertEquals("No of events: ", 50, count.get());
        AssertJUnit.assertEquals(true, outlier);

        siddhiAppRuntime.shutdown();

    }

    @Test
    public void simpleOutlierTest2() throws Exception {
        LOGGER.info("Simple Outlier TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        // Limit number of events based on time window (query):
        String siddhiApp = ("@info(name = 'query1') " +
                "from InputStream#timeseries:lengthTimeOutlier(200, 10000, 1, y, x) "
                + "select * " + "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                outlier = (Boolean) inEvents[inEvents.length - 1].getData(5);
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());

        // Limit number of events based on time window (test case):
        inputHandler.send(new Object[]{3300.00, 31.00});
        inputHandler.send(new Object[]{2600.00, 18.00});
        inputHandler.send(new Object[]{2500.00, 17.00});
        inputHandler.send(new Object[]{2475.00, 12.00});
        inputHandler.send(new Object[]{2313.00, 8.00});
        inputHandler.send(new Object[]{2175.00, 26.00});
        inputHandler.send(new Object[]{600.00, 14.00});
        inputHandler.send(new Object[]{460.00, 3.00});
        inputHandler.send(new Object[]{240.00, 1.00});
        inputHandler.send(new Object[]{200.00, 10.00});
        inputHandler.send(new Object[]{177.00, 0.00});
        inputHandler.send(new Object[]{140.00, 6.00});
        inputHandler.send(new Object[]{117.00, 1.00});
        inputHandler.send(new Object[]{115.00, 0.00});
        inputHandler.send(new Object[]{2600.00, 19.00});
        inputHandler.send(new Object[]{1907.00, 13.00});
        inputHandler.send(new Object[]{1190.00, 3.00});
        inputHandler.send(new Object[]{990.00, 16.00});
        inputHandler.send(new Object[]{925.00, 6.00});
        inputHandler.send(new Object[]{365.00, 0.00});
        inputHandler.send(new Object[]{302.00, 10.00});
        inputHandler.send(new Object[]{300.00, 6.00});
        inputHandler.send(new Object[]{129.00, 2.00});
        inputHandler.send(new Object[]{111.00, 1.00});
        inputHandler.send(new Object[]{6100.00, 18.00});
        inputHandler.send(new Object[]{4125.00, 19.00});
        inputHandler.send(new Object[]{3213.00, 1.00});
        inputHandler.send(new Object[]{2319.00, 38.00});
        inputHandler.send(new Object[]{2000.00, 10.00});
        inputHandler.send(new Object[]{1600.00, 0.00});
        Thread.sleep(200);
        inputHandler.send(new Object[]{1394.00, 4.00});
        inputHandler.send(new Object[]{935.00, 4.00});
        inputHandler.send(new Object[]{850.00, 0.00});
        inputHandler.send(new Object[]{775.00, 5.00});
        inputHandler.send(new Object[]{760.00, 6.00});
        inputHandler.send(new Object[]{629.00, 1.00});
        inputHandler.send(new Object[]{275.00, 6.00});
        inputHandler.send(new Object[]{120.00, 0.00});
        inputHandler.send(new Object[]{2567.00, 12.00});
        inputHandler.send(new Object[]{2500.00, 28.00});
        inputHandler.send(new Object[]{2350.00, 21.00});
        inputHandler.send(new Object[]{2317.00, 3.00});
        inputHandler.send(new Object[]{2000.00, 12.00});
        inputHandler.send(new Object[]{715.00, 1.00});
        inputHandler.send(new Object[]{660.00, 9.00});
        inputHandler.send(new Object[]{650.00, 0.00});
        inputHandler.send(new Object[]{260.00, 0.00});
        inputHandler.send(new Object[]{250.00, 1.00});
        inputHandler.send(new Object[]{200.00, 13.00});
        inputHandler.send(new Object[]{180.00, 6.00});

        SiddhiTestHelper.waitForEvents(waitTime, 50, count, timeout);


        AssertJUnit.assertEquals("No of events: ", 50, count.get());
        AssertJUnit.assertEquals(true, outlier);

        siddhiAppRuntime.shutdown();
    }


    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest3() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test as attributeExpressionExecutors[0]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";
        // Limit number of events based on time window (query):
        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:lengthTimeOutlier('1stpara', "
                + "10000, 1, y, x) select * " + "insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest4() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test as attributeExpressionExecutors[1]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";
        // Limit number of events based on time window (query):
        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:lengthTimeOutlier(200, "
                + " '2ndpara', 1, y, x) select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest5() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test as attributeExpressionExecutors[3]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";
        // Limit number of events based on time window (query):
        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:lengthTimeOutlier(200, "
                + "10000, 1,1.0, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest6() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test as attributeExpressionExecutors[4]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";
        // Limit number of events based on time window (query):
        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:lengthTimeOutlier(200, "
                + "10000, 1, 1, 1, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest7() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test as attributeExpressionExecutors[4]'s interval range");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";
        // Limit number of events based on time window (query):
        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:lengthTimeOutlier(200, "
                + "10000, 1,1,2.0, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }
}
