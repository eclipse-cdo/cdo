/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */

/**
 * The Net4j transport layer. 
 * The five main interfaces of the transport layer are:
 * 	<ul>
 * 		<li>{@link org.eclipse.net4j.buffer.IBuffer}</li> 
 * 		<li>{@link org.eclipse.net4j.channel.IChannel}</li>
 * 		<li>{@link org.eclipse.net4j.acceptor.IAcceptor}</li>
 * 		<li>{@link org.eclipse.net4j.connector.IConnector}</li> 
 * 		<li>{@link org.eclipse.net4j.protocol.IProtocol}</li>
 * 	</ul>
 * <p>
 * 
 * <dt><b>Sequence Diagram: Communication Process</b></dt>
 * <dd> <img src="doc-files/CommunicationProcess.jpg" title="Communication Process" border="0"
 * usemap="#CommunicationProcess.jpg"/></dd>
 * <p>
 * <MAP NAME="CommunicationProcess.jpg">
 * <AREA SHAPE="RECT" COORDS="128,94,247,123" HREF="IConnector.html">
 * <AREA SHAPE="RECT" COORDS="648,95,767,123" HREF="IConnector.html">
 * <AREA SHAPE="RECT" COORDS="509,254,608,283" HREF="IChannel.html">
 * <AREA SHAPE="RECT" COORDS="287,355,387,383" HREF="IChannel.html">
 * <AREA SHAPE="RECT" COORDS="818,195,897,222" HREF="IProtocol.html">
 * </MAP>
 */
package org.eclipse.net4j;
