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

package boofcv.abst.feature.tracker;

import boofcv.abst.feature.associate.AssociateDescription2D;
import boofcv.abst.feature.associate.ScoreAssociateHamming_B;
import boofcv.abst.feature.describe.DescribeRegionPoint;
import boofcv.abst.feature.describe.WrapDescribeBrief;
import boofcv.abst.feature.detect.interest.ConfigGeneralDetector;
import boofcv.alg.feature.associate.AssociateMaxDistanceNaive;
import boofcv.alg.feature.describe.DescribePointBrief;
import boofcv.alg.feature.describe.brief.FactoryBriefDefinition;
import boofcv.alg.feature.detect.interest.EasyGeneralFeatureDetector;
import boofcv.alg.feature.detect.interest.GeneralFeatureDetector;
import boofcv.factory.feature.describe.FactoryDescribePointAlgs;
import boofcv.factory.feature.detect.interest.FactoryDetectPoint;
import boofcv.factory.filter.blur.FactoryBlurFilter;
import boofcv.struct.feature.TupleDesc_B;
import boofcv.struct.image.ImageFloat32;

import java.util.Random;

/**
 * @author Peter Abeles
 */
public class TestDetectDescribeAssociateTwoPass extends StandardPointTrackerTwoPass<ImageFloat32> {

	public TestDetectDescribeAssociateTwoPass() {
		super(true, false);
	}

	@Override
	public PointTrackerTwoPass<ImageFloat32> createTracker() {
		DescribePointBrief<ImageFloat32> brief =
				FactoryDescribePointAlgs.brief(FactoryBriefDefinition.gaussian2(new Random(123), 16, 512),
						FactoryBlurFilter.gaussian(ImageFloat32.class, 0, 4));

		GeneralFeatureDetector<ImageFloat32,ImageFloat32> corner =
				FactoryDetectPoint.createShiTomasi(new ConfigGeneralDetector(-1, 2, 0), false, ImageFloat32.class);

		ScoreAssociateHamming_B score = new ScoreAssociateHamming_B();

		// use an association algorithm which uses the track's pose information
		AssociateDescription2D<TupleDesc_B> association =
				new AssociateMaxDistanceNaive<TupleDesc_B>(score,true,400,20);

		DescribeRegionPoint<ImageFloat32,TupleDesc_B> describe =
				new WrapDescribeBrief<ImageFloat32>(brief,ImageFloat32.class);

		EasyGeneralFeatureDetector<ImageFloat32,ImageFloat32> easy = new
				EasyGeneralFeatureDetector<ImageFloat32,ImageFloat32>(corner,ImageFloat32.class,ImageFloat32.class);

		DdaManagerGeneralPoint<ImageFloat32,ImageFloat32,TupleDesc_B> manager;
		manager = new DdaManagerGeneralPoint<ImageFloat32,ImageFloat32,TupleDesc_B>(easy,describe,2);

		DetectDescribeAssociateTwoPass<ImageFloat32,TupleDesc_B> tracker =
				new DetectDescribeAssociateTwoPass<ImageFloat32, TupleDesc_B>(manager,association,association,false);
		return tracker;
	}
}
