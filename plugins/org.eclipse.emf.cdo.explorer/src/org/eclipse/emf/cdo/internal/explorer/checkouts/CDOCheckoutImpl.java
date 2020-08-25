/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
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
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

  public static final String CHECKOUT_KEY = CDOCheckout.class.getName();

  private static final CDOBranchPoint[] NO_BRANCH_POINTS = {};

  private static final int BRANCH_POINTS_MAX = 10;

  private static final String BRANCH_POINT_SEPARATOR = ",";

  private static final String BRANCH_AND_POINT_SEPARATOR = "_";

  private final Set<CDOView> views = new HashSet<>();

  private final Map<CDOID, String> editorIDs = new WeakHashMap<>();

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

  private URI uri;

  public CDOCheckoutImpl()
  {
  }

  public CDOCheckoutImpl(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  @Override
  public final CDOCheckoutManagerImpl getManager()
  {
    return OM.getCheckoutManager();
  }

  @Override
  public final CDORepository getRepository()
  {
    return repository;
  }

  @Override
  public final int getBranchID()
  {
    return branchID;
  }

  @Override
  public final void setBranchID(int branchID)
  {
    setBranchPoint(branchID, timeStamp);
  }

  @Override
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

  @Override
  public final CDOBranchPoint getBranchPoint()
  {
    return view;
  }

  @Override
  public final void setBranchPoint(CDOBranchPoint branchPoint)
  {
    int branchID = branchPoint.getBranch().getID();
    long timeStamp = branchPoint.getTimeStamp();
    setBranchPoint(branchID, timeStamp);
  }

  @Override
  public final void setBranchPoint(int branchID, long timeStamp)
  {
    if (this.branchID != branchID || this.timeStamp != timeStamp)
    {
      addBranchPoint(this.branchID, this.timeStamp);

      this.branchID = branchID;
      this.timeStamp = timeStamp;

      if (isOpen())
      {
        branchPath = doSetBranchPoint(branchID, timeStamp);

        for (CDOView view : getViews())
        {
          if (view != this.view)
          {
            view.setBranchPoint(this.view);
          }
        }
      }
      else
      {
        branchPath = null;
      }

      save();
    }
  }

  @Override
  public final CDOBranchPoint getBranchPoint(CDOCheckout fromCheckout)
  {
    if (repository == fromCheckout.getRepository() && repository.isConnected())
    {
      CDOBranchManager branchManager = repository.getSession().getBranchManager();
      CDOBranch branch = branchManager.getBranch(fromCheckout.getBranchID());
      return branch.getPoint(fromCheckout.getTimeStamp());
    }

    return null;
  }

  protected String doSetBranchPoint(int branchID, long timeStamp)
  {
    CDOBranch branch = view.getSession().getBranchManager().getBranch(branchID);
    view.setBranchPoint(branch.getPoint(timeStamp));
    return branch.getPathName();
  }

  @Override
  public CDOBranchPoint[] getBranchPoints()
  {
    if (branchPoints != null && isOpen())
    {
      List<CDOBranchPoint> result = new ArrayList<>();
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

  @Override
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

  @Override
  public final long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public final void setTimeStamp(long timeStamp)
  {
    setBranchPoint(branchID, timeStamp);
  }

  @Override
  public final boolean isReadOnly()
  {
    return readOnly;
  }

  @Override
  public void setReadOnly(boolean readOnly)
  {
    if (state != State.Closed)
    {
      throw new IllegalStateException("Checkout is not closed: " + this);
    }

    if (this.readOnly != readOnly)
    {
      this.readOnly = readOnly;
      save();
    }
  }

  @Override
  public final CDOID getRootID()
  {
    return rootID;
  }

  @Override
  public final void setRootID(CDOID rootID)
  {
    this.rootID = rootID;
  }

  @Override
  public CDOCheckout duplicate()
  {
    Properties properties = new Properties();
    collectDuplicationProperties(properties);

    CDOCheckout copy = CDOExplorerUtil.getCheckoutManager().addCheckout(properties);
    if (isOpen())
    {
      copy.open();
    }

    return copy;
  }

  protected void collectDuplicationProperties(Properties properties)
  {
    properties.setProperty(PROP_TYPE, getType());
    properties.setProperty(PROP_LABEL, getManager().getUniqueLabel(getLabel()));
    properties.setProperty(PROP_REPOSITORY, getRepository().getID());
    properties.setProperty(PROP_BRANCH_ID, Integer.toString(getBranchID()));
    properties.setProperty(PROP_TIME_STAMP, Long.toString(getTimeStamp()));
    properties.setProperty(PROP_READ_ONLY, Boolean.toString(isReadOnly()));
    properties.setProperty(PROP_ROOT_ID, CDOExplorerUtil.getCDOIDString(getRootID()));
  }

  @Override
  public final State getState()
  {
    return state;
  }

  @Override
  public final boolean isOpen()
  {
    return view != null;
  }

  @Override
  public final void open()
  {
    CDOCheckoutManagerImpl manager = getManager();
    State oldState = null;
    State newState = null;

    try
    {
      synchronized (this)
      {
        oldState = state;

        if (!isOpen())
        {
          try
          {
            state = State.Opening;
            if (manager != null)
            {
              manager.fireCheckoutOpenEvent(this, null, oldState, state);
            }

            oldState = state;

            prepareOpen();
            CDOSession session = ((CDORepositoryImpl)repository).openCheckout(this);

            view = openView(session);
            view.addListener(new LifecycleEventAdapter()
            {
              @Override
              protected void onDeactivated(ILifecycle lifecycle)
              {
                removeView(view);
                if (state != State.Closing)
                {
                  close();
                }
              }
            });

            configureView(view);

            rootObject = loadRootObject();
            rootObject.eAdapters().add(this);

            state = State.Open;
            newState = state;
          }
          catch (RuntimeException ex)
          {
            view = null;
            rootObject = null;
            state = State.Closed;
            newState = state;
            throw ex;
          }
          catch (Error ex)
          {
            view = null;
            rootObject = null;
            state = State.Closed;
            newState = state;
            throw ex;
          }
        }
      }
    }
    finally
    {
      if (manager != null && oldState != null && newState != null && newState != oldState)
      {
        manager.fireCheckoutOpenEvent(this, view, oldState, newState);
      }
    }
  }

  protected void prepareOpen()
  {
    // Do nothing.
  }

  @Override
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

            CDOView[] remainingViews = getViews();
            for (CDOView remainingView : remainingViews)
            {
              remainingView.close();
            }
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
        manager.fireCheckoutOpenEvent(this, oldView, State.Open, State.Closed);
      }
    }
  }

  private void addView(CDOView view)
  {
    synchronized (this)
    {
      views.add(view);
    }
  }

  private void removeView(CDOView view)
  {
    synchronized (this)
    {
      views.remove(view);
    }
  }

  @Override
  public CDOView[] getViews()
  {
    synchronized (this)
    {
      return views.toArray(new CDOView[views.size()]);
    }
  }

  @Override
  public final CDOView getView()
  {
    return view;
  }

  @Override
  public final URI getURI()
  {
    return uri;
  }

  @Override
  public final EObject getRootObject()
  {
    return rootObject;
  }

  @Override
  public final ObjectType getRootType()
  {
    return ObjectType.valueFor(rootObject);
  }

  protected ResourceSetImpl createResourceSet()
  {
    return new ResourceSetImpl();
  }

  @Override
  public final CDOView openView()
  {
    return openView(createResourceSet());
  }

  @Override
  public CDOView openView(ResourceSet resourceSet)
  {
    return openView(readOnly, resourceSet);
  }

  @Override
  public final CDOView openView(boolean readOnly)
  {
    return openView(readOnly, createResourceSet());
  }

  @Override
  public CDOView openView(boolean readOnly, ResourceSet resourceSet)
  {
    return openAndConfigureView(readOnly, resourceSet);
  }

  @Override
  public final CDOTransaction openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  @Override
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
    view = configureView(view);

    EObject object = view.getObject(rootObject);
    object.eAdapters().add(this);

    return view;
  }

  protected CDOView doOpenView(boolean readOnly, ResourceSet resourceSet)
  {
    if (view == null)
    {
      return null;
    }

    CDOSession session = view.getSession();

    if (readOnly)
    {
      return session.openView(view, resourceSet);
    }

    CDOBranch branch = view.getBranch();
    CDOBranchPoint head = branch.getHead();

    return session.openTransaction(head, resourceSet);
  }

  protected CDOView configureView(final CDOView view)
  {
    CDOUtil.configureView(view);
    ((InternalCDOView)view).setRepositoryName(repository.getLabel());

    view.properties().put(CDOView.PROP_TIME_MACHINE_DISABLED, !isReadOnly());
    view.properties().put(CHECKOUT_KEY, this);

    view.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewLocksChangedEvent)
        {
          CDOViewLocksChangedEvent e = (CDOViewLocksChangedEvent)event;
          EObject[] objects = e.getAffectedObjects();
          if (objects.length != 0)
          {
            CDOCheckoutManagerImpl manager = getManager();
            if (manager != null)
            {
              manager.fireElementsChangedEvent(objects);
            }
          }
        }
        else if (event instanceof CDOViewTargetChangedEvent)
        {
          CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
          setBranchPoint(e.getBranchPoint());
        }
        else if (event instanceof ILifecycleEvent)
        {
          ILifecycleEvent e = (ILifecycleEvent)event;
          if (e.getKind() == Kind.DEACTIVATED)
          {
            removeView(view);
          }
        }
      }
    });

    URI from = URI.createURI("cdo://" + view.getSession().getRepositoryInfo().getUUID() + "/");
    URI to = uri.appendSegment("");
    view.getResourceSet().getURIConverter().getURIMap().put(from, to);

    addView(view);
    return view;
  }

  @Override
  public URI createResourceURI(String path)
  {
    String authority = getID();

    if (StringUtil.isEmpty(path))
    {
      return URI.createHierarchicalURI(CDOCheckoutViewProvider.SCHEME, authority, null, null, null, null);
    }

    String[] segments = new Path(path).segments();
    return URI.createHierarchicalURI(CDOCheckoutViewProvider.SCHEME, authority, null, segments, null, null);
  }

  @Override
  public String getEditorOpenerID(CDOID objectID)
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
        String idString = CDOExplorerUtil.getCDOIDString(objectID);
        return properties.getProperty(idString);
      }

      return null;
    }
  }

  @Override
  public void setEditorOpenerID(CDOID objectID, String editorID)
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

      String idString = CDOExplorerUtil.getCDOIDString(objectID);
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

    String repositoryID = properties.getProperty(PROP_REPOSITORY);
    repository = OM.getRepositoryManager().getElement(repositoryID);
    if (repository == null)
    {
      throw new IllegalStateException("Repository " + repositoryID + " not found: " + this);
    }

    branchID = Integer.parseInt(properties.getProperty(PROP_BRANCH_ID));
    branchPath = properties.getProperty(PROP_BRANCH_PATH);
    branchPoints = properties.getProperty(PROP_BRANCH_POINTS);
    timeStamp = Long.parseLong(properties.getProperty(PROP_TIME_STAMP));
    readOnly = isOnline() ? Boolean.parseBoolean(properties.getProperty(PROP_READ_ONLY)) : false;

    String property = properties.getProperty(PROP_ROOT_ID);
    if (property != null)
    {
      rootID = CDOIDUtil.read(property);
    }

    uri = createResourceURI(null);
    ((CDORepositoryImpl)repository).addCheckout(this);
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_REPOSITORY, repository.getID());
    properties.setProperty(PROP_BRANCH_ID, Integer.toString(branchID));
    properties.setProperty(PROP_TIME_STAMP, Long.toString(timeStamp));
    properties.setProperty(PROP_READ_ONLY, Boolean.toString(isOnline() ? readOnly : false));

    if (branchPath != null)
    {
      properties.setProperty(PROP_BRANCH_PATH, branchPath);
    }

    if (branchPoints != null)
    {
      properties.setProperty(PROP_BRANCH_POINTS, branchPoints);
    }

    if (!CDOIDUtil.isNull(rootID))
    {
      String string = CDOExplorerUtil.getCDOIDString(rootID);
      properties.setProperty(PROP_ROOT_ID, string);
    }
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected EObject loadRootObject()
  {
    if (CDOIDUtil.isNull(rootID))
    {
      rootID = view.getSession().getRepositoryInfo().getRootResourceID();
    }

    InternalCDOObject cdoObject = (InternalCDOObject)view.getObject(rootID);
    return cdoObject.cdoInternalInstance();
  }

  protected abstract CDOView openView(CDOSession session);

  protected void closeView()
  {
    view.close();
  }
}
