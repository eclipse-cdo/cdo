/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

/**
 * Isolated timeout regression for schema access cleanup.
 * <p>
 * This class is deliberately excluded from {@link AllTests} because its timeout is configured on the test JVM. Run
 * {@link #testTimedOutWriterDoesNotBlockReaders()} with a short
 * {@code org.eclipse.net4j.internal.db.DBDatabase.TIMEOUT_SCHEMA_ACCESS} VM argument.
 *
 * @author Eike Stepper
 */
public class DBSchemaAccessTimeoutTest extends DBSchemaAccessTest
{
  public void testTimedOutWriterDoesNotBlockReaders() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);
    Worker writer = startSchemaWriter("TABLE1");

    writer.join(WAIT_MILLIS);
    assertFalse("Schema writer did not time out", writer.isAlive()); //$NON-NLS-1$

    Throwable failure = writer.getFailure();
    boolean timedOut = false;

    for (Throwable cause = failure; cause != null; cause = cause.getCause())
    {
      if (cause instanceof TimeoutRuntimeException)
      {
        timedOut = true;
        break;
      }
    }

    assertTrue("Schema writer did not report a timeout: " + failure, timedOut); //$NON-NLS-1$

    IDBConnection otherConnection = database.getConnection();
    otherConnection.prepareStatement("SELECT 2", ReuseProbability.HIGH).close();
    statement.close();
  }
}
