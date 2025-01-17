/*
 * Copyright (c) 2016, 2018, Player, asie
 * Copyright (c) 2021, FabricMC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.basiqueevangelist.tinierremapper.extension.mixin.soft.annotation;

import java.util.Objects;

import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.AnnotationElement;
import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.Constant;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * The common annotation visitor for first pass.
 */
public class FirstPassAnnotationVisitor extends AnnotationNode {
	protected boolean remap;

	public FirstPassAnnotationVisitor(String descriptor, boolean remapDefault) {
		super(Constant.ASM_VERSION, descriptor);
		remap = remapDefault;
	}

	@Override
	public void visit(String name, Object value) {
		if (name.equals(AnnotationElement.REMAP)) {
			remap = Objects.requireNonNull((Boolean) value);
		}

		super.visit(name, value);
	}
}
