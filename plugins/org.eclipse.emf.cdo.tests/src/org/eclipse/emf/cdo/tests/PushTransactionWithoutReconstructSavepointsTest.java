/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
