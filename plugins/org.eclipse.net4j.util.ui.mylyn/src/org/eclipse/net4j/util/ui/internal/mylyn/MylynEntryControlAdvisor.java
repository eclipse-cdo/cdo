/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.internal.mylyn;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.StringBuilderWriter;
import org.eclipse.net4j.util.ui.widgets.EntryControlAdvisor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupProjectionViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupSourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.EditorsUI;

import java.util.Objects;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class MylynEntryControlAdvisor extends EntryControlAdvisor
{
  private static final String DOCUMENT_KEY = "net4j.document";

  private final MarkupLanguage markupLanguage;

  public MylynEntryControlAdvisor(String description)
  {
    String name = Objects.requireNonNullElse(description, "Markdown");
    markupLanguage = WikiText.getMarkupLanguage(name);
  }

  @Override
  protected Control doCreateControl(Composite parent, ControlConfig config)
  {
    IDocument document = new Document();

    FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
    partitioner.setMarkupLanguage(markupLanguage == null ? null : markupLanguage.clone());
    partitioner.connect(document);
    document.setDocumentPartitioner(partitioner);

    MarkupProjectionViewer viewer = new MarkupProjectionViewer(parent, null, null, false, SWT.V_SCROLL | SWT.WRAP);
    viewer.configure(new MarkupSourceViewerConfiguration(EditorsUI.getPreferenceStore()));

    StyledText text = viewer.getTextWidget();
    text.setData(MarkupLanguage.class.getName(), markupLanguage);
    text.setData(ISourceViewer.class.getName(), viewer);
    text.addVerifyKeyListener(e -> processKeyEvent(text, config, e));

    document.addDocumentListener(new IDocumentListener()
    {
      @Override
      public void documentAboutToBeChanged(DocumentEvent event)
      {
        // Do nothing.
      }

      @Override
      public void documentChanged(DocumentEvent event)
      {
        processModifyEvent(text, config);
      }
    });

    viewer.setDocument(document);
    text.setData(DOCUMENT_KEY, document);
    return text;
  }

  @Override
  public String getEntry(Control control)
  {
    IDocument document = getDocument(control);
    return document.get();
  }

  @Override
  public void setEntry(Control control, String entry)
  {
    IDocument document = getDocument(control);
    document.set(entry);

    ((StyledText)control).setSelection(entry.length());
  }

  @Override
  public void renderHTML(Control control, StringBuilder html)
  {
    String entry = getEntry(control);

    StringBuilderWriter out = new StringBuilderWriter(html);
    HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out, false);
    builder.setEmitAsDocument(false);

    MarkupParser parser = new MarkupParser(markupLanguage, builder);
    parser.parse(entry);
  }

  private static IDocument getDocument(Control control)
  {
    return (IDocument)control.getData(DOCUMENT_KEY);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends EntryControlAdvisor.Factory
  {
    public static final String TYPE = "mylyn";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public EntryControlAdvisor create(String description) throws ProductCreationException
    {
      return new MylynEntryControlAdvisor(description);
    }
  }
}
