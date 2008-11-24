/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import java.sql.PreparedStatement;

/**
 * Wrapper for a prepared statement that is cleaned up when it is cached in a WeakReferenceCache and gc'd. Note that
 * this is just a wrapper with access to its wrapped object. There's no interface delegation, because the interface
 * delegation would also put the necessity to wrap resultSets and maybe even more, which seems to much overkill for a
 * simple internal implementation.
 * 
 * @author Stefan Winkler
 */
public class WrappedPreparedStatement
{
  private PreparedStatement wrappedStatement = null;

  public WrappedPreparedStatement(PreparedStatement ps)
  {
    wrappedStatement = ps;
  }

  public PreparedStatement getWrappedStatement()
  {
    return wrappedStatement;
  }

  @Override
  protected void finalize() throws Throwable
  {
    wrappedStatement.close();
  }

  @Override
  public String toString()
  {
    return "[Wrapped " + wrappedStatement.toString() + "]";
  }
}
