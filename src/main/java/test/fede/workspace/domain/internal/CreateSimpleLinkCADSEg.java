package test.fede.workspace.domain.internal;

import java.util.List;

import junit.framework.Assert;
import fr.imag.adele.cadse.cadseg.generate.GenerateCadseDefinitionModel;
import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.attributes.LinkTypeManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.DataModelManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.ItemTypeManager;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CCadse;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CItemType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CLinkType;

public class CreateSimpleLinkCADSEg extends GTCadseTestCase {
	GTShell				shell;

	private static String	packageName;

	private static String	cadseName;

	static GeneratorName	generator	= new GeneratorName();


	
	/**
	 * Performs the official simple tutorial
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void test_Initializations() throws Exception {

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
		//	SWTBotPreferences.SCREENSHOTS_DIR = System.getProperty("test.screenshotPath");
	}

	

	@Test
	public void test_selection() throws Exception {
		//workspaceView.open();
		
		/* ================================== */
		/* CADSEs selection in startup window */
		/* ================================== */

		shell =  new GTShell(GTCadseRTConstants.CADSE_SELECTOR_SHELL_TITLE);
		shell.selectCadses(GTCadseRTConstants.CADSEG_MODEL);
		shell.capture();
		shell.close();
		welcomeView.close();

	}

	@Test
	public void test_createCadse() throws Exception {

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


		packageExplorerView.show();
		packageExplorerView.findTree().selectNode("Model.Workspace." + cadseName).expand();
		packageExplorerView.findTree().selectNode("Model.Workspace." + cadseName, "sources").expand();
		packageExplorerView.capture();
		workspaceView.show();
	}

	@Test
	public void test_createTypeA_TypeB_Link_lt_a_to_b() throws Exception {
		// Item Type A
		String TypeA = "A";
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL)
				.contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu("Item type").click();

		shell = new GTShell(CadseGCST.ITEM_TYPE);
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText(TypeA);
		shell.findField(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_).check(false);
		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA).expand();
		workspaceView.capture();

		// Item Type B
		String TypeB = "B";
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL)
				.contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu("Item type").click();
		shell = new GTShell(CadseGCST.ITEM_TYPE);
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText(TypeB);
		shell.findField(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_).check(false);
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeB).expand();
		workspaceView.capture();

		// hasComp link
		final String lt_a_to_b = "a_to_b";
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA)
				.contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu("Link").click();
		shell = new GTShell(CadseGCST.LINK);
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText(lt_a_to_b);
		shell.findField(CadseGCST.LINK_lt_DESTINATION).browser(cadseName, CadseDefinitionManager.DATA_MODEL, TypeB);
		// shell.findField(WorkspaceCST.LINK_at_AGGREGATION_, true);
		// shell.findField(WorkspaceCST.LINK_at_REQUIRE_, true);
		// shell.findField(WorkspaceCST.LINK_at_PART_, true);
		Pages pages = getPages(shell);
		ItemDelta linkCreating = (ItemDelta) pages.getItem();
		assertEquals(Integer.valueOf(0), linkCreating.getAttribute(CadseGCST.LINK_at_MIN_, false));
		assertEquals(Integer.valueOf(-1), linkCreating.getAttribute(CadseGCST.LINK_at_MAX_, false));

		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA,
				lt_a_to_b);

		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);
		Item ItemTypeA = DataModelManager.getItemType(dataModel, TypeA);
		Assert.assertNotNull(ItemTypeA);
		Item ItemTypeB = DataModelManager.getItemType(dataModel, TypeB);
		Assert.assertNotNull(ItemTypeB);
		Item itemLt_a_to_b = ItemTypeManager.getAttribute(ItemTypeA, lt_a_to_b);
		Assert.assertNotNull(itemLt_a_to_b);
		Assert.assertEquals(CadseGCST.LINK, itemLt_a_to_b.getType());

		Integer min = itemLt_a_to_b.getAttribute(CadseGCST.LINK_at_MIN_);
		assertEquals(Integer.valueOf(0), min);
		Integer max = itemLt_a_to_b.getAttribute(CadseGCST.LINK_at_MAX_);
		assertEquals(Integer.valueOf(-1), max);

		int min_ = LinkManager.getMin(itemLt_a_to_b);
		assertEquals(min_, 0);
		min_ = LinkManager.getMinAttribute(itemLt_a_to_b);
		assertEquals(min_, 0);

		int max_ = LinkManager.getMax(itemLt_a_to_b);
		assertEquals(max_, -1);
		max_ = LinkManager.getMaxAttribute(itemLt_a_to_b);
		assertEquals(max_, -1);

		CCadse ccadse = GenerateCadseDefinitionModel.generateCADSE(cadseDefinition);
		CItemType citemTypeA = findType(ccadse, TypeA);
		assertNotNull(citemTypeA);
		CLinkType clt_a_to_b = findLinkType(citemTypeA, lt_a_to_b);
		assertNotNull(clt_a_to_b);
		assertEquals(clt_a_to_b.getMax(), -1);
		assertEquals(clt_a_to_b.getMin(), 0);
		assertEquals(clt_a_to_b.getDestination(), ItemTypeManager.getIdRuntime(ItemTypeB).toString());
		CItemType citemTypeB = findType(ccadse, TypeB);
		assertNotNull(citemTypeB);


	}

	private CLinkType findLinkType(CItemType citemType, String lt_a_to_b) {
		List<CLinkType> linktypes = citemType.getOutgoingLink();
		for (CLinkType linkType : linktypes) {
			if (linkType.getName().equals(lt_a_to_b))
				return linkType;
		}
		return null;
	}

	private CItemType findType(CCadse ccadse, String typeA) {
		List<CItemType> citemtypes = ccadse.getItemType();
		for (CItemType itemType : citemtypes) {
			if (itemType.getName().equals(typeA))
				return itemType;
		}
		return null;
	}

	private Pages getPages(final GTShell shell) {
		final Pages[] ret = new Pages[1];
		Runnable r = new Runnable() {
			public void run() {
				ret[0] = (Pages) shell.getSWTBotWidget().widget.getData(UIField.CADSE_MODEL_KEY);
			}
		};
		shell.getSWTBotWidget().widget.getDisplay().syncExec(r);
		return ret[0];
	}
}
