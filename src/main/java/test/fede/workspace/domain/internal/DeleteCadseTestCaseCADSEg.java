/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package test.fede.workspace.domain.internal;

import junit.framework.Assert;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.graphictests.cadse.gtcadseworkbench_part.GTCadseShell;
import fr.imag.adele.graphictests.cadse.gtcadseworkbench_part.GTCadseView;
import fr.imag.adele.graphictests.cadse.test.GTCadseRTConstants;
import fr.imag.adele.graphictests.gttree.GTTreePath;
import fr.imag.adele.graphictests.test.GTTestCase;

public class DeleteCadseTestCaseCADSEg extends GTTestCase {
	GTCadseShell				shell;

	private static String	packageName;

	private static String	cadseName;

	static GeneratorName	generator	= new GeneratorName();


	@Test
	public void test_createAndDeleteCadse() throws Exception {
		/* =============== */
		/* Initializations */
		/* =============== */
		Bundle b = Platform.getBundle("fr.imag.adele.cadse.si.workspace.view");
		b.start();
		assertEquals(b.getState(), Bundle.ACTIVE);
		
		// a few parameters...
		// TestUtil.setVelocity(100);
		SWTBotPreferences.TIMEOUT = 10000;
		
		//set org.eclipse.swtbot.screenshots.dir
		//if (System.getProperty("test.screenshotPath") != null)
		//	GTScreenshot.setScreenshotPath(System.getProperty("test.screenshotPath"));

		/* ================================== */
		/* CADSEs selection in startup window */
		/* ================================== */

		shell =  new GTCadseShell(GTCadseRTConstants.CADSE_SELECTOR_SHELL_TITLE);
		shell.selectCadses(GTCadseRTConstants.CADSEG_MODEL);
		shell.capture();
		shell.close();
		GTCadseView.welcomeView.close();
		
		/* =================== */
		/* data-model creation */
		/* =================== */

		// CADSE WebAppModel
		GTCadseView.workspaceView.contextMenuNew(CadseGCST.CADSE_DEFINITION).click();
		shell = new GTCadseShell(CadseGCST.CADSE_DEFINITION);
		cadseName = "Cadse_" + generator.newName();
		shell.findCadseField(CadseGCST.ITEM_at_NAME_).typeText( cadseName);

		packageName = "model.webapp";
		shell.findCadseField(CadseGCST.CADSE_DEFINITION_at_PACKAGENAME_).typeText( packageName);
		shell.capture();
		shell.close();
		GTCadseView.workspaceView.show();
		GTCadseView.workspaceView.capture();
		GTCadseView.workspaceView.selectNode(cadseName);

		//captureView(CadseRTConstants.WORKSPACE_VIEW);
		//showView(CadseRTConstants.PACKAGE_EXPLORER_VIEW);
		//selectTreeInView(CadseRTConstants.PACKAGE_EXPLORER_VIEW, true, "Model.Workspace." + cadseName);
		//selectTreeInView(CadseRTConstants.PACKAGE_EXPLORER_VIEW, true, "Model.Workspace." + cadseName, "sources");
		//captureView(CadseRTConstants.PACKAGE_EXPLORER_VIEW);
		//showView(CadseRTConstants.WORKSPACE_VIEW);

		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);

		GTCadseView.workspaceView.selectNode(cadseName);
		//showMenu(selectTreeInView);

		GTCadseView.workspaceView.contextMenu(new GTTreePath(cadseName), "Delete " + cadseName).click();

		shell = new GTCadseShell("Deleted items");
		shell.close();

		cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNull(cadseDefinition);
	}


//	private void showMenu(final SWTBotTreeItem selectTreeInView) {
//		UIThreadRunnable.syncExec(selectTreeInView.display, new WidgetResult<MenuItem>() {
//			public MenuItem run() {
//				SWTBotMenu botbar = selectTreeInView.contextMenu(CadseRTConstants.CONTEXTMENU_NEW);
//				TreeItem w = selectTreeInView.widget;
//				Menu bar = w.getMenu();
//				System.out.println("TRACE : show menu");
//				if (bar != null) {
//					bar.notifyListeners(SWT.Show, new Event());
//					MenuItem[] items = bar.getItems();
//					for (MenuItem menuItem : items) {
//						System.out.println("TRACE : show menu " +SWTUtils.getText(menuItem));
//					}
//					bar.notifyListeners(SWT.Hide, new Event());
//				}
//				System.out.println("TRACE : show menu end");
//				return null;
//			}
//		});
//	}

}
