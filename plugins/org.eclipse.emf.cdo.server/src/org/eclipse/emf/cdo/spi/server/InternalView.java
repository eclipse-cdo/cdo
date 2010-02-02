/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IView;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalView extends IView
{
  public InternalSession getSession();

  public InternalRepository getRepository();

  public boolean[] changeTarget(CDOBranchPoint branchPoint, List<CDOID> invalidObjects);

  public void subscribe(CDOID id);

  public void unsubscribe(CDOID id);

  public boolean hasSubscription(CDOID id);

  public void clearChangeSubscription();

  public void doClose();
}
