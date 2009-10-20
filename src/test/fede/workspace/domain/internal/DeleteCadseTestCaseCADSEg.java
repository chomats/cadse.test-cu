package test.fede.workspace.domain.internal;

import junit.framework.Assert;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.core.*;
import fr.imag.adele.cadse.core.impl.*;
import fr.imag.adele.cadse.test.GTCadseRTConstants;
import fr.imag.adele.cadse.test.GTCadseTestCase;
import fr.imag.adele.cadse.test.GTScreenshot;
import fr.imag.adele.cadse.test.GTTestParameters;
import fr.imag.adele.cadse.test.gttree.GTTreeNode;
import fr.imag.adele.cadse.test.gtworkbench_part.GTShell;

public class DeleteCadseTestCaseCADSEg extends GTCadseTestCase {
	GTShell				shell;

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
		
		GTTestParameters.banner();
		//set org.eclipse.swtbot.screenshots.dir
		//if (System.getProperty("test.screenshotPath") != null)
		//	GTScreenshot.setScreenshotPath(System.getProperty("test.screenshotPath"));

		/* ================================== */
		/* CADSEs selection in startup window */
		/* ================================== */

		shell =  new GTShell(GTCadseRTConstants.CADSE_SELECTOR_SHELL_TITLE);
		shell.selectCadses(GTCadseRTConstants.CADSEG_MODEL);
		shell.capture();
		shell.close();
		welcomeView.close();
		
		/* =================== */
		/* data-model creation */
		/* =================== */

		// CADSE WebAppModel
		workspaceView.findTree().contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu(
				GTCadseRTConstants.CONTEXTMENU_NEW_CADSE_DEFINITION).click();
		shell = new GTShell(CadseGCST.CADSE_DEFINITION);
		cadseName = "Cadse_" + generator.newName();
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText( cadseName);

		packageName = "model.webapp";
		shell.findField(CadseGCST.CADSE_DEFINITION_at_PACKAGENAME_).typeText( packageName);
		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName).expand();

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

		final GTTreeNode selectTreeInView = workspaceView.findTree().selectNode(cadseName);
		Assert.assertNotNull(selectTreeInView);
		//showMenu(selectTreeInView);

		selectTreeInView.contextMenu("Delete "+cadseName).click();

		shell = new GTShell("Deleted items");
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
