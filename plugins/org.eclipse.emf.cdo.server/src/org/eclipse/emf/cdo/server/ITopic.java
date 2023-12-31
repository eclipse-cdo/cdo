/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.net4j.util.container.IContainer;

/**
 * @author Eike Stepper
 * @since 4.17
 */
public interface ITopic extends IContainer<ISession>
{
  public ITopicManager getManager();

  public String getID();

  public ISession[] getSessions();
}
