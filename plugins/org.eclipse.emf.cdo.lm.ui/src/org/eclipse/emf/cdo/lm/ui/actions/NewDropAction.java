/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;
import org.eclipse.emf.cdo.lm.ui.widgets.TimeStampComposite;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 */
public class NewDropAction extends LMAction.NewElement<Stream>
{
  private static final long INVALID_DATE = CDOBranchPoint.INVALID_DATE;

  private final DropType dropType;

  private ISystemDescriptor systemDescriptor;

  private long startTimeStamp;

  private long timeStamp;

  private String timeStampError;

  private Text versionText;

  private Job versionExtractionJob;

  private AtomicLong versionExtractionTimeStamp = new AtomicLong(INVALID_DATE);

  private String labelString;

  private Text labelText;

  public NewDropAction(IWorkbenchPage page, StructuredViewer viewer, Stream stream, DropType dropType)
  {
    super(page, viewer, //
        "New " + dropType.getName() + INTERACTIVE, //
        "Add a new " + dropType.getName().toLowerCase() + " to stream '" + stream.getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(LMEditPlugin.INSTANCE.getImage(dropType.isRelease() ? "full/obj16/Release" : "full/obj16/Drop")), //
        "Add a new " + dropType.getName().toLowerCase() + " to stream '" + stream.getName() + "'.", //
        dropType.isRelease() ? "icons/NewRelease.png" : "icons/NewDrop.png", //
        stream);
    this.dropType = dropType;
  }

  @Override
  protected void preRun() throws Exception
  {
    Stream stream = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(stream.getSystem());

    startTimeStamp = stream.getStartTimeStamp();
    if (dropType.isRelease())
    {
      Drop lastRelease = stream.getLastRelease();
      if (lastRelease != null)
      {
        startTimeStamp = lastRelease.getBranchPoint().getTimeStamp() + 1;
      }
    }

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    Stream stream = getContext();

    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Time stamp:");

      TimeStampComposite timeStampComposite = new TimeStampComposite(parent, SWT.NONE, startTimeStamp, CDOBranchPoint.UNSPECIFIED_DATE, stream);
      timeStampComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      timeStampComposite.addModifyListener((control, timeStamp, error) -> {
        NewDropAction.this.timeStamp = timeStamp;
        timeStampError = error;
        validateDialog();
        scheduleVersionExtraction(stream);
      });
      timeStampComposite.setTimeStamp(java.lang.System.currentTimeMillis());
    }

    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Version:");

      versionText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      versionText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
    }

    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Label:");

      labelText = new Text(parent, SWT.BORDER | SWT.SINGLE);
      labelText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      labelText.addModifyListener(e -> {
        labelString = labelText.getText();
        validateDialog();
      });
      labelText.setFocus();
      labelText.selectAll();
    }
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (timeStamp == INVALID_DATE)
    {
      return timeStampError == null ? "A time stamp must be entered" : timeStampError;
    }

    if (labelString == null || labelString.isEmpty())
    {
      return "A label must be entered.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(Stream stream, IProgressMonitor monitor) throws Exception
  {
    return systemDescriptor.createDrop(stream, dropType, timeStamp, labelString, monitor);
  }

  private void scheduleVersionExtraction(FloatingBaseline baseline)
  {
    synchronized (versionExtractionTimeStamp)
    {
      versionExtractionTimeStamp.set(timeStamp);

      if (timeStamp == INVALID_DATE)
      {
        if (versionExtractionJob != null)
        {
          versionExtractionJob.cancel();
          versionExtractionJob = null;
        }

        setVersionString("", false);
      }
      else
      {
        if (versionExtractionJob == null)
        {
          versionExtractionJob = new Job("Extract Version")
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              while (!monitor.isCanceled())
              {
                long timeStampToUse;
                synchronized (versionExtractionTimeStamp)
                {
                  timeStampToUse = versionExtractionTimeStamp.getAndSet(INVALID_DATE);
                }

                if (timeStampToUse == INVALID_DATE)
                {
                  break;
                }

                extractVersion(timeStampToUse);
              }

              return Status.OK_STATUS;
            }

            private void extractVersion(long timeStamp)
            {
              ModuleDefinition moduleDefinition = //
                  systemDescriptor.extractModuleDefinition(baseline, timeStamp);

              Version moduleVersion = moduleDefinition.getVersion();
              int major = (int)moduleVersion.getSegment(0);
              int minor = (int)moduleVersion.getSegment(1);
              int micro = (int)moduleVersion.getSegment(2);

              setVersionString(major + "." + minor + "." + micro, true);
            }
          };

          versionExtractionJob.schedule();
        }
      }
    }
  }

  private void setVersionString(String versionString, boolean fromJob)
  {
    UIUtil.asyncExec(getDisplay(), () -> {
      if (!versionText.isDisposed())
      {
        if (fromJob)
        {
          synchronized (versionExtractionTimeStamp)
          {
            if (versionExtractionJob == null)
            {
              return;
            }
          }
        }

        versionText.setText(versionString);

        labelText.setText(versionString);
        labelText.selectAll();
      }
    });
  }
}
