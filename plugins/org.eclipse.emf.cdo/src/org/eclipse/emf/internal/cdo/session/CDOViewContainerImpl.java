/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewContainer;
import org.eclipse.emf.cdo.view.CDOViewProvider;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDOViewContainerImpl extends Container<CDOView> implements CDOViewContainer
{
  public static final ThreadLocal<CDOViewProvider> VIEW_PROVIDER = new ThreadLocal<>();

  private Set<InternalCDOView> views = new HashSet<>();

  @ExcludeFromDump
  private int lastViewID;

  public CDOViewContainerImpl()
  {
  }

  @Override
  public InternalCDOView getView(int viewID)
  {
    checkActive();
    for (InternalCDOView view : getViews())
    {
      if (view.getViewID() == viewID)
      {
        return view;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOView[] getViews()
  {
    return getViews(null);
  }

  @Override
  public InternalCDOView[] getViews(CDOBranch branch)
  {
    List<InternalCDOView> views = getViews(branch, false);
    return views.toArray(new InternalCDOView[views.size()]);
  }

  protected List<InternalCDOView> getViews(CDOBranch branch, boolean writeable)
  {
    checkActive();
    synchronized (views)
    {
      List<InternalCDOView> result = new ArrayList<>();
      for (InternalCDOView view : views)
      {
        if (branch != null && branch != view.getBranch())
        {
          continue;
        }

        if (writeable && view.isReadOnly())
        {
          continue;
        }

        result.add(view);
      }

      return result;
    }
  }

  @Override
  public CDOView[] getElements()
  {
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    return views.isEmpty();
  }

  @Override
  public InternalCDOView openView(CDOBranchPoint target, ResourceSet resourceSet)
  {
    return openView(target.getBranch(), target.getTimeStamp(), resourceSet);
  }

  @Override
  public InternalCDOView openView(CDOBranchPoint target)
  {
    return openView(target, createResourceSet());
  }

  @Override
  public InternalCDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView(branch, timeStamp);
    initView(view, resourceSet);
    return view;
  }

  @Override
  public InternalCDOView openView(CDOBranch branch, long timeStamp)
  {
    return openView(branch, timeStamp, createResourceSet());
  }

  @Override
  public InternalCDOView openView(CDOBranch branch)
  {
    return openView(branch, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  @Override
  public InternalCDOView openView(long timeStamp)
  {
    return openView(getMainBranch(), timeStamp);
  }

  @Override
  public InternalCDOView openView(ResourceSet resourceSet)
  {
    return openView(getMainBranch(), CDOBranchPoint.UNSPECIFIED_DATE, resourceSet);
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOView openView()
  {
    return openView(CDOBranchPoint.UNSPECIFIED_DATE);
  }

  @Override
  public InternalCDOView openView(String durableLockingID)
  {
    return openView(durableLockingID, createResourceSet());
  }

  @Override
  public InternalCDOView openView(String durableLockingID, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView(durableLockingID);
    initView(view, resourceSet);
    return view;
  }

  /**
   * @since 2.0
   */
  public void viewDetached(InternalCDOView view)
  {
    synchronized (views)
    {
      if (!views.remove(view))
      {
        return;
      }
    }

    if (isActive())
    {
      try
      {
        LifecycleUtil.deactivate(view);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    fireElementRemovedEvent(view);
  }

  /**
   * @since 2.0
   */
  protected InternalCDOView createView(CDOBranch branch, long timeStamp)
  {
    return new CDOViewImpl((CDOSession)this, branch, timeStamp);
  }

  /**
   * @since 4.0
   */
  protected InternalCDOView createView(String durableLockingID)
  {
    return new CDOViewImpl((CDOSession)this, durableLockingID);
  }

  protected ResourceSet createResourceSet()
  {
    return new ResourceSetImpl();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (InternalCDOView view : views.toArray(new InternalCDOView[views.size()]))
    {
      try
      {
        view.close();
      }
      catch (RuntimeException ignore)
      {
      }
    }

    views.clear();
    super.doDeactivate();
  }

  /**
   * @since 2.0
   */
  protected void initView(InternalCDOView view, ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = SessionUtil.prepareResourceSet(resourceSet);

    synchronized (views)
    {
      view.setViewID(++lastViewID);
      initViewUnsynced(view); // Must be called before setting the view provider!

      CDOViewProvider viewProvider = VIEW_PROVIDER.get();
      if (viewProvider != null)
      {
        view.setProvider(viewProvider);
        VIEW_PROVIDER.remove();
      }

      // Link ViewSet with View
      view.setViewSet(viewSet);
      viewSet.add(view);

      views.add(view); // From now the view.invalidate() method can be called!
    }

    try
    {
      view.activate();

      ResourceSetConfigurer.Registry.INSTANCE.configureResourceSet(resourceSet, view);

      fireElementAddedEvent(view);
    }
    catch (RuntimeException ex)
    {
      synchronized (views)
      {
        views.remove(view);
      }

      viewSet.remove(view);
      view.deactivate();
      throw ex;
    }
  }

  protected void initViewUnsynced(InternalCDOView view)
  {
  }

  protected abstract CDOBranch getMainBranch();
}
