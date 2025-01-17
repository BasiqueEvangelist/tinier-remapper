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

package me.basiqueevangelist.tinierremapper.extension.mixin.hard;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.Annotation;
import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.CommonData;
import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.Constant;
import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.MxMember;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;

import me.basiqueevangelist.tinierremapper.extension.mixin.hard.annotation.ShadowAnnotationVisitor;

class HardTargetMixinFieldVisitor extends FieldVisitor {
	private final List<Consumer<CommonData>> tasks;
	private final MxMember field;

	private final boolean remap;
	private final List<String> targets;

	HardTargetMixinFieldVisitor(List<Consumer<CommonData>> tasks, FieldVisitor delegate, MxMember field,
								boolean remap, List<String> targets) {
		super(Constant.ASM_VERSION, delegate);
		this.tasks = Objects.requireNonNull(tasks);
		this.field = Objects.requireNonNull(field);

		this.remap = remap;
		this.targets = Objects.requireNonNull(targets);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		AnnotationVisitor av = super.visitAnnotation(descriptor, visible);

		if (Annotation.SHADOW.equals(descriptor)) {
			av = new ShadowAnnotationVisitor(tasks, av, field, remap, targets);
		}

		return av;
	}
}
