/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.spi.cdo.FSMUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CDOLobStorage extends AbstractDocumentProvider
{
  private static final String CHARSET_UTF_8 = "UTF-8"; //$NON-NLS-1$

  private static CDOLobStorage instance;

  public CDOLobStorage()
  {
    instance = this;
  }

  /**
   * Avoids initialization of the "instances" field.
   */
  private CDOLobStorage(boolean dummy)
  {
  }

  @Override
  protected IDocument createDocument(Object element) throws CoreException
  {
    if (element instanceof CDOLobEditorInput)
    {
      CDOResourceLeaf resource = ((CDOLobEditorInput)element).getResource();
      String contents = getContents(resource);

      IDocument document = new Document();
      document.set(contents);
      return document;
    }

    return null;
  }

  @Override
  protected IAnnotationModel createAnnotationModel(Object element) throws CoreException
  {
    return null;
  }

  @Override
  protected ElementInfo createElementInfo(Object element) throws CoreException
  {
    if (element instanceof CDOLobEditorInput)
    {
      CDOResourceLeaf resource = ((CDOLobEditorInput)element).getResource();

      CDOView view = resource.cdoView();
      if (view != null && view.isReadOnly())
      {
        return new CDOElementInfo(createDocument(element), createAnnotationModel(element), view);
      }
    }

    return new ElementInfo(createDocument(element), createAnnotationModel(element));
  }

  @Override
  protected void disposeElementInfo(Object element, ElementInfo info)
  {
    if (element instanceof CDOElementInfo)
    {
      ((CDOElementInfo)element).dispose();
    }

    super.disposeElementInfo(element, info);
  }

  @Override
  public boolean isReadOnly(Object element)
  {
    if (element instanceof CDOLobEditorInput)
    {
      CDOResourceLeaf resource = ((CDOLobEditorInput)element).getResource();
      return resource.cdoView().isReadOnly();
    }

    return super.isReadOnly(element);
  }

  @Override
  public boolean isModifiable(Object element)
  {
    return !isReadOnly(element);
  }

  @Override
  public boolean canSaveDocument(Object element)
  {
    return super.canSaveDocument(element);
  }

  @Override
  protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException
  {
    if (element instanceof CDOLobEditorInput)
    {
      CDOLobEditorInput editorInput = (CDOLobEditorInput)element;

      CDOResourceLeaf resource = editorInput.getResource();
      String contents = document.get();

      try
      {
        if (resource instanceof CDOTextResource)
        {
          CDOTextResource textResource = (CDOTextResource)resource;
          CDOClob clob = new CDOClob(new CharArrayReader(contents.toCharArray()));
          textResource.setContents(clob);
        }
        else if (resource instanceof CDOBinaryResource)
        {
          byte[] bytes = contents.getBytes(getDefaultEncoding());
          CDOBlob blob = new CDOBlob(new ByteArrayInputStream(bytes));
          ((CDOBinaryResource)resource).setContents(blob);
        }

        if (editorInput.isCommitOnSave())
        {
          CDOView view = resource.cdoView();
          if (view instanceof CDOTransaction)
          {
            CDOTransaction transaction = (CDOTransaction)view;
            transaction.commit();
          }
        }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  @Override
  protected IRunnableContext getOperationRunner(IProgressMonitor monitor)
  {
    return null;
  }

  private String getContents(CDOResourceLeaf resource)
  {
    if (resource == null)
    {
      return StringUtil.EMPTY;
    }

    if (FSMUtil.isInvalid(resource))
    {
      try
      {
        CDOResourceLeaf resource2 = (CDOResourceLeaf)resource.cdoView().getObject(resource.cdoID());
        if (resource2 == resource)
        {
          return StringUtil.EMPTY;
        }

        return getContents(resource2);
      }
      catch (Exception ex)
      {
        return StringUtil.EMPTY;
      }
    }

    try
    {
      if (resource instanceof CDOTextResource)
      {
        CDOTextResource textResource = (CDOTextResource)resource;
        CDOClob clob = textResource.getContents();
        if (clob == null)
        {
          return StringUtil.EMPTY;
        }

        return clob.getString();
      }

      if (resource instanceof CDOBinaryResource)
      {
        CDOBlob blob = ((CDOBinaryResource)resource).getContents();
        if (blob == null)
        {
          return StringUtil.EMPTY;
        }

        return new String(blob.getBytes(), getDefaultEncoding());
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ((CDOResource)resource).save(outputStream, null);
      return new String(outputStream.toByteArray(), getDefaultEncoding());
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  private String getDefaultEncoding()
  {
    String encoding = CHARSET_UTF_8;

    try
    {
      encoding = org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
    }
    catch (CoreException ex)
    {
      // Ignore
    }

    return encoding;
  }

  public static IDocumentProvider getInstance()
  {
    if (instance != null)
    {
      return instance;
    }

    return new CDOLobStorage(true);
  }

  /**
   * @author Eike Stepper
   */
  private final class CDOElementInfo extends ElementInfo implements IListener
  {
    private final CDOView view;

    public CDOElementInfo(IDocument document, IAnnotationModel model, CDOView view)
    {
      super(document, model);
      this.view = view;
      EventUtil.addListener(view, this);
    }

    public void dispose()
    {
      EventUtil.removeListener(view, this);
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewInvalidationEvent)
      {
        UIUtil.asyncExec(() -> {
          try
          {
            doResetDocument(fElement, null);
          }
          catch (CoreException ex)
          {
            OM.LOG.error(ex);
          }
        });
      }
    }
  }
}
