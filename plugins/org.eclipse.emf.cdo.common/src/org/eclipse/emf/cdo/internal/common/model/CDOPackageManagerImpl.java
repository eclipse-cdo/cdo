/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.model.core.CDOCorePackageImpl;
import org.eclipse.emf.cdo.internal.common.model.resource.CDOResourcePackageImpl;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public abstract class CDOPackageManagerImpl extends Container<CDOPackage> implements CDOPackageManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOPackageManagerImpl.class);

  private ConcurrentMap<String, CDOPackage> packages = new ConcurrentHashMap<String, CDOPackage>();

  private CDOCorePackage cdoCorePackage;

  private CDOResourcePackage cdoResourcePackage;

  public CDOPackageManagerImpl()
  {
    addPackage(cdoCorePackage = new CDOCorePackageImpl(this));
    addPackage(cdoResourcePackage = new CDOResourcePackageImpl(this));
  }

  public CDOPackage lookupPackage(String uri)
  {
    if (uri == null)
    {
      return null;
    }

    return packages.get(uri);
  }

  public int getPackageCount()
  {
    return packages.size();
  }

  public CDOPackage[] getPackages()
  {
    return packages.values().toArray(new CDOPackage[packages.size()]);
  }

  public CDOPackage[] getElements()
  {
    return getPackages();
  }

  @Override
  public boolean isEmpty()
  {
    return packages.isEmpty();
  }

  public CDOCorePackage getCDOCorePackage()
  {
    return cdoCorePackage;
  }

  public CDOResourcePackage getCDOResourcePackage()
  {
    return cdoResourcePackage;
  }

  public List<CDOPackage> getTransientPackages()
  {
    List<CDOPackage> result = new ArrayList<CDOPackage>();
    for (CDOPackage cdoPackage : packages.values())
    {
      if (!cdoPackage.isPersistent())
      {
        result.add(cdoPackage);
      }
    }

    return result;
  }

  public void addPackage(CDOPackage cdoPackage)
  {
    String uri = cdoPackage.getPackageURI();
    if (uri == null)
    {
      throw new IllegalArgumentException("uri == null");
    }

    CDOPackage existing = packages.putIfAbsent(uri, cdoPackage);
    if (existing == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Added package: {0}", cdoPackage);
      }

      fireElementAddedEvent(cdoPackage);
    }
    else
    {
      throw new IllegalStateException("Duplicate package: " + cdoPackage);
    }
  }

  public void removePackage(CDOPackage cdoPackage)
  {
    packages.remove(cdoPackage.getPackageURI());
    fireElementRemovedEvent(cdoPackage);
  }

  /**
   * @param cdoPackage
   *          is a proxy CDO package. The implementer of this method must only use the package URI of the cdoPackage
   *          passed in.
   */
  protected abstract void resolve(CDOPackage cdoPackage);

  /**
   * Only called on clients for generated models
   */
  protected abstract String provideEcore(CDOPackage cdoPackage);
}
