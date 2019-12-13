/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.examples.transfer;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.examples.bundle.OM;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.MonitorCanceledException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Need to change to this class in plugin.xml
 *
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @since 4.0
 */
public class UploadSSLClientAction implements IWorkbenchWindowActionDelegate, UploadProtocol
{
  private IWorkbenchWindow window;

  public UploadSSLClientAction()
  {
  }

  @Override
  public void init(IWorkbenchWindow window)
  {
    this.window = window;
  }

  @Override
  public void run(IAction action)
  {
    FileDialog fileDialog = new FileDialog(window.getShell());
    final String path = fileDialog.open();
    if (path != null)
    {
      final File file = new File(path);
      new Job("Uploading file") //$NON-NLS-1$
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          try
          {
            boolean replaced = transferFile(file, monitor);
            OM.LOG.info("File " + path + (replaced ? " replaced" : " stored") + " on the Net4j server."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            return Status.OK_STATUS;
          }
          catch (MonitorCanceledException ex)
          {
            return Status.CANCEL_STATUS;
          }
          catch (Exception ex)
          {
            return new Status(IStatus.ERROR, OM.BUNDLE_ID, "Problem with upload of " + path, ex); //$NON-NLS-1$
          }
          finally
          {
            monitor.done();
          }
        }
      }.schedule();
    }
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  @Override
  public void dispose()
  {
  }

  private boolean transferFile(final File file, IProgressMonitor monitor) throws Exception
  {
    SignalProtocol<Object> protocol = null;

    try
    {
      // Start a connector that represents the client side of a physical connection
      IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "ssl", "localhost:2036");

      // Open a virtual channel with the ECHO protocol, send an ECHO request and close the channel
      protocol = new SignalProtocol<Object>(PROTOCOL_NAME);
      protocol.open(connector);

      UploadRequest request = new UploadRequest(protocol, file);
      return request.send(EclipseMonitor.safe(monitor));
    }
    finally
    {
      protocol.close();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UploadRequest extends RequestWithMonitoring<Boolean>
  {
    private File file;

    public UploadRequest(SignalProtocol<?> protocol, File file)
    {
      super(protocol, UPLOAD_SIGNAL_ID);
      this.file = file;
    }

    @Override
    protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
    {
      long size = file.length();
      out.writeLong(size);
      out.writeString(file.getName());

      monitor.begin((int)size);
      BufferedInputStream in = null;

      try
      {
        in = new BufferedInputStream(new FileInputStream(file));
        while (size != 0L)
        {
          int chunk = BUFFER_SIZE;
          if (size < BUFFER_SIZE)
          {
            chunk = (int)size;
          }

          byte[] buffer = new byte[chunk];
          in.read(buffer);
          out.writeByteArray(buffer);

          monitor.worked(chunk);
          size -= chunk;
        }
      }
      finally
      {
        monitor.done();
        in.close();
      }
    }

    @Override
    protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
    {
      return in.readBoolean();
    }
  }
}
