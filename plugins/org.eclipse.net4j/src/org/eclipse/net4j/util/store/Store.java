/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.store;

import org.eclipse.net4j.util.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleImpl;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class Store<CONTENT> extends LifecycleImpl
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG, Store.class);

  private CONTENT content;

  private boolean dirty;

  public Store()
  {
  }

  public CONTENT getContent()
  {
    return content;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void setDirty()
  {
    dirty = true;
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    loadContent();
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    if (dirty)
    {
      saveContent();
      dirty = false;
    }

    super.onDeactivate();
  }

  private void loadContent() throws Exception
  {
    ObjectInputStream ois = null;
    if (TRACER.isEnabled())
    {
      TRACER.trace("Loading content");
    }

    try
    {
      ois = new StoreInputStream(getInputStream());
      content = (CONTENT)ois.readObject();
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Initializing content");
      }

      content = getInitialContent();

      if (!(ex instanceof FileNotFoundException))
      {
        throw ex;
      }
    }
    finally
    {
      IOUtil.closeSilent(ois);
    }
  }

  private void saveContent()
  {
    ObjectOutputStream oos = null;
    if (TRACER.isEnabled())
    {
      TRACER.trace("Saving content");
    }

    try
    {
      oos = new StoreOutputStream(getOutputStream());
      oos.writeObject(content);
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
    }
    finally
    {
      IOUtil.closeSilent(oos);
    }
  }

  protected String formatForTracing(Object obj)
  {
    if (obj instanceof String)
    {
      return (String)obj;
    }

    return null;
  }

  protected abstract CONTENT getInitialContent();

  protected abstract InputStream getInputStream() throws IOException;

  protected abstract OutputStream getOutputStream() throws IOException;

  /**
   * @author Eike Stepper
   */
  private final class StoreInputStream extends ObjectInputStream
  {
    private StoreInputStream(InputStream in) throws IOException
    {
      super(in);
      enableResolveObject(true);
    }

    @Override
    protected Object resolveObject(Object obj) throws IOException
    {
      if (TRACER.isEnabled() && obj instanceof String)
      {
        String str = formatForTracing(obj);
        if (str != null)
        {
          TRACER.trace("Loaded " + obj);
        }
      }

      return super.resolveObject(obj);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StoreOutputStream extends ObjectOutputStream
  {
    private StoreOutputStream(OutputStream out) throws IOException
    {
      super(out);
      enableReplaceObject(true);
    }

    @Override
    protected Object replaceObject(Object obj) throws IOException
    {
      if (TRACER.isEnabled())
      {
        String str = formatForTracing(obj);
        if (str != null)
        {
          TRACER.trace("Saved " + obj);
        }
      }

      return super.replaceObject(obj);
    }
  }
}