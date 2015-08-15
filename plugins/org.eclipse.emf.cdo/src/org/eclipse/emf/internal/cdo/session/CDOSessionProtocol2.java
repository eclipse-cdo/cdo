/*
 * Copyright (c) 2004-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * @author Eike Stepper
 */
public interface CDOSessionProtocol2 extends CDOSessionProtocol
{
  public void openedSession();
}
