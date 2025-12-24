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
 */
package org.eclipse.net4j.internal.trace;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.log.RollingLog;
import org.eclipse.net4j.util.om.log.RollingLog.LogLine;

/**
 * @author Eike Stepper
 */
public class LogAnalyzer
{
  public static void analyzeLog(String logFile, int bufferID)
  {
    for (CloseableIterator<LogLine> it = RollingLog.iterator("C:\\Develop\\cdo-master\\trace"); it.hasNext();)
    {
      LogLine logLine = it.next();
      System.out.println(logLine);
    }
  }

  public static void main(String[] args)
  {
    String logFile = args[0];
    int bufferID = Integer.parseInt(args[1]);
    analyzeLog(logFile, bufferID);
  }
}
