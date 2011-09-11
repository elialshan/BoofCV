/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.feature.benchmark;

import boofcv.struct.image.ImageBase;


/**
 * Evaluates how similar the output of the original image is modified version of it.
 *
 * @author Peter Abeles
 */
public interface StabilityEvaluator<T extends ImageBase> {

	/**
	 * Extracts information from the original image
	 * @param alg Algorithm being evaluated.
	 * @param image Original image.
	 */
	void extractInitial( StabilityAlgorithm alg , T image );

	/**
	 * Extracts information from a modified image and compares
	 * to what was extracted from the original image.
	 *
	 * @param alg Algorithm being evaluated.
	 * @param image Modified image.
	 * @param scale The true change in scale from the initial image.
	 * @param theta The true change in orientation from the initial image.
	 * @return Error metrics.
	 */
	double[] evaluateImage(StabilityAlgorithm alg, T image, double scale , double theta );

	/**
	 * Names of extracted metrics.
	 * @return metric names.
	 */
	public abstract String[] getMetricNames();
}