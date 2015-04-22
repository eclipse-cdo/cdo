/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.spi;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public enum DawnState
{
  LOCKED_LOCALLY, LOCKED_REMOTELY, DIRTY, CLEAN, CONFLICT
}
