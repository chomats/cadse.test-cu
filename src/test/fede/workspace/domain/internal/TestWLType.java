package test.fede.workspace.domain.internal;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.CadseRootCST;

public class TestWLType {
	private static TestSenario	senario;
	private ItemType			TYPE_A;
	private ItemType			TYPE_B;
	private LinkType			LT_A_TO_B;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	static public void setUp() throws Exception {
		senario = new TestSenario();
		senario.init();

	}

	@Test
	public void testCreateType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
	}

	@Test
	public void deleteItemType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		TYPE_A.delete(true);
		senario.remove(TYPE_A);
		Assert.assertNull(senario.getItem(TYPE_A.getId()));
		Assert.assertEquals(senario.getItem(CadseRootCST.ITEM_TYPE_at_NAME_.getId()), CadseRootCST.ITEM_TYPE_at_NAME_);
		Assert.assertEquals(senario.getItem(CadseRootCST.ITEM_TYPE.getId()), CadseRootCST.ITEM_TYPE);
	}

	@Test
	public void createLinkType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		TYPE_B = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_B);
		Assert.assertNotNull(senario.getItem(TYPE_B.getId()));
		LT_A_TO_B = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
		Assert.assertNotNull(LT_A_TO_B);
		Assert.assertNotNull(TYPE_A.getOutgoingLink(CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION, LT_A_TO_B
				.getId()));
		Assert.assertNotNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getShortName()));
		Assert.assertNotNull(senario.getItem(LT_A_TO_B.getId()));

		Assert.assertNotNull(TYPE_B.getIncomingLinkType(LT_A_TO_B.getShortName()));
		Assert.assertNotNull(LT_A_TO_B.getIncomingLink(CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION, TYPE_A
				.getId()));
	}

	@Test
	public void deleteLinkType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		TYPE_B = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_B);
		Assert.assertNotNull(senario.getItem(TYPE_B.getId()));
		LT_A_TO_B = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
		Assert.assertNotNull(LT_A_TO_B);
		Assert.assertNotNull(TYPE_A.getOutgoingLink(CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION, LT_A_TO_B
				.getId()));
		Assert.assertNotNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getShortName()));
		Assert.assertNotNull(senario.getItem(LT_A_TO_B.getId()));

		Assert.assertNotNull(TYPE_B.getIncomingLinkType(LT_A_TO_B.getShortName()));
		Assert.assertNotNull(LT_A_TO_B.getIncomingLink(CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION, TYPE_A
				.getId()));

		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(TYPE_B);
		Assert.assertNotNull(LT_A_TO_B);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		Assert.assertNotNull(senario.getItem(TYPE_B.getId()));
		Assert.assertNotNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getShortName()));
		Assert.assertNotNull(senario.getItem(LT_A_TO_B.getId()));
		LT_A_TO_B.delete();
		Assert.assertNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getShortName()));
		Assert.assertNull(TYPE_A.getOutgoingLink(CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION, LT_A_TO_B
				.getId()));
		Assert.assertNull(senario.getItem(LT_A_TO_B.getId()));
		Assert.assertNull(TYPE_B.getIncomingLinkType(LT_A_TO_B.getShortName()));
	}
}
