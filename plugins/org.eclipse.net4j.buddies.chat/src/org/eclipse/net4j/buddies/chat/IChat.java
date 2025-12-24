/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.chat;

import org.eclipse.net4j.buddies.common.IFacility;

/**
 * @author Eike Stepper
 */
public interface IChat extends IFacility
{
  public static final String TYPE = "chat"; //$NON-NLS-1$

  public IComment[] getComments();

  public void sendComment(String text);
}
