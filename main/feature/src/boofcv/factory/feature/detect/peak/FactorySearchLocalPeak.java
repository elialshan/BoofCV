/*
 * Copyright (c) 2011-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
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

package boofcv.factory.feature.detect.peak;

import boofcv.abst.feature.detect.peak.MeanShiftPeak_to_SearchLocalPeak;
import boofcv.abst.feature.detect.peak.SearchLocalPeak;
import boofcv.alg.feature.detect.peak.MeanShiftGaussianPeak;
import boofcv.alg.feature.detect.peak.MeanShiftPeak;
import boofcv.alg.feature.detect.peak.MeanShiftUniformPeak;
import boofcv.struct.image.ImageSingleBand;

/**
 * Factory for implementations of {@link SearchLocalPeak}
 *
 * @author Peter Abeles
 */
public class FactorySearchLocalPeak {

	/**
	 * Mean-shift based search with a uniform kernel
	 * @param maxIterations Maximum number of iterations.  Try 15
	 * @param convergenceTol Convergence tolerance.  try 1e-3
	 * @param imageType Type of input image
	 * @return mean-shift search
	 */
	public static <T extends ImageSingleBand>
	SearchLocalPeak<T> meanShiftUniform( int maxIterations, float convergenceTol , Class<T> imageType ) {
		MeanShiftPeak alg = new MeanShiftUniformPeak(maxIterations,convergenceTol,-1, imageType);
		return new MeanShiftPeak_to_SearchLocalPeak(alg);
	}

	/**
	 * Mean-shift based search with a Gaussian kernel
	 * @param maxIterations Maximum number of iterations.  Try 15
	 * @param convergenceTol Convergence tolerance.  try 1e-3
	 * @param imageType Type of input image
	 * @return mean-shift search
	 */
	public static <T extends ImageSingleBand>
	SearchLocalPeak<T> meanShiftGaussian( int maxIterations, float convergenceTol , Class<T> imageType) {
		MeanShiftPeak alg = new MeanShiftGaussianPeak(maxIterations,convergenceTol,-1,imageType);
		return new MeanShiftPeak_to_SearchLocalPeak(alg);
	}
}
