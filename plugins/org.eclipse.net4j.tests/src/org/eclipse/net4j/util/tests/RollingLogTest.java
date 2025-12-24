/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.om.log.RollingLog;

import java.io.File;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class RollingLogTest extends AbstractOMTest
{
  private static final String MESSAGE = "Hello World";

  public void testAppend() throws Exception
  {
    TestListener2 propertiesListener = new TestListener2(RollingLog.PropertiesEvent.class);
    TestListener2 splitListener = new TestListener2(RollingLog.SplitEvent.class);

    RollingLog log = new RollingLog(TMPUtil.getTempName("rollinglog").getAbsolutePath(), 1000, false);
    log.setWriteBulk(false);
    log.setWriteInterval(0);
    log.addListener(propertiesListener);
    log.addListener(splitListener);
    log.activate();

    write(log, 3000);
    log.commit();

    assertEquals(9, log.getFileNumber());
    assertEquals(9, splitListener.getEvents().size());

    log.deactivate();
    assertEquals(1, propertiesListener.getEvents().size());

    propertiesListener = new TestListener2(RollingLog.PropertiesEvent.class);
    splitListener = new TestListener2(RollingLog.SplitEvent.class);

    log = new RollingLog(log.getLogFile(), 1000, true);
    log.setWriteBulk(false);
    log.setWriteInterval(0);
    log.addListener(propertiesListener);
    log.addListener(splitListener);
    log.activate();

    write(log, 3000);
    log.commit();

    assertEquals(19, log.getFileNumber());
    assertEquals(10, splitListener.getEvents().size());

    log.deactivate();
    assertEquals(2, propertiesListener.getEvents().size());
  }

  public void testOverwrite() throws Exception
  {
    TestListener2 propertiesListener = new TestListener2(RollingLog.PropertiesEvent.class);
    TestListener2 splitListener = new TestListener2(RollingLog.SplitEvent.class);

    RollingLog log = new RollingLog(TMPUtil.getTempName("rollinglog").getAbsolutePath(), 1000, false);
    log.setWriteBulk(false);
    log.setWriteInterval(0);
    log.addListener(propertiesListener);
    log.addListener(splitListener);
    log.activate();

    write(log, 3000);
    log.commit();

    assertEquals(9, log.getFileNumber());
    assertEquals(9, splitListener.getEvents().size());

    log.deactivate();
    assertEquals(1, propertiesListener.getEvents().size());

    propertiesListener = new TestListener2(RollingLog.PropertiesEvent.class);
    splitListener = new TestListener2(RollingLog.SplitEvent.class);

    log = new RollingLog(log.getLogFile(), 1000, false);
    log.setWriteBulk(false);
    log.setWriteInterval(0);
    log.addListener(propertiesListener);
    log.addListener(splitListener);
    log.activate();

    write(log, 3000);
    log.commit();

    assertEquals(9, log.getFileNumber());
    assertEquals(9, splitListener.getEvents().size());

    log.deactivate();
    assertEquals(1, propertiesListener.getEvents().size());
  }

  public void testRecover() throws Exception
  {
    RollingLog log = new RollingLog(TMPUtil.getTempName("rollinglog").getAbsolutePath(), 1000, false);
    log.setWriteBulk(false);
    log.setWriteInterval(0);
    log.activate();

    write(log, 3000);
    log.commit();
    log.deactivate();

    assertTrue(new File(log.getLogFile() + ".properties").delete());
    TestListener2 recoveryListener = new TestListener2(RollingLog.RecoveryEvent.class);
    TestListener2 propertiesListener = new TestListener2(RollingLog.PropertiesEvent.class);

    log = new RollingLog(log.getLogFile(), 1000, true);
    log.setWriteBulk(false);
    log.setWriteInterval(0);
    log.addListener(recoveryListener);
    log.addListener(propertiesListener);
    log.activate();
    assertEquals(19, recoveryListener.getEvents().size());
    log.deactivate();
    assertEquals(2, propertiesListener.getEvents().size());
  }

  private static void write(RollingLog log, int size)
  {
    for (int i = 0; i < size; i += MESSAGE.length())
    {
      log.log(MESSAGE);
    }
  }
}
