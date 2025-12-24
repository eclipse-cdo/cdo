/*
 * Copyright (c) 2009-2012, 2015, 2019, 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.emf.cdo.common.util.CDONameProvider;

import org.eclipse.net4j.util.event.IEvent;

/**
 * A persistent, named {@link CDOBranchPoint branch point}.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOBranchTag extends CDOBranchPoint, CDONameProvider, Comparable<CDOBranchTag>
{
  /**
   * Returns the branch manager that manages this branch, never <code>null</code>.
   *
   * @since 4.11
   */
  public CDOBranchManager getBranchManager();

  /**
   * Returns the name of this branch tag.
   */
  @Override
  public String getName();

  /**
   * @since 4.11
   */
  public void setName(String name);

  /**
   * @since 4.11
   */
  public void move(CDOBranchPoint branchPoint);

  /**
   * @since 4.11
   */
  public void delete();

  /**
   * @since 4.11
   */
  public boolean isDeleted();

  /**
   * An {@link IEvent event} fired from a {@link CDOBranchTag branch tag}.
   *
   * @author Eike Stepper
   * @since 4.11
   */
  public interface TagEvent extends IEvent
  {
    public CDOBranchTag getTag();
  }

  /**
   * A {@link TagEvent tag event} fired when a branch tag was renamed.
   *
   * @author Eike Stepper
   * @since 4.11
   */
  public interface TagRenamedEvent extends TagEvent
  {
    public String getOldName();

    public String getNewName();
  }

  /**
   * A {@link TagEvent tag event} fired when a branch tag was moved.
   *
   * @author Eike Stepper
   * @since 4.11
   */
  public interface TagMovedEvent extends TagEvent
  {
    public CDOBranchPoint getOldBranchPoint();

    public CDOBranchPoint getNewBranchPoint();
  }

  /**
   * A {@link TagEvent tag event} fired when a branch tag was deleted.
   *
   * @author Eike Stepper
   * @since 4.11
   */
  public interface TagDeletedEvent extends TagEvent
  {
  }
}
