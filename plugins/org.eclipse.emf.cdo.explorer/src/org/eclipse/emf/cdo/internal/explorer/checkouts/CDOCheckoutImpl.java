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
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
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

  public static final String PROP_REPOSITORY = "repository";

  public static final String EDITORS_FILE = "_editors";

  private final Map<CDOID, String> editorIDs = new WeakHashMap<CDOID, String>();

  private final IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == Kind.DEACTIVATED)
        {
          close();
        }
      }
    }
  };

  private CDORepository repository;

  private int branchID;

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
    this.branchID = branchID;
  }

  public final long getTimeStamp()
  {
    return timeStamp;
  }

  public final void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
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

  public final synchronized void open()
  {
    boolean opened = false;

    synchronized (viewListener)
    {
      if (!isOpen())
      {
        try
        {
          state = State.Opening;

          CDOSession session = ((CDORepositoryImpl)repository).openCheckout(this);

          view = openView(session);
          view.addListener(viewListener);

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

  public final synchronized void close()
  {
    boolean closed = false;
    CDOView oldView = null;

    synchronized (viewListener)
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

  public final CDOTransaction openTransaction()
  {
    CDOTransaction transaction = doOpenTransaction();
    if (transaction != null)
    {
      new CDOTransactionCommentator(transaction);
    }

    return transaction;
  }

  protected CDOTransaction doOpenTransaction()
  {
    if (view == null)
    {
      return null;
    }

    return view.getSession().openTransaction(view.getBranch());
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

      Properties properties = AbstractManager.loadProperties(getFolder(), EDITORS_FILE);
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

      Properties properties = AbstractManager.loadProperties(getFolder(), EDITORS_FILE);
      if (properties == null)
      {
        properties = new Properties();
      }

      String idString = getCDOIDString(objectID);
      properties.put(idString, editorID);

      saveProperties(EDITORS_FILE, properties);
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

      if (adapter == EObject.class)
      {
        return rootObject;
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
    timeStamp = Long.parseLong(properties.getProperty(PROP_TIME_STAMP));
    readOnly = Boolean.parseBoolean(properties.getProperty(PROP_READ_ONLY));
    rootID = CDOIDUtil.read(properties.getProperty(PROP_ROOT_ID));

    ((CDORepositoryImpl)repository).addCheckout(this);
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.put(PROP_REPOSITORY, repository.getID());
    properties.put(PROP_BRANCH_ID, Integer.toString(branchID));
    properties.put(PROP_TIME_STAMP, Long.toString(timeStamp));
    properties.put(PROP_READ_ONLY, Boolean.toString(readOnly));

    String string = getCDOIDString(rootID);
    properties.put(PROP_ROOT_ID, string);
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
