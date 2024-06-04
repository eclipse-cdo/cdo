/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.impl.SystemImpl;
import org.eclipse.emf.cdo.lm.internal.client.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.Path;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LMViewProvider extends AbstractCDOViewProvider
{
  public static final String SCHEME = "cdo.lm";

  private static final String PARAM_BRANCH = "branch";

  private static final String PARAM_TIME_STAMP = "timeStamp";

  public LMViewProvider()
  {
    this(DEFAULT_PRIORITY);
  }

  public LMViewProvider(int priority)
  {
    super(schemeRegex(SCHEME), priority);
  }

  @Override
  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    LMViewProviderAdapter adapter = LMViewProviderAdapter.get(resourceSet);
    if (adapter == null)
    {
      adapter = LMViewProviderAdapter.create(resourceSet);
    }

    return adapter.getView(uri);
  }

  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    CDOSession session = view.getSession();
    IRegistry<String, Object> properties = session.properties();
    ISystemDescriptor systemDescriptor = (ISystemDescriptor)properties.get(SystemDescriptor.KEY_SYSTEM_DESCRIPTOR);
    String moduleName = (String)properties.get(SystemDescriptor.KEY_MODULE_NAME);
    String systemName = systemDescriptor.getSystemName();
    String authority = createAuthority(systemName, moduleName);

    String[] segments = StringUtil.isEmpty(path) ? null : new Path(path).segments();
    return URI.createHierarchicalURI(SCHEME, authority, null, segments, null, null);
  }

  @Override
  public URI getViewURI(URI uri)
  {
    if (uri == null)
    {
      return null;
    }

    String scheme = uri.scheme();
    if (!SCHEME.equals(scheme))
    {
      return null;
    }

    return super.getViewURI(uri);
  }

  public static URI createViewURI(AssemblyModule module)
  {
    Assembly assembly = module.getAssembly();
    String systemName = assembly.getSystemName();
    String moduleName = module.getName();
    String authority = createAuthority(systemName, moduleName);

    CDOBranchPointRef branchPoint = module.getBranchPoint();
    Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PARAM_BRANCH, branchPoint.getBranchPath());
    parameters.put(PARAM_TIME_STAMP, branchPoint.getTimeStampSpec());
    String query = CDOURIUtil.formatQuery(parameters);

    return URI.createHierarchicalURI(SCHEME, authority, null, query, null);
  }

  private static String createAuthority(String systemName, String moduleName)
  {
    return URI.encodeAuthority(systemName + System.SEPARATOR + moduleName, false);
  }

  private static CDOView openView(ResourceSet resourceSet, URI uri)
  {
    CDORepository moduleRepository = getModuleRepository(uri);
    CDOSession session = moduleRepository.acquireSession();

    CDOBranchPoint branchPoint = getBranchPoint(uri, session);
    InternalCDOView view = (InternalCDOView)session.openView(branchPoint, resourceSet);
    view.setRepositoryName(moduleRepository.getName());
    view.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        moduleRepository.releaseSession();
      }
    });

    return view;
  }

  private static CDORepository getModuleRepository(URI uri)
  {
    if (SCHEME.equals(uri.scheme()))
    {
      String authority = URI.decode(uri.authority());
      if (authority != null)
      {
        int colon = authority.indexOf(System.SEPARATOR);
        if (colon != -1)
        {
          String systemName = authority.substring(0, colon);
          String moduleName = authority.substring(colon + 1);
          if (SystemImpl.isValidName(systemName) && SystemImpl.isValidName(moduleName))
          {
            ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(systemName);
            if (systemDescriptor != null)
            {
              return systemDescriptor.getModuleRepository(moduleName);
            }
          }
        }
      }
    }

    return null;
  }

  private static CDOBranchPoint getBranchPoint(URI uri, CDOSession session)
  {
    Map<String, String> parameters = CDOURIUtil.getParameters(uri.query());

    String branchPath = parameters.get(PARAM_BRANCH);
    CDOBranch branch = session.getBranchManager().getBranch(branchPath);

    String timeSpec = parameters.get(PARAM_TIME_STAMP);
    boolean timeUnspecified = StringUtil.isEmpty(timeSpec) || CDOCommonUtil.UNSPECIFIED_DATE_STRING.equals(timeSpec);
    long timeStamp = timeUnspecified ? CDOBranchPoint.UNSPECIFIED_DATE : Long.parseLong(timeSpec);

    return branch.getPoint(timeStamp);
  }

  /**
   * @author Eike Stepper
   */
  private static final class LMViewProviderAdapter extends AdapterImpl
  {
    private final Map<URI, CDOView> views = new HashMap<>();

    private LMViewProviderAdapter()
    {
    }

    public CDOView getView(URI uri)
    {
      synchronized (views)
      {
        CDOView view = views.get(uri);
        if (view == null)
        {
          ResourceSet resourceSet = getTarget();
          CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
          if (viewSet != null)
          {
            for (CDOView viewSetView : viewSet.getViews())
            {

              if (uri == viewSetView.getURI())
              {
                view = viewSetView;
                break;
              }
            }
          }

          if (view == null)
          {
            view = openView(resourceSet, uri);
          }

          if (view != null)
          {
            views.put(uri, view);

            view.addListener(new LifecycleEventAdapter()
            {
              @Override
              protected void onDeactivated(ILifecycle lifecycle)
              {
                synchronized (views)
                {
                  views.remove(uri);
                }
              }
            });
          }
        }

        return view;
      }
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type == LMViewProviderAdapter.class;
    }

    @Override
    public ResourceSet getTarget()
    {
      return (ResourceSet)super.getTarget();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      ResourceSet resourceSet = getTarget();
      if (newTarget == resourceSet)
      {
        return;
      }

      if (newTarget == null)
      {
        CDOView[] array;
        synchronized (views)
        {
          array = views.values().toArray(new CDOView[views.size()]);
          views.clear();
        }

        for (CDOView view : array)
        {
          try
          {
            view.close();
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
      else if (resourceSet != null)
      {
        throw new IllegalStateException("Illegal attempt to retarget LMViewProviderAdapter to " + newTarget);
      }

      super.setTarget(newTarget);
    }

    public static LMViewProviderAdapter get(ResourceSet resourceSet)
    {
      EList<Adapter> adapters = resourceSet.eAdapters();

      LMViewProviderAdapter adapter = (LMViewProviderAdapter)EcoreUtil.getAdapter(adapters, LMViewProviderAdapter.class);
      if (adapter != null && adapter.getTarget() != resourceSet)
      {
        adapters.remove(adapter);
        adapter = null;
      }

      return adapter;
    }

    public static LMViewProviderAdapter create(ResourceSet resourceSet)
    {
      LMViewProviderAdapter adapter = new LMViewProviderAdapter();
      resourceSet.eAdapters().add(adapter);
      return adapter;
    }
  }
}
