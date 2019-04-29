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
 * Tests for regress Extension.
 */
public class LinearRegressionTestcase {
    static final Logger LOGGER = Logger.getLogger(LinearRegressionTestcase.class);
    protected static SiddhiManager siddhiManager;
    private AtomicInteger count = new AtomicInteger();
    private int waitTime = 2000;
    private int timeout = 30000;
    private double betaZero, betaTwo, forecastY;
    private boolean outlier;

    @BeforeMethod
    public void init() {
        count.set(0);
    }

    @Test
    public void simpleRegressionTest() throws Exception {
        LOGGER.info("Simple Regression TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y int, x int);";

        String siddhiApp = ("@info(name = 'query1') from InputStream#timeseries:regress(1, 100, 0.95, y, x) "
                + "select * "
                + "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                betaZero = (Double) inEvents[inEvents.length - 1].getData(3);
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());

        inputHandler.send(new Object[]{2500.00, 17.00});
        inputHandler.send(new Object[]{2600.00, 18.00});
        inputHandler.send(new Object[]{3300.00, 31.00});
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
        AssertJUnit.assertEquals("Beta0: ", 573.1418421169493, betaZero, 573.1418421169493 - betaZero);

        siddhiAppRuntime.shutdown();

    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleRegressionTest1() throws Exception {
        LOGGER.info("Simple Regression TestCase with test attributeExpressionExecutors[0]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y int, x int);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:regress('1stpara', "
                + "100, 0.95, y, x) select * "
                + "insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleRegressionTest2() throws Exception {
        LOGGER.info("Simple Regression TestCase with test attributeExpressionExecutors[1]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y int, x int);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:regress(1, '2ndpara', 0.95, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleRegressionTest3() throws Exception {
        LOGGER.info("Simple Regression TestCase with test attributeExpressionExecutors[2]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y int, x int);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:regress(1, 100, 1, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleRegressionTest4() throws Exception {
        LOGGER.info("Simple Regression TestCase with test attributeExpressionExecutors[2]'s interval range");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y int, x int);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:regress('1stpara', 100, 2.0, y, x) "
                + "select * "
                + "insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test
    public void multipleRegressionTest() throws Exception {
        LOGGER.info("Multiple Regression TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (a int, b int, c int, d int, e int);";

        String eventFuseSiddhiApp = ("@info(name = 'query2') from InputStream#timeseries:regress(a, c, b, e) "
                + "select * "
                + "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + eventFuseSiddhiApp);

        siddhiAppRuntime.addCallback("query2", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                betaTwo = (Double) inEvents[inEvents.length - 1].getData(8);
            }
        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        //system.out.println(System.currentTimeMillis());

        inputHandler.send(new Object[]{3300, 104, 22, 80, 3});
        inputHandler.send(new Object[]{2600, 66, 39, 69, 3});
        inputHandler.send(new Object[]{2500, 73, 63, 116, 5});
        inputHandler.send(new Object[]{2475, 50, 23, 64, 21});
        inputHandler.send(new Object[]{2313, 58, 70, 53, 8});
        inputHandler.send(new Object[]{2175, 100, 87, 89, 4});
        inputHandler.send(new Object[]{600, 38, 15, 45, 10});
        inputHandler.send(new Object[]{460, 21, 11, 32, 3});
        inputHandler.send(new Object[]{240, 18, 24, 26, 2});
        inputHandler.send(new Object[]{200, 33, 14, 96, 6});
        inputHandler.send(new Object[]{177, 10, 5, 18, 7});
        inputHandler.send(new Object[]{140, 22, 19, 56, 3});
        inputHandler.send(new Object[]{117, 3, 2, 1, 0});
        inputHandler.send(new Object[]{115, 2, 4, 3, 0});
        inputHandler.send(new Object[]{2600, 75, 53, 64, 7});
        inputHandler.send(new Object[]{1907, 73, 50, 100, 14});
        inputHandler.send(new Object[]{1190, 26, 42, 61, 8});
        inputHandler.send(new Object[]{990, 64, 42, 102, 6});
        inputHandler.send(new Object[]{925, 26, 22, 26, 5});
        inputHandler.send(new Object[]{365, 15, 14, 30, 6});
        inputHandler.send(new Object[]{302, 51, 95, 151, 27});
        inputHandler.send(new Object[]{300, 39, 34, 89, 6});
        inputHandler.send(new Object[]{129, 18, 20, 22, 5});
        inputHandler.send(new Object[]{111, 8, 1, 18, 0});
        inputHandler.send(new Object[]{6100, 100, 90, 67, 15});
        inputHandler.send(new Object[]{4125, 96, 55, 74, 7});
        inputHandler.send(new Object[]{3213, 17, 39, 47, 3});
        inputHandler.send(new Object[]{2319, 117, 78, 120, 31});
        inputHandler.send(new Object[]{2000, 40, 36, 56, 4});
        inputHandler.send(new Object[]{1600, 31, 50, 69, 15});
        inputHandler.send(new Object[]{1394, 51, 83, 50, 5});
        inputHandler.send(new Object[]{935, 21, 30, 42, 3});
        inputHandler.send(new Object[]{850, 54, 75, 38, 20});
        inputHandler.send(new Object[]{775, 35, 9, 19, 3});
        inputHandler.send(new Object[]{760, 36, 40, 53, 14});
        inputHandler.send(new Object[]{629, 30, 24, 43, 0});
        inputHandler.send(new Object[]{275, 34, 33, 57, 8});
        inputHandler.send(new Object[]{120, 5, 14, 19, 2});
        inputHandler.send(new Object[]{2567, 42, 41, 66, 8});
        inputHandler.send(new Object[]{2500, 81, 48, 93, 5});
        inputHandler.send(new Object[]{2350, 92, 67, 100, 3});
        inputHandler.send(new Object[]{2317, 12, 37, 20, 4});
        inputHandler.send(new Object[]{2000, 40, 12, 57, 9});
        inputHandler.send(new Object[]{715, 11, 16, 36, 3});
        inputHandler.send(new Object[]{660, 49, 14, 49, 9});
        inputHandler.send(new Object[]{650, 15, 30, 30, 4});
        inputHandler.send(new Object[]{260, 12, 13, 14, 0});
        inputHandler.send(new Object[]{250, 11, 2, 26, 2});
        inputHandler.send(new Object[]{200, 50, 31, 73, 3});
        inputHandler.send(new Object[]{180, 21, 17, 26, 8});

        SiddhiTestHelper.waitForEvents(waitTime, 50, count, timeout);

        AssertJUnit.assertEquals("No of events: ", 50, count.get());
        AssertJUnit.assertEquals("Beta2: ", 26.665526771748596, betaTwo, 26.665526771748596 - betaTwo);

        siddhiAppRuntime.shutdown();
    }

    @Test
    public void simpleForecastTest() throws Exception {
        LOGGER.info("Simple Forecast TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, symbol string, x double);";

        String siddhiApp = ("@info(name = 'query1') from InputStream#timeseries:forecast(2, 1000, 0.95, x+2, y, x) "
                + "select * "
                + "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                forecastY = (Double) inEvents[inEvents.length - 1].getData(6);
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());

        inputHandler.send(new Object[]{3300, "IBM", 31});
        inputHandler.send(new Object[]{2600, "GOOG", 18});
        inputHandler.send(new Object[]{2500, "GOOG", 17});
        inputHandler.send(new Object[]{2475, "APPL", 12});
        inputHandler.send(new Object[]{2313, "MSFT", 8});
        inputHandler.send(new Object[]{2175, "IBM", 26});
        inputHandler.send(new Object[]{600, "APPL", 14});
        inputHandler.send(new Object[]{460, "APPL", 3});
        inputHandler.send(new Object[]{240, "IBM", 1});
        inputHandler.send(new Object[]{200, "GOOG", 10});
        inputHandler.send(new Object[]{177, "GOOG", 0});
        inputHandler.send(new Object[]{140, "APPL", 6});
        inputHandler.send(new Object[]{117, "MSFT", 1});
        inputHandler.send(new Object[]{115, "IBM", 0});
        inputHandler.send(new Object[]{2600, "APPL", 19});
        inputHandler.send(new Object[]{1907, "APPL", 13});
        inputHandler.send(new Object[]{1190, "IBM", 3});
        inputHandler.send(new Object[]{990, "GOOG", 16});
        inputHandler.send(new Object[]{925, "GOOG", 6});
        inputHandler.send(new Object[]{365, "APPL", 0});
        inputHandler.send(new Object[]{302, "MSFT", 10});
        inputHandler.send(new Object[]{300, "IBM", 6});
        inputHandler.send(new Object[]{129, "APPL", 2});
        inputHandler.send(new Object[]{111, "APPL", 1});
        inputHandler.send(new Object[]{6100, "IBM", 18});
        inputHandler.send(new Object[]{4125, "GOOG", 19});
        inputHandler.send(new Object[]{3213, "GOOG", 1});
        inputHandler.send(new Object[]{2319, "APPL", 38});
        inputHandler.send(new Object[]{2000, "MSFT", 10});
        inputHandler.send(new Object[]{1600, "IBM", 0});
        inputHandler.send(new Object[]{1394, "APPL", 4});
        inputHandler.send(new Object[]{935, "APPL", 4});
        inputHandler.send(new Object[]{850, "IBM", 0});
        inputHandler.send(new Object[]{775, "GOOG", 5});
        inputHandler.send(new Object[]{760, "GOOG", 6});
        inputHandler.send(new Object[]{629, "APPL", 1});
        inputHandler.send(new Object[]{275, "MSFT", 6});
        inputHandler.send(new Object[]{120, "IBM", 0});
        inputHandler.send(new Object[]{2567, "APPL", 12});
        inputHandler.send(new Object[]{2500, "APPL", 28});
        inputHandler.send(new Object[]{2350, "IBM", 21});
        inputHandler.send(new Object[]{2317, "GOOG", 3});
        inputHandler.send(new Object[]{2000, "GOOG", 12});
        inputHandler.send(new Object[]{715, "APPL", 1});
        inputHandler.send(new Object[]{660, "MSFT", 9});
        inputHandler.send(new Object[]{650, "IBM", 0});
        inputHandler.send(new Object[]{260, "APPL", 0});
        inputHandler.send(new Object[]{250, "APPL", 1});
        inputHandler.send(new Object[]{200, "IBM", 13});
        inputHandler.send(new Object[]{180, "GOOG", 6});

        SiddhiTestHelper.waitForEvents(waitTime, 25, count, timeout);

        AssertJUnit.assertEquals("Beta0: ", 1250.1106928045238, forecastY, 1250.1106928045238 - forecastY);
        AssertJUnit.assertEquals("No of events: ", 25, count.get());

        siddhiAppRuntime.shutdown();

    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleForecastTest1() throws Exception {
        LOGGER.info("Simple Forecast TestCase with test attributeExpressionExecutors[0]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, symbol string, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:forecast('1stpara', 1000, 0.95, "
                + "x+2, y, x) select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleForecastTest2() throws Exception {
        LOGGER.info("Simple Forecast TestCase with test attributeExpressionExecutors[1]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, symbol string, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:forecast(2, '2ndpara', 0.95, "
                + "x+2, y, x) select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleForecastTest3() throws Exception {
        LOGGER.info("Simple Forecast TestCase with test attributeExpressionExecutors[2]'s type");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, symbol string, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:forecast(2, 1000, 1, "
                + "x+2, y, x) select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleForecastTest4() throws Exception {
        LOGGER.info("Simple Forecast TestCase with test attributeExpressionExecutors[2]'s interval range");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, symbol string, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:forecast(2, 1000, 2.0, "
                + "x+2, y, x) select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test
    public void simpleOutlierTest() throws Exception {
        LOGGER.info("Simple Outlier TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        String siddhiApp = ("@info(name = 'query1') from InputStream#timeseries:outlier(1, y, x) "
                + "select * "
                + "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                outlier = (Boolean) inEvents[inEvents.length - 1].getData(5);
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());

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
        AssertJUnit.assertEquals(false, outlier);

        siddhiAppRuntime.shutdown();

    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest1() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test Calculation interval, batch size"
                + " and range should be of type int");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:outlier(1,'1stpara', y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest2() throws Exception {
        LOGGER.info("Simple Outlier TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:outlier(2,'2ndpara', y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest3() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test Confidence interval should be of type double");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:outlier(1,2,1, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void simpleOutlierTest4() throws Exception {
        LOGGER.info("Simple Outlier TestCase with test Confidence interval should be a value between 0 and 1 ");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        String executionPlan = ("@info(name = 'query1') from InputStream#timeseries:outlier(1,2,2.0, y, x) "
                + "select * insert into OutputStream;");

        siddhiManager.createSiddhiAppRuntime(inputStream + executionPlan);
    }

    @Test
    public void discreteSeasonalityTest() throws Exception {
        LOGGER.info("Discrete Seasonality TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, t long);";

        String siddhiApp = ("from InputStream "
                + " select y, t,  time:extract(t*1000,'MONTH') as eventMonth "
                + " insert into tempStream1;"
                + ""
                + " from tempStream1[eventMonth == 12] "
                + " select y, t, 1 as dum1"
                + " insert into tempStream2;"
                + ""
                + " from tempStream1[eventMonth != 12] "
                + " select y, t, 0 as dum1"
                + " insert into tempStream2;"
                + ""
                + " @info(name = 'query1') from tempStream2#timeseries:regress( 1, 1000, 0.95, y, dum1)"
                + " select *  "
                + " insert into RegressionResult;"
        );

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                betaTwo = (Double) inEvents[inEvents.length - 1].getData(5);
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());


        inputHandler.send(new Object[]{3439, 694224000});
        inputHandler.send(new Object[]{3264, 696902400});
        inputHandler.send(new Object[]{3437, 699408000});
        inputHandler.send(new Object[]{3523, 702086400});
        inputHandler.send(new Object[]{3545, 704678400});
        inputHandler.send(new Object[]{3611, 707356800});
        inputHandler.send(new Object[]{3637, 709948800});
        inputHandler.send(new Object[]{3986, 712627200});
        inputHandler.send(new Object[]{3797, 715305600});
        inputHandler.send(new Object[]{3758, 717897600});
        inputHandler.send(new Object[]{4428, 720576000});
        inputHandler.send(new Object[]{8566, 723168000});
        inputHandler.send(new Object[]{3801, 725846400});
        inputHandler.send(new Object[]{3204, 728524800});
        inputHandler.send(new Object[]{3686, 730944000});
        inputHandler.send(new Object[]{3827, 733622400});
        inputHandler.send(new Object[]{3770, 736214400});
        inputHandler.send(new Object[]{3923, 738892800});
        inputHandler.send(new Object[]{3839, 741484800});
        inputHandler.send(new Object[]{4270, 744163200});
        inputHandler.send(new Object[]{3988, 746841600});
        inputHandler.send(new Object[]{3920, 749433600});
        inputHandler.send(new Object[]{4853, 752112000});
        inputHandler.send(new Object[]{9010, 754704000});

        SiddhiTestHelper.waitForEvents(waitTime, 24, count, timeout);

        AssertJUnit.assertEquals("No of events: ", 24, count.get());
        AssertJUnit.assertEquals("Beta0: ", 3795.7272727272725, betaZero, 3795.7272727272725 - betaZero);

        siddhiAppRuntime.shutdown();

    }

    @Test
    public void continuousSeasonalityTest() throws Exception {
        LOGGER.info("Continuous Seasonality TestCase");

        siddhiManager = new SiddhiManager();
        String inputStream = "define stream InputStream (y double, x double);";

        String siddhiApp = ("@info(name = 'query1') from InputStream "
                + " select y, x, math:sin(x) as sinx"
                + " insert into tempStream;"
                + ""
                + " @info(name = 'query2') from tempStream#timeseries:regress( y, x, sinx)"
                + " select *  "
                + " insert into RegressionResult;"
        );

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + siddhiApp);

        siddhiAppRuntime.addCallback("query2", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count.addAndGet(inEvents.length);
                betaTwo = (Double) inEvents[inEvents.length - 1].getData(6);
            }
        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();

        //system.out.println(System.currentTimeMillis());

        inputHandler.send(new Object[]{5.49, 0.00});
        inputHandler.send(new Object[]{6.79, 1.00});
        inputHandler.send(new Object[]{6.46, 2.00});
        inputHandler.send(new Object[]{6.24, 3.00});
        inputHandler.send(new Object[]{5.05, 4.00});
        inputHandler.send(new Object[]{4.92, 5.00});
        inputHandler.send(new Object[]{5.64, 6.00});
        inputHandler.send(new Object[]{7.33, 7.00});
        inputHandler.send(new Object[]{7.55, 8.00});
        inputHandler.send(new Object[]{6.87, 9.00});
        inputHandler.send(new Object[]{6.20, 10.00});
        inputHandler.send(new Object[]{5.79, 11.00});
        inputHandler.send(new Object[]{6.56, 12.00});
        inputHandler.send(new Object[]{6.71, 13.00});
        inputHandler.send(new Object[]{7.41, 14.00});
        inputHandler.send(new Object[]{7.97, 15.00});
        inputHandler.send(new Object[]{6.51, 16.00});
        inputHandler.send(new Object[]{5.95, 17.00});
        inputHandler.send(new Object[]{6.40, 18.00});
        inputHandler.send(new Object[]{7.88, 19.00});
        inputHandler.send(new Object[]{7.92, 20.00});

        SiddhiTestHelper.waitForEvents(waitTime, 21, count, timeout);

        AssertJUnit.assertEquals("No of events: ", 21, count.get());
        AssertJUnit.assertEquals("Beta0: ", 0.996755594843574, betaTwo, 0.996755594843574 - betaTwo);

        siddhiAppRuntime.shutdown();
    }
}
