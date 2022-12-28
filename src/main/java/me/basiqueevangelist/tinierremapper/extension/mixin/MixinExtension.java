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

package me.basiqueevangelist.tinierremapper.extension.mixin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import me.basiqueevangelist.tinierremapper.TinyRemapper;
import me.basiqueevangelist.tinierremapper.api.TrClass;
import me.basiqueevangelist.tinierremapper.api.TrEnvironment;
import me.basiqueevangelist.tinierremapper.extension.mixin.hard.HardTargetMixinClassVisitor;
import org.objectweb.asm.ClassVisitor;

import me.basiqueevangelist.tinierremapper.extension.mixin.common.Logger;
import me.basiqueevangelist.tinierremapper.extension.mixin.common.Logger.Level;
import me.basiqueevangelist.tinierremapper.extension.mixin.common.data.CommonData;
import me.basiqueevangelist.tinierremapper.extension.mixin.soft.SoftTargetMixinClassVisitor;

/**
 * A extension for remapping mixin annotation.
 */
public class MixinExtension implements TinyRemapper.Extension {
	private final Logger logger;
	private final Map<Integer, List<Consumer<CommonData>>> tasks;
	private final Set<AnnotationTarget> targets;

	public enum AnnotationTarget {
		/**
		 * The string literal in mixin annotation. E.g. Mixin, Invoker, Accessor, Inject,
		 * ModifyArg, ModifyArgs, Redirect, ModifyVariable, ModifyConstant, At, Slice.
		 */
		SOFT,
		/**
		 * The field or method with mixin annotation. E.g. Shadow, Overwrite, Accessor,
		 * Invoker, Implements.
		 */
		HARD
	}

	/**
	 * Remap mixin annotation.
	 */
	public MixinExtension() {
		this(Level.WARN);
	}

	public MixinExtension(Logger.Level logLevel) {
		this(EnumSet.allOf(AnnotationTarget.class), logLevel);
	}

	public MixinExtension(Set<AnnotationTarget> targets) {
		this(targets, Level.WARN);
	}

	public MixinExtension(Set<AnnotationTarget> targets, Logger.Level logLevel) {
		this.logger = new Logger(logLevel);
		this.tasks = new HashMap<>();
		this.targets = targets;
	}

	@Override
	public void attach(TinyRemapper.Builder builder) {
		if (targets.contains(AnnotationTarget.HARD)) {
			builder.extraAnalyzeVisitor(this::analyzeVisitor).extraStateProcessor(this::stateProcessor);
		}

		if (targets.contains(AnnotationTarget.SOFT)) {
			builder.extraPreApplyVisitor(this::preApplyVisitor);
		}
	}

	/**
	 * Hard-target: Shadow, Overwrite, Accessor, Invoker, Implements.
	 */
	private ClassVisitor analyzeVisitor(int mrjVersion, String className, ClassVisitor next) {
		tasks.putIfAbsent(mrjVersion, new ArrayList<>());
		return new HardTargetMixinClassVisitor(tasks.get(mrjVersion), next);
	}

	private void stateProcessor(TrEnvironment environment) {
		CommonData data = new CommonData(environment, logger);

		for (Consumer<CommonData> task : tasks.get(environment.getMrjVersion())) {
			try {
				task.accept(data);
			} catch (RuntimeException e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * Soft-target: Mixin, Invoker, Accessor, Inject, ModifyArg, ModifyArgs, Redirect, ModifyVariable, ModifyConstant, At, Slice.
	 */
	public ClassVisitor preApplyVisitor(TrClass cls, ClassVisitor next) {
		return new SoftTargetMixinClassVisitor(new CommonData(cls.getEnvironment(), logger), next);
	}
}

