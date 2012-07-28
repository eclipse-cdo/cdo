/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.ui;

import org.eclipse.emf.cdo.releng.internal.version.Markers;
import org.eclipse.emf.cdo.releng.internal.version.VersionBuilderArguments;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class QuickFixer implements IMarkerResolutionGenerator2
{
  public QuickFixer()
  {
  }

  public IMarkerResolution[] getResolutions(IMarker marker)
  {
    List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

    String regEx = Markers.getQuickFixPattern(marker);
    if (regEx != null)
    {
      String replacement = Markers.getQuickFixReplacement(marker);
      resolutions.add(new ReplaceResolution(marker, replacement));
    }

    String ignoreOption = Markers.getQuickFixConfigureOption(marker);
    if (ignoreOption != null)
    {
      resolutions.add(new ConfigureResolution(marker, ignoreOption));
    }

    return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
  }

  public boolean hasResolutions(IMarker marker)
  {
    if (Markers.getQuickFixPattern(marker) != null)
    {
      return true;
    }

    if (Markers.getQuickFixConfigureOption(marker) != null)
    {
      return true;
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  private abstract class AbstractResolution extends WorkbenchMarkerResolution
  {
    private IMarker marker;

    private String label;

    private String imageKey;

    public AbstractResolution(IMarker marker, String label, String imageKey)
    {
      this.marker = marker;
      this.label = label;
      this.imageKey = imageKey;
    }

    public IMarker getMarker()
    {
      return marker;
    }

    public String getLabel()
    {
      return label;
    }

    public String getDescription()
    {
      return "";
    }

    public final Image getImage()
    {
      ImageRegistry imageRegistry = Activator.getPlugin().getImageRegistry();
      return imageRegistry.get(imageKey);
    }

    @Override
    public IMarker[] findOtherMarkers(IMarker[] markers)
    {
      List<IMarker> result = new ArrayList<IMarker>();
      for (IMarker marker : markers)
      {
        try
        {
          if (marker != this.marker && isApplicable(marker))
          {
            result.add(marker);
          }
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }

      return result.toArray(new IMarker[result.size()]);
    }

    protected abstract boolean isApplicable(IMarker marker) throws Exception;

    public final void run(IMarker marker)
    {
      try
      {
        apply(marker);
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    protected abstract void apply(IMarker marker) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  private abstract class AbstractDocumentResolution extends AbstractResolution
  {
    public AbstractDocumentResolution(IMarker marker, String label, String imageKey)
    {
      super(marker, label, imageKey);
    }

    @Override
    protected final void apply(IMarker marker) throws Exception
    {
      IPath fullPath = ((IFile)marker.getResource()).getFullPath();
      ITextFileBufferManager.DEFAULT.connect(fullPath, LocationKind.IFILE, new NullProgressMonitor());

      try
      {
        ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(fullPath, LocationKind.IFILE);
        boolean wasDirty = buffer.isDirty();

        IDocument document = buffer.getDocument();
        if (apply(marker, document))
        {
          if (!wasDirty && !buffer.isShared())
          {
            buffer.commit(new NullProgressMonitor(), true);
          }
        }
      }
      finally
      {
        ITextFileBufferManager.DEFAULT.disconnect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
      }
    }

    protected abstract boolean apply(IMarker marker, IDocument document) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  private class ReplaceResolution extends AbstractDocumentResolution
  {
    private String replacement;

    public ReplaceResolution(IMarker marker, String replacement)
    {
      super(marker, replacement == null ? "Remove the reference" : "Change the version",
          replacement == null ? Activator.CORRECTION_DELETE_GIF : Activator.CORRECTION_CHANGE_GIF);
      this.replacement = replacement == null ? "" : replacement;
    }

    @Override
    public String getDescription()
    {
      if (replacement.length() != 0)
      {
        return getLabel() + " to " + replacement;
      }

      return super.getDescription();
    }

    @Override
    protected boolean isApplicable(IMarker marker)
    {
      if (Markers.getQuickFixPattern(marker) == null)
      {
        return false;
      }

      boolean expectedReplacement = replacement.length() != 0;
      boolean actualReplacement = Markers.getQuickFixReplacement(marker) != null;
      return actualReplacement == expectedReplacement;
    }

    @Override
    protected boolean apply(IMarker marker, IDocument document) throws Exception
    {
      String content = document.get();

      String regEx = Markers.getQuickFixPattern(marker);
      String replacement = Markers.getQuickFixReplacement(marker);

      Pattern pattern = Pattern.compile(regEx, Pattern.MULTILINE | Pattern.DOTALL);
      Matcher matcher = pattern.matcher(content);
      if (matcher.find())
      {
        int start;
        int end;
        if (replacement != null && replacement.length() != 0)
        {
          start = matcher.start(1);
          end = matcher.end(1);
        }
        else
        {
          start = matcher.start();
          end = matcher.end();
          replacement = "";
        }

        document.replace(start, end - start, replacement);
        return true;
      }

      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class ConfigureResolution extends AbstractResolution
  {
    private String option;

    public ConfigureResolution(IMarker marker, String option)
    {
      super(marker, "Configure the project to ignore the problem", Activator.CORRECTION_CONFIGURE_GIF);
      this.option = option;
    }

    @Override
    public String getDescription()
    {
      IProject project = getMarker().getResource().getProject();
      return "Set " + option + " = true in '/" + project.getName() + "/.project'";
    }

    @Override
    protected boolean isApplicable(IMarker marker)
    {
      String requiredOption = Markers.getQuickFixConfigureOption(marker);
      return option.equals(requiredOption);
    }

    @Override
    protected void apply(IMarker marker) throws Exception
    {
      String option = Markers.getQuickFixConfigureOption(marker);

      IProject project = marker.getResource().getProject();
      VersionBuilderArguments arguments = new VersionBuilderArguments(project);
      arguments.put(option, "true");
      arguments.applyTo(project);
    }
  }
}
