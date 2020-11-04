/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

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
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

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

        Reader reader = null;

        try
        {
          reader = clob.getContents();

          CharArrayWriter writer = new CharArrayWriter();
          IOUtil.copyCharacter(reader, writer);
          return writer.toString();
        }
        finally
        {
          IOUtil.close(reader);
        }

      }

      if (resource instanceof CDOBinaryResource)
      {
        CDOBlob blob = ((CDOBinaryResource)resource).getContents();
        if (blob == null)
        {
          return StringUtil.EMPTY;
        }

        InputStream inputStream = null;

        try
        {
          inputStream = blob.getContents();

          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          IOUtil.copy(inputStream, outputStream);
          return new String(outputStream.toByteArray(), getDefaultEncoding());
        }
        finally
        {
          IOUtil.close(inputStream);
        }

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
}
