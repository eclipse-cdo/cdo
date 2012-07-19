/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.ide.Node;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SafeActionDelegate;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Victor Roldan Betancort
 */
public abstract class SessionAwareActionDelegate extends SafeActionDelegate
{
  public SessionAwareActionDelegate()
  {
  }

  public CDOSession getSession()
  {
    Node node = UIUtil.getElement(getSelection(), Node.class);
    return node.getRepositoryProject().getView().getSession();
  }

  public IWorkbenchPage getPage()
  {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
  }
}
