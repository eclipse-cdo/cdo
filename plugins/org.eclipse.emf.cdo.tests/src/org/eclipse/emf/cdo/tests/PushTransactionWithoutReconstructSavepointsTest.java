/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class PushTransactionWithoutReconstructSavepointsTest extends PushTransactionTest
{
  @Override
  protected CDOPushTransaction openPushTransaction(CDOTransaction transaction, File file) throws IOException
  {
    return new CDOPushTransaction(transaction, file, false);
  }
}
