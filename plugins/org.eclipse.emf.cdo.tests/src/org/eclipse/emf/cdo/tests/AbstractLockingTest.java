/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;

import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class AbstractLockingTest extends AbstractCDOTest
{
  protected static void readLock(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    assertEquals(true, cdoObject.cdoReadLock().tryLock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
  }

  protected static void readUnlock(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    cdoObject.cdoReadLock().unlock();
  }

  protected static void writeLock(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    assertEquals(true, cdoObject.cdoWriteLock().tryLock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
  }

  protected static void writeUnlock(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    cdoObject.cdoWriteLock().unlock();
  }

  protected static void writeOption(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    assertEquals(true, cdoObject.cdoWriteOption().tryLock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
  }

  protected static void writeUnoption(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    cdoObject.cdoWriteOption().unlock();
  }

  protected static void assertReadLock(boolean expected, EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    assertEquals(expected, cdoObject.cdoReadLock().isLocked());
  }

  protected static void assertWriteLock(boolean expected, EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    assertEquals(expected, cdoObject.cdoWriteLock().isLocked());
  }

  protected static void assertWriteOption(boolean expected, EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    assertEquals(expected, cdoObject.cdoWriteOption().isLocked());
  }
}
