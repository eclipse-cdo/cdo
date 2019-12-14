/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.StubCDORevision;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;

import org.eclipse.emf.ecore.EcorePackage;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CachingCDOWorkspaceBase extends FolderCDOWorkspaceBase
{
  private static final CDORevision ADDED = new StubCDORevision(EcorePackage.Literals.EOBJECT)
  {
    @Override
    public String toString()
    {
      return "ADDED";
    }
  };

  private final Map<CDOID, CDORevision> revisions = new LinkedHashMap<>();

  private final Map<CDOID, Integer> detachedVersions = new LinkedHashMap<>();

  public CachingCDOWorkspaceBase(File folder)
  {
    super(folder);
  }

  @Override
  public void init(InternalCDOWorkspace workspace)
  {
    super.init(workspace);

    for (CDOID id : getIDs())
    {
      CDORevision revision = getRevision(id);
      if (revision == null)
      {
        revision = ADDED;
      }

      revisions.put(id, revision);
    }

    handleAddedAndDetachedObjects(new AddedAndDetachedHandler()
    {
      @Override
      public void handleAddedAndDetachedHandler(CDOID id, int detachedVersion)
      {
        detachedVersions.put(id, detachedVersion);
      }
    });
  }

  @Override
  protected void doRegisterChangedOrDetachedObject(InternalCDORevision revision)
  {
    super.doRegisterChangedOrDetachedObject(revision);

    CDOID id = revision.getID();
    if (revisions.containsKey(id))
    {
      return;
    }

    revisions.put(id, revision);
  }

  @Override
  protected void doRegisterAddedAndDetachedObject(InternalCDORevision revision)
  {
    super.doRegisterAddedAndDetachedObject(revision);

    CDOID id = revision.getID();
    int detachedVersion = revision.getVersion();
    detachedVersions.put(id, detachedVersion);
  }

  @Override
  protected void doRegisterAddedObject(CDOID id)
  {
    super.doRegisterAddedObject(id);
    revisions.put(id, ADDED);
  }

  @Override
  protected void doDeregisterObject(CDOID id)
  {
    super.doDeregisterObject(id);
    revisions.remove(id);
  }

  @Override
  protected void doClear()
  {
    super.doClear();
    revisions.clear();
    detachedVersions.clear();
  }
}
