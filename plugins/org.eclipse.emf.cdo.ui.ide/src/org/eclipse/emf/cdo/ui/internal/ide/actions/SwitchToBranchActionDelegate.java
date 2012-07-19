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

import org.eclipse.emf.cdo.ui.ide.Node.BranchNode;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;

/**
 * @author Victor Roldan Betancort
 */
public class SwitchToBranchActionDelegate extends SessionAwareActionDelegate
{
  public SwitchToBranchActionDelegate()
  {
  }

  @Override
  protected void safeRun() throws Exception
  {
    BranchNode branchNode = UIUtil.getElement(getSelection(), BranchNode.class);
    CDOView view = branchNode.getRepositoryProject().getView();
    view.setBranch(branchNode.getBranch());
  }
}
