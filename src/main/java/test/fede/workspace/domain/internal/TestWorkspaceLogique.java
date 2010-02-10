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
 */
/**
 *
 */
package test.fede.workspace.domain.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseRuntime;
import java.util.UUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.internal.CadseDomainImpl;
import fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl;

/**
 * @author chomats
 *
 */
public class TestWorkspaceLogique {
	LogicalWorkspaceImpl	wl;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		CadseDomain wd = CadseCore.getCadseDomain();
		wl = new LogicalWorkspaceImpl((CadseDomainImpl) wd);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		wl.dispose();
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#setCadseName(java.lang.String[], int[])}.
	 */
	@Test
	public void testSetCadseName() {
		CadseRuntime cr = wl.createCadseRuntime("a", UUID.randomUUID(), UUID.randomUUID());
		assertArrayEquals(new String[] { "a" }, wl.getCadseName());
		assertArrayEquals(new int[] { 1 }, wl.getCadseVersion());
		if (wl.getCadseRuntime() == null) {
			fail("no cadse runtime");
		}
		for (CadseRuntime aCr : wl.getCadseRuntime()) {
			if (aCr == cr) {
				return;
			}
		}
		fail("no cadse runtime 'a' found'");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadMetaModel()}.
	 */
	@Test
	public void testLoadMetaModel() {

		Item metaItemType = wl.getItem(CadseDomain.ITEMTYPE_ID);
		assertNotNull(metaItemType);
		assertTrue(metaItemType instanceof ItemType);
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getState()}.
	 */
	@Test
	public void testGetState() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#setState(fr.imag.adele.cadse.core.WSModelState)}.
	 */
	@Test
	public void testSetState() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType)}.
	 */
	@Test
	public void testCreateItemItemTypeItemLinkType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.UUID, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateItemItemTypeItemLinkTypeUUIDStringString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#commit(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testCommitItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createItem(fr.imag.adele.cadse.core.ItemType)}.
	 */
	@Test
	public void testCreateItemItemType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createOrphanItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType)}.
	 */
	@Test
	public void testCreateOrphanItemItemTypeItemLinkType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createOrphanItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.UUID, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateOrphanItemItemTypeItemLinkTypeUUIDStringString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItem(fr.imag.adele.cadse.core.UUID)}.
	 */
	@Test
	public void testGetItemUUID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItem(java.lang.String)}.
	 */
	@Test
	public void testGetItemString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItemByShortName(fr.imag.adele.cadse.core.ItemType, java.lang.String)}.
	 */
	@Test
	public void testGetItemByShortName() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItems(java.lang.String)}.
	 */
	@Test
	public void testGetItemsString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getCadseDomain()}.
	 */
	@Test
	public void testGetWorkspaceDomain() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#canDeleteLink(fr.imag.adele.cadse.core.Link)}.
	 */
	@Test
	public void testCanDeleteLink() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#canDeleteInverseLink(fr.imag.adele.cadse.core.Link)}.
	 */
	@Test
	public void testCanDeleteInverseLink() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#removeItem(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testRemoveItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#checkUniqueName(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testCheckUniqueName() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#checkUniqueNameForRename(fr.imag.adele.cadse.core.Item, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCheckUniqueNameForRename() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getUnresolvedLinks()}.
	 */
	@Test
	public void testGetUnresolvedLinks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getUnresolvedLink(fr.imag.adele.cadse.core.UUID)}.
	 */
	@Test
	public void testGetUnresolvedLink() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getUnresolvedItem()}.
	 */
	@Test
	public void testGetUnresolvedItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItem(fr.imag.adele.cadse.core.key.Key)}.
	 */
	@Test
	public void testGetItemKey() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItems(fr.imag.adele.cadse.core.ItemType)}.
	 */
	@Test
	public void testGetItemsItemType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItems()}.
	 */
	@Test
	public void testGetItems() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#existsItem(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testExistsItemItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getKeyItem(fr.imag.adele.cadse.core.Item, java.lang.String)}.
	 */
	@Test
	public void testGetKeyItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#existsItem(fr.imag.adele.cadse.core.Item, java.lang.String)}.
	 */
	@Test
	public void testExistsItemItemString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#containsUniqueName(java.lang.String)}.
	 */
	@Test
	public void testContainsUniqueName() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#containsSpaceKey(fr.imag.adele.cadse.core.key.Key)}.
	 */
	@Test
	public void testContainsSpaceKey() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#notifieChangeEvent(fr.imag.adele.cadse.core.ChangeID, java.lang.Object[])}.
	 */
	@Test
	public void testNotifieChangeEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#setReadOnlyClosedItem(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testSetReadOnlyClosedItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#setReadOnlyOpenItem(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testSetReadOnlyOpenItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#toString()}.
	 */
	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItem(fr.imag.adele.cadse.core.UUID, fr.imag.adele.cadse.core.ItemType, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetItemUUIDItemTypeStringString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#renameUniqueName(fr.imag.adele.cadse.core.impl.internal.ItemImpl, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRenameUniqueName() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#removeItemInKeyMap(fr.imag.adele.cadse.core.impl.internal.ItemImpl)}.
	 */
	@Test
	public void testRemoveItemInKeyMap() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#addItemInKeyMap(fr.imag.adele.cadse.core.impl.internal.ItemImpl)}.
	 */
	@Test
	public void testAddItemInKeyMap() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadItem(fr.imag.adele.cadse.core.ItemDescriptionRef)}.
	 */
	@Test
	public void testLoadItemItemDescriptionRef() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadItems(java.util.Collection, boolean, boolean, boolean, java.util.Map)}.
	 */
	@Test
	public void testLoadItemsCollectionOfItemDescriptionBooleanBooleanBooleanMapOfUUIDString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadItems(java.util.Collection, java.util.Collection, boolean, boolean)}.
	 */
	@Test
	public void testLoadItemsCollectionOfURLCollectionOfProjectAssociationBooleanBoolean() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#detacheItem(fr.imag.adele.cadse.core.Item)}.
	 */
	@Test
	public void testDetacheItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadItem(fr.imag.adele.cadse.core.ItemDescription)}.
	 */
	@Test
	public void testLoadItemItemDescription() {
		fail("Not yet implemented");
	}


	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadItem(fr.imag.adele.cadse.core.ItemDescription, boolean, boolean, java.util.Map)}.
	 */
	@Test
	public void testLoadItemItemDescriptionBooleanBooleanMapOfUUIDString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItemType(fr.imag.adele.cadse.core.ItemDescriptionRef, java.util.Map)}.
	 */
	@Test
	public void testGetItemTypeItemDescriptionRefMapOfUUIDString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItemType(fr.imag.adele.cadse.core.UUID)}.
	 */
	@Test
	public void testGetItemTypeUUID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createMIT()}.
	 */
	@Test
	public void testCreateMIT() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItemTypes()}.
	 */
	@Test
	public void testGetItemTypes() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createItemType(java.lang.String, fr.imag.adele.cadse.core.ItemType, int, fr.imag.adele.cadse.core.UUID, java.lang.String, java.lang.String, boolean, boolean)}.
	 */
	@Test
	public void testCreateItemType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#registerItemType(fr.imag.adele.cadse.core.ItemType)}.
	 */
	@Test
	public void testRegisterItemType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getTypeAny()}.
	 */
	@Test
	public void testGetTypeAny() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#delete()}.
	 */
	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getItemTypeByName(java.lang.String)}.
	 */
	@Test
	public void testGetItemTypeByName() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createUnresolvedLinkType(java.lang.String, fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.ItemType)}.
	 */
	@Test
	public void testCreateUnresolvedLinkType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#loadContentManager(fr.imag.adele.cadse.core.impl.internal.ItemImpl)}.
	 */
	@Test
	public void testLoadContentManager() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#createWorkingCopy()}.
	 */
	@Test
	public void testCreateWorkingCopy() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#commit(fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy)}.
	 */
	@Test
	public void testCommitWorkingLogiqueCopy() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#load(fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy)}.
	 */
	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getHandleIdentifier(fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.impl.internal.MementoIdentifier)}.
	 */
	@Test
	public void testGetHandleIdentifierItemMementoIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getWithHandleIdentifier(java.lang.String)}.
	 */
	@Test
	public void testGetWithHandleIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl#getHandleIdentifier(fr.imag.adele.cadse.core.impl.internal.AbstractItem)}.
	 */
	@Test
	public void testGetHandleIdentifierAbstractItem() {
		fail("Not yet implemented");
	}

}
