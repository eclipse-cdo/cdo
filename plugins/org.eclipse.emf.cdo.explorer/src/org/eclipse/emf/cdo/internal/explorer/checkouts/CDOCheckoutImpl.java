/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.AbstractManager;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public abstract class CDOCheckoutImpl extends AbstractElement implements CDOCheckout
{
  public static final String PROP_ROOT_ID = "rootID";

  public static final String PROP_READ_ONLY = "readOnly";

  public static final String PROP_TIME_STAMP = "timeStamp";

  public static final String PROP_BRANCH_ID = "branchID";

  public static final String PROP_BRANCH_PATH = "branchPath";

  public static final String PROP_BRANCH_POINTS = "branchPoints";

  public static final String PROP_REPOSITORY = "repository";

  public static final String EDITOR_PROPERTIES = "editor.properties";

  private static final String CHECKOUT_KEY = CDOCheckout.class.getName();

  private static final CDOBranchPoint[] NO_BRANCH_POINTS = {};

  private static final int BRANCH_POINTS_MAX = 10;

  private static final String BRANCH_POINT_SEPARATOR = ",";

  private static final String BRANCH_AND_POINT_SEPARATOR = "_";

  private final Map<CDOID, String> editorIDs = new WeakHashMap<CDOID, String>();

  private CDORepository repository;

  private int branchID;

  private String branchPath;

  private String branchPoints;

  private long timeStamp;

  private boolean readOnly;

  private CDOID rootID;

  private State state = State.Closed;

  private CDOView view;

  private EObject rootObject;

  public CDOCheckoutImpl()
  {
  }

  @Override
  public final CDOCheckoutManagerImpl getManager()
  {
    return OM.getCheckoutManager();
  }

  public final CDORepository getRepository()
  {
    return repository;
  }

  public final int getBranchID()
  {
    return branchID;
  }

  public final void setBranchID(int branchID)
  {
    setBranchPoint(branchID, timeStamp);
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public final void setBranchPath(String branchPath)
  {
    if (!ObjectUtil.equals(this.branchPath, branchPath))
    {
      this.branchPath = branchPath;
      save();
    }
  }

  public final CDOBranchPoint getBranchPoint()
  {
    return view;
  }

  public final void setBranchPoint(CDOBranchPoint branchPoint)
  {
    setBranchPoint(branchPoint.getBranch().getID(), branchPoint.getTimeStamp());
  }

  public final void setBranchPoint(int branchID, long timeStamp)
  {
    boolean changed = addBranchPoint(this.branchID, this.timeStamp);

    if (this.branchID != branchID || this.timeStamp != timeStamp)
    {
      this.branchID = branchID;
      this.timeStamp = timeStamp;

      if (isOpen())
      {
        branchPath = doSetBranchPoint(branchID, timeStamp);
      }
      else
      {
        branchPath = null;
      }

      changed = true;
    }

    if (changed)
    {
      save();
    }
  }

  protected String doSetBranchPoint(int branchID, long timeStamp)
  {
    CDOBranch branch = view.getSession().getBranchManager().getBranch(branchID);
    view.setBranchPoint(branch.getPoint(timeStamp));
    return branch.getPathName();
  }

  public CDOBranchPoint[] getBranchPoints()
  {
    if (branchPoints != null && isOpen())
    {
      List<CDOBranchPoint> result = new ArrayList<CDOBranchPoint>();
      CDOBranchManager branchManager = view.getSession().getBranchManager();

      for (String token : branchPoints.split(BRANCH_POINT_SEPARATOR))
      {
        String[] branchAndPoint = token.split(BRANCH_AND_POINT_SEPARATOR);
        int branchID = Integer.parseInt(branchAndPoint[0]);
        CDOBranch branch = branchManager.getBranch(branchID);
        if (branch != null)
        {
          long timeStamp;
          if (branchAndPoint.length >= 2)
          {
            timeStamp = Long.parseLong(branchAndPoint[1]);
          }
          else
          {
            timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
          }

          result.add(branch.getPoint(timeStamp));
        }
      }

      return result.toArray(new CDOBranchPoint[result.size()]);
    }

    return NO_BRANCH_POINTS;
  }

  public boolean addBranchPoint(CDOBranchPoint branchPoint)
  {
    int branchID = branchPoint.getBranch().getID();
    long timeStamp = branchPoint.getTimeStamp();

    boolean changed = addBranchPoint(branchID, timeStamp);
    if (changed)
    {
      save();
    }

    return changed;
  }

  private boolean addBranchPoint(int branchID, long timeStamp)
  {
    String oldBranchPoints = branchPoints;

    String newToken = Integer.toString(branchID);
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      newToken += BRANCH_AND_POINT_SEPARATOR + timeStamp;
    }

    if (branchPoints != null)
    {
      StringBuilder builder = new StringBuilder(newToken);
      int count = 1;

      for (String token : branchPoints.split(BRANCH_POINT_SEPARATOR))
      {
        if (count++ == BRANCH_POINTS_MAX)
        {
          break;
        }

        if (!token.equals(newToken))
        {
          builder.append(BRANCH_POINT_SEPARATOR);
          builder.append(token);
        }
      }

      branchPoints = builder.toString();
    }
    else
    {
      branchPoints = newToken;
    }

    if (!ObjectUtil.equals(branchPoints, oldBranchPoints))
    {
      return true;
    }

    return false;
  }

  public final long getTimeStamp()
  {
    return timeStamp;
  }

  public final void setTimeStamp(long timeStamp)
  {
    setBranchPoint(branchID, timeStamp);
  }

  public final boolean isReadOnly()
  {
    return readOnly;
  }

  public final void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  public final CDOID getRootID()
  {
    return rootID;
  }

  public final void setRootID(CDOID rootID)
  {
    this.rootID = rootID;
  }

  public final State getState()
  {
    return state;
  }

  public final boolean isOpen()
  {
    return view != null;
  }

  public final void open()
  {
    boolean opened = false;

    synchronized (this)
    {
      if (!isOpen())
      {
        try
        {
          state = State.Opening;

          prepareOpen();
          CDOSession session = ((CDORepositoryImpl)repository).openCheckout(this);

          view = openView(session);
          view.addListener(new LifecycleEventAdapter()
          {
            @Override
            protected void onDeactivated(ILifecycle lifecycle)
            {
              close();
            }
          });

          configureView(view);

          rootObject = loadRootObject();
          rootObject.eAdapters().add(this);

          state = State.Open;
        }
        catch (RuntimeException ex)
        {
          state = State.Closed;
          throw ex;
        }
        catch (Error ex)
        {
          state = State.Closed;
          throw ex;
        }

        opened = true;
      }
    }

    if (opened)
    {
      CDOCheckoutManagerImpl manager = getManager();
      if (manager != null)
      {
        manager.fireCheckoutOpenEvent(this, view, true);
      }
    }
  }

  protected void prepareOpen()
  {
    // Do nothing.
  }

  public final void close()
  {
    boolean closed = false;
    CDOView oldView = null;

    synchronized (this)
    {
      if (isOpen())
      {
        try
        {
          state = State.Closing;
          oldView = view;

          try
          {
            rootObject.eAdapters().remove(this);
            closeView();
          }
          finally
          {
            ((CDORepositoryImpl)repository).closeCheckout(this);
          }
        }
        finally
        {
          rootObject = null;
          view = null;
          state = State.Closed;
        }

        closed = true;
      }
    }

    if (closed)
    {
      CDOCheckoutManagerImpl manager = getManager();
      if (manager != null)
      {
        manager.fireCheckoutOpenEvent(this, oldView, false);
      }
    }
  }

  public final CDOView getView()
  {
    return view;
  }

  public final EObject getRootObject()
  {
    return rootObject;
  }

  public final ObjectType getRootType()
  {
    return ObjectType.valueFor(rootObject);
  }

  protected ResourceSetImpl createResourceSet()
  {
    return new ResourceSetImpl();
  }

  public final CDOView openView()
  {
    return openView(createResourceSet());
  }

  public CDOView openView(ResourceSet resourceSet)
  {
    return openView(readOnly, resourceSet);
  }

  public final CDOView openView(boolean readOnly)
  {
    return openView(readOnly, createResourceSet());
  }

  public CDOView openView(boolean readOnly, ResourceSet resourceSet)
  {
    return openAndConfigureView(readOnly, resourceSet);
  }

  public final CDOTransaction openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  public CDOTransaction openTransaction(ResourceSet resourceSet)
  {
    if (readOnly)
    {
      throw new ReadOnlyException("Checkout '" + getLabel() + "' is read-only");
    }

    return (CDOTransaction)openAndConfigureView(false, resourceSet);
  }

  private CDOView openAndConfigureView(boolean readOnly, ResourceSet resourceSet)
  {
    CDOView view = doOpenView(readOnly, resourceSet);
    return configureView(view);
  }

  protected CDOView doOpenView(boolean readOnly, ResourceSet resourceSet)
  {
    if (view == null)
    {
      return null;
    }

    CDOSession session = view.getSession();
    CDOBranch branch = view.getBranch();
    CDOBranchPoint head = branch.getHead();

    if (readOnly)
    {
      return session.openView(head, resourceSet);
    }

    return session.openTransaction(head, resourceSet);
  }

  protected CDOView configureView(final CDOView view)
  {
    CDOUtil.configureView(view);
    ((InternalCDOView)view).setRepositoryName(repository.getLabel());

    view.properties().put(CHECKOUT_KEY, this);
    view.addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewLocksChangedEvent)
        {
          CDOViewLocksChangedEvent e = (CDOViewLocksChangedEvent)event;
          List<CDOObject> objects = e.getAffectedObjects(view);
          if (!objects.isEmpty())
          {
            List<Object> elements = new ArrayList<Object>();
            for (CDOObject object : objects)
            {
              elements.add(object);
            }

            getManager().fireElementsChangedEvent(elements);
          }
        }
      }
    });

    return view;
  }

  public String getEditorID(CDOID objectID)
  {
    synchronized (editorIDs)
    {
      String editorID = editorIDs.get(objectID);
      if (editorID != null)
      {
        return editorID;
      }

      Properties properties = AbstractManager.loadProperties(getFolder(), EDITOR_PROPERTIES);
      if (properties != null)
      {
        String idString = getCDOIDString(objectID);
        return properties.getProperty(idString);
      }

      return null;
    }
  }

  public void setEditorID(CDOID objectID, String editorID)
  {
    synchronized (editorIDs)
    {
      String exisingEditorID = editorIDs.get(objectID);
      if (ObjectUtil.equals(exisingEditorID, editorID))
      {
        return;
      }

      Properties properties = AbstractManager.loadProperties(getFolder(), EDITOR_PROPERTIES);
      if (properties == null)
      {
        properties = new Properties();
      }

      String idString = getCDOIDString(objectID);
      properties.setProperty(idString, editorID);

      saveProperties(EDITOR_PROPERTIES, properties);
      editorIDs.put(objectID, editorID);
    }
  }

  @Override
  public void delete(boolean deleteContents)
  {
    close();

    CDOCheckoutManagerImpl manager = getManager();
    if (manager != null)
    {
      manager.removeElement(this);
    }

    super.delete(deleteContents);

    ((CDORepositoryImpl)repository).removeCheckout(this);
    repository = null;
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    if (type == CDOCheckout.class)
    {
      return true;
    }

    return super.isAdapterForType(type);
  }

  @Override
  @SuppressWarnings({ "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    if (isOpen())
    {
      if (adapter == CDOView.class)
      {
        return view;
      }
    }

    return super.getAdapter(adapter);
  }

  @Override
  public String toString()
  {
    return getLabel();
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    repository = OM.getRepositoryManager().getElement(properties.getProperty(PROP_REPOSITORY));
    branchID = Integer.parseInt(properties.getProperty(PROP_BRANCH_ID));
    branchPath = properties.getProperty(PROP_BRANCH_PATH);
    branchPoints = properties.getProperty(PROP_BRANCH_POINTS);
    timeStamp = Long.parseLong(properties.getProperty(PROP_TIME_STAMP));
    readOnly = Boolean.parseBoolean(properties.getProperty(PROP_READ_ONLY));
    rootID = CDOIDUtil.read(properties.getProperty(PROP_ROOT_ID));

    ((CDORepositoryImpl)repository).addCheckout(this);
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_REPOSITORY, repository.getID());
    properties.setProperty(PROP_BRANCH_ID, Integer.toString(branchID));
    properties.setProperty(PROP_TIME_STAMP, Long.toString(timeStamp));
    properties.setProperty(PROP_READ_ONLY, Boolean.toString(readOnly));

    if (branchPath != null)
    {
      properties.setProperty(PROP_BRANCH_PATH, branchPath);
    }

    if (branchPoints != null)
    {
      properties.setProperty(PROP_BRANCH_POINTS, branchPoints);
    }

    String string = getCDOIDString(rootID);
    properties.setProperty(PROP_ROOT_ID, string);
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected EObject loadRootObject()
  {
    return view.getObject(rootID);
  }

  protected abstract CDOView openView(CDOSession session);

  protected void closeView()
  {
    view.close();
  }

  public static String getCDOIDString(CDOID id)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, id);
    return builder.toString();
  }
}
