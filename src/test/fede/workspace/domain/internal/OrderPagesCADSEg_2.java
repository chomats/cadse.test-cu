package test.fede.workspace.domain.internal;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import fr.imag.adele.cadse.cadseg.generate.GenerateCadseDefinitionModel;
import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.attributes.LinkManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.CreationDialogManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.DataModelManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.ItemTypeManager;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.tool.loadmodel.model.jaxb.CCadse;
import fede.workspace.tool.loadmodel.model.jaxb.CItemType;
import fede.workspace.tool.loadmodel.model.jaxb.CLinkType;
import fr.imag.adele.cadse.test.GTCadseRTConstants;
import fr.imag.adele.cadse.test.GTCadseTestCase;
import fr.imag.adele.cadse.test.gtworkbench_part.GTShell;

public class OrderPagesCADSEg_2 extends GTCadseTestCase {
	GTShell				shell;

	private static String	packageName;

	private static String	cadseName;

	static GeneratorName	generator	= new GeneratorName();





	@Test
	public void test_check() throws Exception {
		// Item Type A
		String TypeA = "A";

		// Item Type B
		String TypeB = "B";

		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		while (wl.getState() != WSModelState.RUN) {
			Thread.sleep(10);
		}
		cadseName = "Cadse_" + generator.newName();
		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);
		Item ItemTypeA = DataModelManager.getItemType(dataModel, TypeA);
		Assert.assertNotNull(ItemTypeA);
		Item ItemTypeB = DataModelManager.getItemType(dataModel, TypeB);
		Assert.assertNotNull(ItemTypeB);

		Item ItemTypeA_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeA);
		Assert.assertNotNull(ItemTypeA_creationDialog);


		Item ItemTypeB_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeB);
		Assert.assertNotNull(ItemTypeB_creationDialog);


		Collection<Item> ItemTypeA_creationDialog_pagesItems = CreationDialogManager.getPages(ItemTypeA_creationDialog);
		assertEquals(6, ItemTypeA_creationDialog_pagesItems.size());
		int i = 1;
		for (Item p : ItemTypeA_creationDialog_pagesItems) {
			assertEquals("creation-page"+i, p.getName());
			i++;
		}

		Collection<Item> ItemTypeB_creationDialog_pagesItems = CreationDialogManager.getPages(ItemTypeB_creationDialog);
		assertEquals(6, ItemTypeB_creationDialog_pagesItems.size());
		i = 1;
		for (Item p : ItemTypeB_creationDialog_pagesItems) {
			assertEquals("creation-page"+i, p.getName());
			i++;
		}

		CCadse ccadse = GenerateCadseDefinitionModel.generateCADSE(cadseDefinition);
		CItemType citemTypeA = findType(ccadse, TypeA);
		assertNotNull(citemTypeA);
		CItemType citemTypeB = findType(ccadse, TypeB);
		assertNotNull(citemTypeB);


	}

	private void createCreationPage(String TypeA, String pageName, String pageTitle, String pageDescription) {
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL,
				TypeA, "creation dialog").contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu(
						GTCadseRTConstants.CONTEXTMENU_PAGE).click();
		shell = new GTShell(CadseGCST.PAGE);
		shell.findField(CadseGCST.ITEM_at_NAME_).typeText(pageName);
		shell.findField(CadseGCST.PAGE_at_TITLE_).typeText(pageTitle);
		shell.findField(CadseGCST.PAGE_at_DESCRIPTION_).typeText(pageDescription);

		shell.capture();
		shell.close();
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

	private Pages getPages(final SWTBotShell shell) {
		final Pages[] ret = new Pages[1];
		Runnable r = new Runnable() {
			public void run() {
				ret[0] = (Pages) shell.widget.getData(UIField.CADSE_MODEL_KEY);
			}
		};
		shell.widget.getDisplay().syncExec(r);
		return ret[0];
	}
}
