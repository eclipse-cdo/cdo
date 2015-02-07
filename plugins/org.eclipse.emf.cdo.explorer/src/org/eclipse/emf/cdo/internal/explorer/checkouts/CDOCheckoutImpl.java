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
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class CDOCheckoutImpl extends AbstractElement implements CDOCheckout
{
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

  private String branchPath;

  private long timeStamp;

  private boolean readOnly;

  private CDOID rootID;

  private State state = State.Closed;

  private CDOView view;

  private EObject rootObject;

  public CDOCheckoutImpl()
  {
  }

  public final CDORepository getRepository()
  {
    return repository;
  }

  public final String getBranchPath()
  {
    return branchPath;
  }

  public final void setBranchPath(String branchPath)
  {
    this.branchPath = branchPath;
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
        manager.fireCheckoutOpenEvent(this, true);
      }
    }
  }

  public final synchronized void close()
  {
    boolean closed = false;

    synchronized (viewListener)
    {
      if (isOpen())
      {
        try
        {
          state = State.Closing;

          rootObject.eAdapters().remove(this);
          view.close();

          ((CDORepositoryImpl)repository).closeCheckout(this);
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
        manager.fireCheckoutOpenEvent(this, false);
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
    repository = OM.getRepositoryManager().getElement(properties.getProperty("repository"));
    branchPath = properties.getProperty("branchPath");
    timeStamp = Long.parseLong(properties.getProperty("timeStamp"));
    readOnly = Boolean.parseBoolean(properties.getProperty("readOnly"));
    rootID = CDOIDUtil.read(properties.getProperty("rootID"));

    ((CDORepositoryImpl)repository).addCheckout(this);
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.put("repository", repository.getID());
    properties.put("branchPath", branchPath);
    properties.put("timeStamp", Long.toString(timeStamp));
    properties.put("readOnly", Boolean.toString(readOnly));

    String string = getCDOIDString(rootID);
    properties.put("rootID", string);
  }

  protected EObject loadRootObject()
  {
    return view.getObject(rootID);
  }

  protected abstract CDOView openView(CDOSession session);

  public static String getCDOIDString(CDOID id)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, id);
    return builder.toString();
  }

  private static CDOCheckoutManagerImpl getManager()
  {
    return OM.getCheckoutManager();
  }
}
