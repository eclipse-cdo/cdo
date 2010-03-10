/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.branch.node;

import org.eclipse.zest.core.widgets.IContainer;

/**
 * The single root node of a branch tree.
 */
public class RootNode extends BranchTreeNode
{
  public RootNode(IContainer graphModel, int style, BranchRootFigure figure)
  {
    super(graphModel, style, figure);
  }
}
