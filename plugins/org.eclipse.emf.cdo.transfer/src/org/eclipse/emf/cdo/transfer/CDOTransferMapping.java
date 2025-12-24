/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.core.runtime.IPath;

/**
 * The mapping of a source {@link CDOTransferElement element} to a target element in the context of a specific {@link CDOTransfer transfer}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface CDOTransferMapping extends Comparable<CDOTransferMapping>
{
  public static final CDOTransferMapping[] NO_CHILDREN = {};

  public CDOTransfer getTransfer();

  public CDOTransferElement getSource();

  public CDOTransferMapping getParent();

  public boolean isRoot();

  public boolean isDirectory();

  public String getName();

  public void setName(String name);

  public IPath getRelativePath();

  public void setRelativePath(IPath path);

  public void setRelativePath(String path);

  public void accept(Visitor visitor);

  public CDOTransferMapping[] getChildren();

  public CDOTransferMapping getChild(IPath path);

  public CDOTransferMapping getChild(String path);

  public void unmap();

  public CDOTransferType getTransferType();

  public void setTransferType(CDOTransferType transferType);

  public IPath getFullPath();

  public Status getStatus();

  public CDOTransferElement getTarget();

  /**
   * Enumerates the possibles values of {@link CDOTransferMapping#getStatus()}.
   *
   * @author Eike Stepper
   */
  public enum Status
  {
    NEW, MERGE, CONFLICT
  }

  /**
   * A call-back that is called for a {@link CDOTransferMapping mapping} and all its {@link CDOTransferMapping#getChildren() children} when
   * passed into its {@link CDOTransferMapping#accept(Visitor) accept()} method.
   *
   * @author Eike Stepper
   */
  public interface Visitor
  {
    public boolean visit(CDOTransferMapping mapping);
  }
}
