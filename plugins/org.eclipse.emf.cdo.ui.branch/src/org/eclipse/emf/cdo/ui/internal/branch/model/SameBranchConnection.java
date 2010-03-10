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
package org.eclipse.emf.cdo.ui.internal.branch.model;


import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * A connection to a {@link AbstractBranchPointNode} in the same branch.
 */
public class SameBranchConnection extends GraphConnection
{
  public SameBranchConnection(Graph graphModel, int style, GraphNode source, GraphNode destination)
  {
    super(graphModel, style, source, destination);
  }
}
