/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.emf.notifications.impl;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnLockingHandler;

/**
 * @author Martin Fluegge
 */
public class DawnEMFLockingHandler extends BasicDawnLockingHandler
{
  public DawnEMFLockingHandler(IDawnEditor editor)
  {
    super(editor);
  }
}
