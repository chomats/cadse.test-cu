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

import java.util.List;

import fr.imag.adele.cadse.core.ContentChangeInfo;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.delta.LinkDelta;
import fr.imag.adele.cadse.core.delta.MappingOperation;
import fr.imag.adele.cadse.core.delta.OrderOperation;
import fr.imag.adele.cadse.core.delta.SetAttributeOperation;

public class Call {
	private CallType						_type;
	private Link							l;
	private Item							destination;
	private Item							source;
	private ItemDelta					_operItem;
	private LogicalWorkspaceTransaction	_wl;
	private MappingOperation				_mappingOperation;
	private LinkDelta					_operLink;
	private SetAttributeOperation			_att;
	private OrderOperation					_orderOperation;
	private List<ItemDelta>				_loadedItems;

	public Call(CallType type, Item source, Link l, Item destination) {
		super();
		this._type = type;
		this.source = source;
		this.l = l;
		this.destination = destination;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, ItemDelta item, ContentChangeInfo[] change) {
		_wl = wl;
		_type = type;
		_operItem = item;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, ItemDelta item, MappingOperation mappingOperation) {
		_wl = wl;
		_type = type;
		_operItem = item;
		_mappingOperation = mappingOperation;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, ItemDelta item) {
		_wl = wl;
		_type = type;
		_operItem = item;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, LinkDelta link) {
		_wl = wl;
		_type = type;
		_operLink = link;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, ItemDelta item, SetAttributeOperation attOperation) {
		_wl = wl;
		_type = type;
		_operItem = item;
		_att = attOperation;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, LinkDelta link, SetAttributeOperation attOperation) {
		_wl = wl;
		_type = type;
		_operLink = link;
		_att = attOperation;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, LinkDelta link, OrderOperation orderOperation) {
		_wl = wl;
		_type = type;
		_operLink = link;
		_orderOperation = orderOperation;
	}

	public Call(LogicalWorkspaceTransaction wl, CallType type, List<ItemDelta> loadedItems) {
		_wl = wl;
		_type = type;
		_loadedItems = loadedItems;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_type.toString()).append(" (");
		switch (_type) {
			case notifyCreatedLink:
				sb.append(_operLink.toString());
				break;
			default:
				if (source != null) {
					sb.append(" source=").append(source.getId());
				}
				if (l != null) {
					sb.append(" l=").append(l);
				}
				if (destination != null) {
					sb.append(" dest=").append(destination.getId());
				}
		}

		sb.append(" )");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Call) {
			Call c = (Call) obj;
			if (c._type.equals(_type)) {
				switch (_type) {
					case notifyCreatedLink:
						return _operLink != null && c._operLink != null && _operLink.equals(c._operLink);
					default:
						return ((source == null && c.source == null) || (source != null && c.source != null && source
								.getId().equals(c.source.getId())))
								&& ((destination == null && c.destination == null) || (destination != null
										&& c.destination != null && destination.getId().equals(c.destination.getId())))
								&& ((l == null && c.l == null) || (l != null && c.l != null && l.equals(c.l)));
				}
			}

		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		switch (_type) {
			case notifyCreatedLink:
				return _type.hashCode() + _operLink.hashCode();
			default:
				return _type.hashCode() + (source != null ? source.hashCode() : 0)
						+ (destination != null ? destination.hashCode() : 0) + (l != null ? l.hashCode() : 0);
		}
	}

	public CallType getType() {
		return _type;
	}

	public Link getL() {
		return l;
	}

	public Item getDestination() {
		return destination;
	}

	public Item getSource() {
		return source;
	}

	public ItemDelta getItemDelta() {
		return _operItem;
	}

	public LogicalWorkspaceTransaction getLogicalWorkspaceTransaction() {
		return _wl;
	}

	public MappingOperation getMappingOperation() {
		return _mappingOperation;
	}

	public LinkDelta getOperLink() {
		return _operLink;
	}

	public SetAttributeOperation getAttributeOperation() {
		return _att;
	}

	public OrderOperation getOrderOperation() {
		return _orderOperation;
	}

	public List<ItemDelta> getLoadedItems() {
		return _loadedItems;
	}

}