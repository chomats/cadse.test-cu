package test.fede.workspace.domain.internal;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.inGroup;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.hamcrest.Matcher;
import org.junit.Test;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;


import fr.imag.adele.cadse.test.GTCadseRTConstants;
import fr.imag.adele.cadse.test.GTCadseTestCase;
import fr.imag.adele.cadse.test.GTEclipseConstants;
import fr.imag.adele.cadse.test.GTScreenshot;
import fr.imag.adele.cadse.test.GTTestParameters;
import fr.imag.adele.cadse.test.gtmenu.GTMenu;
import fr.imag.adele.cadse.test.gttree.GTTreeNode;
import fr.imag.adele.cadse.test.gttree.GTTreePath;
import fr.imag.adele.cadse.test.gtworkbench_part.GTShell;

import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.sam.core.SamCoreCST;

/**
 * Performs the official simple tutorial
 */
public class SamCoreTest_cadseg1 extends GTCadseTestCase {

	private GTShell shell;

	/**
	 * Performs initializations for this test.
	 * Sets the timeout, velocity,...
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void test_tuto1_init() throws Exception {

		GTTestParameters.banner();
		//SWTBotPreferences.PLAYBACK_DELAY = 100;
		SWTBotPreferences.TIMEOUT = 500000;
		GTScreenshot.setScreenshotPath(System.getProperty("test.screenshotPath"));
	}

	/**
	 * Selects CADSEg in the launcher, and closes useless views. 
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void test_selection() throws Exception {

		shell = new GTShell(GTCadseRTConstants.CADSE_SELECTOR_SHELL_TITLE);
		shell.selectCadses("sam.core");
		shell.capture("image020");
		shell.close();
		welcomeView.close();
	}

	/**
	 * Sets up the data-model by creating items. 
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void test_create_sam_package() throws Exception {
	//	SWTBotPreferences.DEFAULT_POLL_DELAY = 50;
		SWTBotPreferences.TIMEOUT = 500000;
		workspaceView.show();

		// Sam package "sam package 1"
		workspaceView.contextMenu(null, GTCadseRTConstants.CONTEXTMENU_NEW, "sam package").click();
		shell = new GTShell(CadseGCST.CADSE_DEFINITION);
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText("sam package 1");
		shell.capture();
		shell.close();
		
		GTTreePath samPackage1 = new GTTreePath("sam package 1");
		

		GTTreeNode cadseWebAppModelNode = workspaceView.findTree().selectNode(samPackage1);
		
		Item cadseWebApp = cadseWebAppModelNode.getItem();
		assertNotNull(cadseWebApp);
		assertEquals("sam package 1", cadseWebApp.getName());

		// Item Type WebApp
		workspaceView.contextMenu(samPackage1, GTCadseRTConstants.CONTEXTMENU_NEW, "Service Specification").click();
		shell = new GTShell(CadseGCST.ITEM_TYPE);
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText("s1");
		shell.findField(SamCoreCST.SERVICE_SPECIFICATION_at_INTERFACE_).typeText("s1");
		shell.capture();
		shell.close();
		
		GTTreePath p1_s1 = new GTTreePath("sam package 1","s1");
		workspaceView.findTree().selectNode(p1_s1); /* Assert item has been displayed */
		workspaceView.capture();
	}

	
}
